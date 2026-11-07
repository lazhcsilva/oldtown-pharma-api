package br.com.oldtown.pharma.product.specification;

import br.com.oldtown.pharma.category.entity.Category;
import br.com.oldtown.pharma.category.repository.CategoryRepository;
import br.com.oldtown.pharma.product.entity.Product;
import br.com.oldtown.pharma.product.entity.enums.ProductType;
import br.com.oldtown.pharma.product.entity.enums.PromotionStatus;
import br.com.oldtown.pharma.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.sql.init.mode=never")
class ProductSpecificationTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category category;

    @BeforeEach
    void setUp() {
        category = categoryRepository.save(new Category("Test category", "Price filter tests"));
    }

    @ParameterizedTest
    @CsvSource({
            "NONE, 30.00",
            "ACTIVE, 15.00",
            "FUTURE, 30.00",
            "EXPIRED, 30.00",
            "MISSING_START, 30.00",
            "MISSING_END, 30.00",
            "MISSING_PRICE, 30.00"
    })
    void shouldFilterByCurrentPriceForEachPromotionState(String promotion, String expectedPrice) {
        Product product = product("Test product", "30.00");
        LocalDateTime now = LocalDateTime.now();
        if (!promotion.equals("NONE")) {
            product.setPromotionalPrice(new BigDecimal("15.00"));
            product.setPromotionStartDate(now.minusDays(1));
            product.setPromotionEndDate(now.plusDays(1));
            switch (promotion) {
                case "FUTURE" -> {
                    product.setPromotionStartDate(now.plusDays(1));
                    product.setPromotionEndDate(now.plusDays(2));
                }
                case "EXPIRED" -> {
                    product.setPromotionStartDate(now.minusDays(2));
                    product.setPromotionEndDate(now.minusDays(1));
                }
                case "MISSING_START" -> product.setPromotionStartDate(null);
                case "MISSING_END" -> product.setPromotionEndDate(null);
                case "MISSING_PRICE" -> product.setPromotionalPrice(null);
                default -> { }
            }
        }
        productRepository.saveAndFlush(product);
        BigDecimal price = new BigDecimal(expectedPrice);

        assertThat(productRepository.findAll(ProductSpecification.priceGreaterThanOrEqual(price)))
                .extracting(Product::getId).containsExactly(product.getId());
        assertThat(productRepository.findAll(ProductSpecification.priceLessThanOrEqual(price)))
                .extracting(Product::getId).containsExactly(product.getId());
        assertThat(productRepository.findAll(
                ProductSpecification.priceGreaterThanOrEqual(price.add(new BigDecimal("0.01")))))
                .isEmpty();
        assertThat(productRepository.findAll(
                ProductSpecification.priceLessThanOrEqual(price.subtract(new BigDecimal("0.01")))))
                .isEmpty();
    }

    @Test
    void shouldCombinePriceBoundsBeforePagination() {
        productRepository.save(product("A", "10.00"));
        productRepository.save(product("B", "20.00"));
        productRepository.save(product("C", "30.00"));
        productRepository.saveAndFlush(product("D", "40.00"));
        var specification = ProductSpecification.priceGreaterThanOrEqual(new BigDecimal("20.00"))
                .and(ProductSpecification.priceLessThanOrEqual(new BigDecimal("30.00")));

        var firstPage = productRepository.findAll(specification,
                PageRequest.of(0, 1, Sort.by("name")));
        var secondPage = productRepository.findAll(specification,
                PageRequest.of(1, 1, Sort.by("name")));

        assertThat(firstPage.getTotalElements()).isEqualTo(2);
        assertThat(firstPage.getContent()).extracting(Product::getName).containsExactly("B");
        assertThat(secondPage.getContent()).extracting(Product::getName).containsExactly("C");
    }

    @Test
    void shouldSortByCalculatedStatusBeforePagination() {
        LocalDateTime now = LocalDateTime.now();
        Product expired = product("Expired", "30.00");
        expired.setPromotionalPrice(new BigDecimal("15.00"));
        expired.setPromotionStartDate(now.minusDays(2));
        expired.setPromotionEndDate(now.minusDays(1));
        Product active = product("Active", "30.00");
        active.setPromotionalPrice(new BigDecimal("15.00"));
        active.setPromotionStartDate(now.minusDays(1));
        active.setPromotionEndDate(now.plusDays(1));
        Product scheduled = product("Scheduled", "30.00");
        scheduled.setPromotionalPrice(new BigDecimal("15.00"));
        scheduled.setPromotionStartDate(now.plusDays(1));
        scheduled.setPromotionEndDate(now.plusDays(2));
        productRepository.saveAllAndFlush(java.util.List.of(
                scheduled, product("None", "30.00"), expired, active));

        var specification = ProductSpecification.priceLessThanOrEqual(new BigDecimal("30.00"));
        var firstPage = productRepository.findAll(specification,
                PageRequest.of(0, 2, Sort.by("status")));
        var secondPage = productRepository.findAll(specification,
                PageRequest.of(1, 2, Sort.by("status")));

        assertThat(firstPage.getTotalElements()).isEqualTo(4);
        assertThat(firstPage.getContent()).extracting(Product::getStatus)
                .containsExactly(PromotionStatus.ACTIVE, PromotionStatus.EXPIRED);
        assertThat(secondPage.getContent()).extracting(Product::getStatus)
                .containsExactly(PromotionStatus.NONE, PromotionStatus.SCHEDULED);
    }

    private Product product(String name, String originalPrice) {
        Product product = new Product();
        product.setName(name);
        product.setDescription("Test product description");
        product.setManufacturer("Test manufacturer");
        product.setCostPrice(new BigDecimal("5.00"));
        product.setOriginalPrice(new BigDecimal(originalPrice));
        product.setActive(true);
        product.setSku("SKU-" + name);
        product.setBarcode("BARCODE-" + name);
        product.setProductType(ProductType.COMMON);
        product.setCategory(category);
        return product;
    }
}
