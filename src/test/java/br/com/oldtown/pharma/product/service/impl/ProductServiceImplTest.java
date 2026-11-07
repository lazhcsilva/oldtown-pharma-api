package br.com.oldtown.pharma.product.service.impl;

import br.com.oldtown.pharma.category.entity.Category;
import br.com.oldtown.pharma.category.repository.CategoryRepository;
import br.com.oldtown.pharma.product.dto.request.*;
import br.com.oldtown.pharma.product.dto.response.*;
import br.com.oldtown.pharma.product.entity.Product;
import br.com.oldtown.pharma.product.entity.enums.ProductType;
import br.com.oldtown.pharma.product.entity.enums.ProductPresentation;
import br.com.oldtown.pharma.product.entity.enums.UnitMeasure;
import br.com.oldtown.pharma.product.entity.enums.TherapeuticClass;
import br.com.oldtown.pharma.product.entity.enums.PromotionStatus;
import br.com.oldtown.pharma.product.mapper.ProductMapper;
import br.com.oldtown.pharma.product.repository.ProductRepository;
import br.com.oldtown.pharma.product.service.SkuGeneratorService;
import br.com.oldtown.pharma.product.specification.ProductSearchCriteria;
import br.com.oldtown.pharma.shared.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {
    private static final Long PRODUCT_ID = 1L;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Mock private ProductRepository productRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private ProductMapper mapper;
    @Mock private SkuGeneratorService skuGenerator;

    private ProductServiceImpl service;
    private Product product;

    @BeforeEach
    void setUp() {
        service = new ProductServiceImpl(productRepository, categoryRepository, skuGenerator, mapper);
        product = new Product();
        product.setId(PRODUCT_ID);
        product.setName("Test product");
        product.setActive(true);
        product.setCostPrice(new BigDecimal("10.00"));
        product.setOriginalPrice(new BigDecimal("30.00"));
    }

    @Test
    void shouldFindProductById() {
        givenProductExists();
        ProductResponse expected = response();
        when(mapper.toResponse(product)).thenReturn(expected);

        assertThat(service.findById(PRODUCT_ID)).isEqualTo(expected);
    }

    @Test
    void shouldFindProductByName() {
        when(productRepository.findByName(product.getName())).thenReturn(Optional.of(product));
        ProductResponse expected = response();
        when(mapper.toResponse(product)).thenReturn(expected);

        assertThat(service.findByName(product.getName())).isEqualTo(expected);
    }

    @Test
    void shouldRejectUnknownProductName() {
        when(productRepository.findByName("Missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findByName("Missing")).isInstanceOf(NotFoundException.class);
        verifyNoInteractions(mapper);
    }

    @ParameterizedTest
    @ValueSource(strings = {"find", "update", "price", "createPromotion", "deletePromotion", "delete"})
    void shouldRejectMissingProductWithoutWriting(String operation) {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> {
            switch (operation) {
                case "find" -> service.findById(PRODUCT_ID);
                case "update" -> service.updateBasicData(PRODUCT_ID, new UpdateProductRequest("New", "Description"));
                case "price" -> service.updatePrice(PRODUCT_ID, new UpdatePriceRequest(new BigDecimal("20")));
                case "createPromotion" -> service.createPromotionalPrice(PRODUCT_ID, promotionRequest(-1, 1));
                case "deletePromotion" -> service.deletePromotionalPrice(PRODUCT_ID);
                case "delete" -> service.delete(PRODUCT_ID);
                default -> throw new AssertionError(operation);
            }
        }).isInstanceOf(NotFoundException.class);
        verify(productRepository, never()).save(any());
        verifyNoInteractions(mapper, skuGenerator);
    }

    @Test
    void shouldReturnMappedSearchPageWithPaginationMetadata() {
        Pageable pageable = PageRequest.of(1, 2);
        ProductSearchCriteria criteria = new ProductSearchCriteria(null, null, null, null, null, null, null);
        when(productRepository.findAll(org.mockito.ArgumentMatchers.<Specification<Product>>any(), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(product), pageable, 3));
        ProductResponse expected = response();
        when(mapper.toResponse(product)).thenReturn(expected);

        Page<ProductResponse> result = service.search(criteria, pageable);

        assertThat(result.getContent()).containsExactly(expected);
        assertThat(result.getNumber()).isEqualTo(1);
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
        verify(productRepository, times(1)).findAll(org.mockito.ArgumentMatchers.<Specification<Product>>any(), eq(pageable));
    }

    @Test
    void shouldReturnEmptySearchPage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(productRepository.findAll(org.mockito.ArgumentMatchers.<Specification<Product>>any(), eq(pageable))).thenReturn(Page.empty(pageable));

        assertThat(service.search(new ProductSearchCriteria(null, null, null, null, null, null, null), pageable))
                .isEmpty();
        verifyNoInteractions(mapper);
    }

    @ParameterizedTest
    @EnumSource(ProductType.class)
    void shouldCreateProductWithCategoryAndGeneratedSku(ProductType type) {
        CreateProductRequest request = createRequest(type);
        Category category = new Category("General", "Description");
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));
        if (type == ProductType.COMMON) {
            when(mapper.toCommonProductEntity(request)).thenReturn(product);
        } else {
            when(mapper.toMedicineProductEntity(request)).thenReturn(product);
        }
        when(skuGenerator.generate(product)).thenReturn("TEST-SKU");
        when(productRepository.save(product)).thenReturn(product);
        ProductResponse expected = response();
        when(mapper.toResponse(product)).thenReturn(expected);

        assertThat(service.create(request)).isEqualTo(expected);
        assertThat(product.getCategory()).isSameAs(category);
        assertThat(product.getSku()).isEqualTo("TEST-SKU");
        verify(productRepository).save(product);
    }

    @Test
    void shouldRejectDuplicateProductCreation() {
        CreateProductRequest request = createRequest(ProductType.COMMON);
        when(productRepository.existsByNameIgnoreCase(request.name())).thenReturn(true);

        assertThatThrownBy(() -> service.create(request)).isInstanceOf(ConflictException.class);
        verify(productRepository, never()).save(any());
        verifyNoInteractions(categoryRepository, mapper, skuGenerator);
    }

    @Test
    void shouldRejectCreationWithMissingCategory() {
        CreateProductRequest request = createRequest(ProductType.COMMON);
        when(categoryRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(request)).isInstanceOf(NotFoundException.class);
        verify(productRepository, never()).save(any());
        verifyNoInteractions(mapper, skuGenerator);
    }

    @Test
    void shouldUpdateBasicDataAndRegenerateSku() {
        givenProductExists();
        UpdateProductRequest request = new UpdateProductRequest("New name", "New description");
        doAnswer(invocation -> {
            product.setName(request.name());
            product.setDescription(request.description());
            return product;
        }).when(mapper).toUpdateEntity(product, request);
        when(skuGenerator.generate(product)).thenReturn("NEW-SKU");
        when(productRepository.save(product)).thenReturn(product);
        ProductResponse expected = response();
        when(mapper.toResponse(product)).thenReturn(expected);

        assertThat(service.updateBasicData(PRODUCT_ID, request)).isEqualTo(expected);
        assertThat(product.getName()).isEqualTo(request.name());
        assertThat(product.getDescription()).isEqualTo(request.description());
        assertThat(product.getSku()).isEqualTo("NEW-SKU");
        verify(productRepository).save(product);
    }

    @Test
    void shouldRejectUpdateWithDuplicateName() {
        givenProductExists();
        UpdateProductRequest request = new UpdateProductRequest("Duplicate", "Description");
        when(productRepository.existsByNameIgnoreCaseAndIdNot(request.name(), PRODUCT_ID)).thenReturn(true);

        assertThatThrownBy(() -> service.updateBasicData(PRODUCT_ID, request)).isInstanceOf(ConflictException.class);
        assertThat(product.getName()).isEqualTo("Test product");
        verify(productRepository, never()).save(any());
        verifyNoInteractions(mapper, skuGenerator);
    }

    @Test
    void shouldUpdatePriceAndReturnPreviousValue() {
        givenProductExists();

        UpdatePriceResponse result = service.updatePrice(PRODUCT_ID, new UpdatePriceRequest(new BigDecimal("10.01")));

        assertThat(result.id()).isEqualTo(PRODUCT_ID);
        assertThat(result.previousPrice()).isEqualByComparingTo("30.00");
        assertThat(result.newPrice()).isEqualByComparingTo("10.01");
        assertThat(product.getOriginalPrice()).isEqualByComparingTo("10.01");
        verify(productRepository).save(product);
    }

    @ParameterizedTest
    @ValueSource(strings = {"9.99", "10.00"})
    void shouldRejectPriceAtOrBelowCost(String price) {
        givenProductExists();

        assertThatThrownBy(() -> service.updatePrice(PRODUCT_ID, new UpdatePriceRequest(new BigDecimal(price))))
                .isInstanceOf(InvalidPriceException.class);
        assertThat(product.getOriginalPrice()).isEqualByComparingTo("30.00");
        verify(productRepository, never()).save(any());
    }

    @ParameterizedTest
    @CsvSource({"-1, 1, ACTIVE", "1, 2, SCHEDULED"})
    void shouldCreatePromotionWithDatesAndStatus(int startOffset, int endOffset, PromotionStatus status) {
        givenProductExists();
        CreatePromotionalPriceRequest request = promotionRequest(startOffset, endOffset);
        when(productRepository.save(product)).thenReturn(product);
        PromotionalPriceResponse expected = new PromotionalPriceResponse(PRODUCT_ID, "Test product", null,
                new BigDecimal("30"), request.promotionalPrice(), request.promotionalPrice(),
                LocalDateTime.parse(request.promotionStartDate(), DATE_FORMAT),
                LocalDateTime.parse(request.promotionEndDate(), DATE_FORMAT), status);
        when(mapper.toResponsePromotionalPrice(product)).thenReturn(expected);

        assertThat(service.createPromotionalPrice(PRODUCT_ID, request)).isEqualTo(expected);
        assertThat(product.getStatus()).isEqualTo(status);
        assertThat(product.getPromotionalPrice()).isEqualByComparingTo(request.promotionalPrice());
        assertThat(product.getPromotionStartDate()).isEqualTo(expected.promotionStartDate());
        assertThat(product.getPromotionEndDate()).isEqualTo(expected.promotionEndDate());
        verify(productRepository).save(product);
    }

    @ParameterizedTest
    @ValueSource(strings = {"9.99", "10.00"})
    void shouldRejectPromotionalPriceAtOrBelowCost(String price) {
        givenProductExists();
        CreatePromotionalPriceRequest dates = promotionRequest(-1, 1);
        CreatePromotionalPriceRequest request = new CreatePromotionalPriceRequest(new BigDecimal(price),
                dates.promotionStartDate(), dates.promotionEndDate());

        assertThatThrownBy(() -> service.createPromotionalPrice(PRODUCT_ID, request))
                .isInstanceOf(InvalidPriceException.class);
        assertThat(product.getPromotionalPrice()).isNull();
        verify(productRepository, never()).save(any());
    }

    @ParameterizedTest
    @EnumSource(value = PromotionStatus.class, names = {"ACTIVE", "SCHEDULED"})
    void shouldRejectOverlappingPromotion(PromotionStatus status) {
        givenProductExists();
        givenPromotion(status);

        assertThatThrownBy(() -> service.createPromotionalPrice(PRODUCT_ID, promotionRequest(-1, 1)))
                .isInstanceOf(ConflictException.class);
        verify(productRepository, never()).save(any());
    }

    @Test
    void shouldRejectPromotionEndingBeforeStart() {
        givenProductExists();

        assertThatThrownBy(() -> service.createPromotionalPrice(PRODUCT_ID, promotionRequest(2, 1)))
                .isInstanceOf(BadRequestException.class);
        assertThat(product.getPromotionalPrice()).isNull();
        verify(productRepository, never()).save(any());
    }

    @Test
    void shouldRejectPromotionWithEqualDates() {
        givenProductExists();

        assertThatThrownBy(() -> service.createPromotionalPrice(PRODUCT_ID, promotionRequest(1, 1)))
                .isInstanceOf(BadRequestException.class);
        verify(productRepository, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldRejectPromotionChangesForInactiveProduct(boolean create) {
        givenProductExists();
        product.setActive(false);

        assertThatThrownBy(() -> {
            if (create) service.createPromotionalPrice(PRODUCT_ID, promotionRequest(-1, 1));
            else service.deletePromotionalPrice(PRODUCT_ID);
        }).isInstanceOf(ConflictException.class);
        verify(productRepository, never()).save(any());
    }

    @ParameterizedTest
    @EnumSource(value = PromotionStatus.class, names = {"ACTIVE", "SCHEDULED"})
    void shouldClearPromotionAndRestoreOriginalPrice(PromotionStatus status) {
        givenProductExists();
        givenPromotion(status);
        when(productRepository.save(product)).thenReturn(product);
        DeletePromotionalPriceResponse expected = new DeletePromotionalPriceResponse(PRODUCT_ID, "Test product",
                null, new BigDecimal("30"), null, new BigDecimal("30"), PromotionStatus.NONE);
        when(mapper.toResponseDeletePromotionalPrice(product)).thenReturn(expected);

        assertThat(service.deletePromotionalPrice(PRODUCT_ID)).isEqualTo(expected);
        assertThat(product.getPromotionalPrice()).isNull();
        assertThat(product.getPromotionStartDate()).isNull();
        assertThat(product.getPromotionEndDate()).isNull();
        assertThat(product.getStatus()).isEqualTo(PromotionStatus.NONE);
        assertThat(product.getCurrentPrice()).isEqualByComparingTo("30");
        verify(productRepository).save(product);
    }

    @ParameterizedTest
    @EnumSource(value = PromotionStatus.class, names = {"NONE", "EXPIRED"})
    void shouldRejectRemovalWithoutActiveOrScheduledPromotion(PromotionStatus status) {
        givenProductExists();
        givenPromotion(status);

        assertThatThrownBy(() -> service.deletePromotionalPrice(PRODUCT_ID)).isInstanceOf(ConflictException.class);
        verify(productRepository, never()).save(any());
    }

    @Test
    void shouldSoftDeleteProductWithoutRemovingHistory() {
        givenProductExists();

        service.delete(PRODUCT_ID);

        assertThat(product.isActive()).isFalse();
        assertThat(product.getId()).isEqualTo(PRODUCT_ID);
        assertThat(product.getOriginalPrice()).isEqualByComparingTo("30");
        verify(productRepository).save(product);
        verify(productRepository, never()).delete(any(Product.class));
    }

    @Test
    void shouldReplaceExpiredPromotionWithoutReadingCurrentPriceFirst() {
        givenProductExists();
        givenPromotion(PromotionStatus.EXPIRED);
        CreatePromotionalPriceRequest request = promotionRequest(-1, 1);
        when(productRepository.save(product)).thenReturn(product);

        service.createPromotionalPrice(PRODUCT_ID, request);

        assertThat(product.getStatus()).isEqualTo(PromotionStatus.ACTIVE);
        assertThat(product.getPromotionEndDate())
                .isEqualTo(LocalDateTime.parse(request.promotionEndDate(), DATE_FORMAT));
        verify(productRepository).save(product);
    }

    @Test
    void shouldRejectNewPromotionAlreadyExpired() {
        givenProductExists();

        assertThatThrownBy(() -> service.createPromotionalPrice(PRODUCT_ID, promotionRequest(-2, -1)))
                .isInstanceOf(BadRequestException.class);
        assertThat(product.getPromotionalPrice()).isNull();
        verify(productRepository, never()).save(any());
    }

    @ParameterizedTest
    @CsvSource(value = {"NULL|01/01/2030 10:00:00", "01/01/2030 10:00:00|NULL",
            "invalid|01/01/2030 10:00:00", "01/01/2030 10:00:00|invalid",
            "31/02/2030 10:00:00|01/03/2030 10:00:00"},
            delimiter = '|', nullValues = "NULL")
    void shouldRejectMissingOrMalformedPromotionDates(String start, String end) {
        givenProductExists();

        assertThatThrownBy(() -> service.createPromotionalPrice(PRODUCT_ID,
                new CreatePromotionalPriceRequest(new BigDecimal("20"), start, end)))
                .isInstanceOf(BadRequestException.class);
        assertThat(product.getPromotionalPrice()).isNull();
        verify(productRepository, never()).save(any());
    }

    private void givenPromotion(PromotionStatus status) {
        if (status == PromotionStatus.NONE) return;
        LocalDateTime now = LocalDateTime.now();
        product.setPromotionalPrice(new BigDecimal("20"));
        product.setPromotionStartDate(status == PromotionStatus.SCHEDULED ? now.plusDays(1) : now.minusDays(2));
        product.setPromotionEndDate(status == PromotionStatus.EXPIRED ? now.minusDays(1) : now.plusDays(2));
    }

    private void givenProductExists() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
    }

    private CreateProductRequest createRequest(ProductType type) {
        CreateMedicineDetailsRequest details = type == ProductType.MEDICINE
                ? new CreateMedicineDetailsRequest("TEST-123", "500", UnitMeasure.MG, false,
                        ProductPresentation.TABLET, "Brand", "Generic", true, TherapeuticClass.ANALGESIC)
                : null;
        return new CreateProductRequest("Test product", "Description", "Manufacturer", new BigDecimal("10"),
                new BigDecimal("30"), null, null, null, "123456", 2L, type, details);
    }

    private CreatePromotionalPriceRequest promotionRequest(int startOffset, int endOffset) {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        return new CreatePromotionalPriceRequest(new BigDecimal("20"),
                now.plusDays(startOffset).format(DATE_FORMAT), now.plusDays(endOffset).format(DATE_FORMAT));
    }

    private ProductResponse response() {
        return new ProductResponse(PRODUCT_ID, "Test product", "Description", "Manufacturer", new BigDecimal("10"),
                new BigDecimal("30"), null, new BigDecimal("30"), null, null, PromotionStatus.NONE,
                true, "TEST-SKU", "123456", null, ProductType.COMMON, null);
    }
}
