package br.com.oldtown.pharma.product.entity;

import br.com.oldtown.pharma.product.entity.enums.PromotionStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ProductPromotionTest {
    private static final LocalDateTime START = LocalDateTime.of(2030, 1, 10, 10, 0);
    private static final LocalDateTime END = START.plusHours(1);

    @ParameterizedTest
    @CsvSource({
            "-1, SCHEDULED, 30.00",
            "0, ACTIVE, 20.00",
            "1800, ACTIVE, 20.00",
            "3600, ACTIVE, 20.00",
            "3601, EXPIRED, 30.00"
    })
    void shouldCalculateStatusAndPriceAtPromotionBoundaries(long seconds, PromotionStatus status, String price) {
        Product product = promotionalProduct();
        LocalDateTime now = START.plusSeconds(seconds);

        assertThat(product.getStatus(now)).isEqualTo(status);
        assertThat(product.getCurrentPrice(now)).isEqualByComparingTo(price);
    }

    @ParameterizedTest
    @ValueSource(strings = {"PRICE", "START", "END", "REVERSED"})
    void shouldIgnoreIncompleteOrReversedPromotion(String invalidField) {
        Product product = promotionalProduct();
        switch (invalidField) {
            case "PRICE" -> product.setPromotionalPrice(null);
            case "START" -> product.setPromotionStartDate(null);
            case "END" -> product.setPromotionEndDate(null);
            case "REVERSED" -> product.setPromotionEndDate(START.minusHours(1));
            default -> throw new AssertionError(invalidField);
        }

        assertThat(product.getStatus(START)).isEqualTo(PromotionStatus.NONE);
        assertThat(product.getCurrentPrice(START)).isEqualByComparingTo("30.00");
    }

    @Test
    void shouldReturnNoneWithoutPromotion() {
        Product product = new Product();
        product.setOriginalPrice(new BigDecimal("30.00"));

        assertThat(product.getStatus(START)).isEqualTo(PromotionStatus.NONE);
        assertThat(product.getCurrentPrice(START)).isEqualByComparingTo("30.00");
    }

    @Test
    void shouldCalculateStatusBeforePriceAndWithoutMutatingPromotion() {
        Product product = promotionalProduct();

        assertThat(product.getStatus(END.plusSeconds(1))).isEqualTo(PromotionStatus.EXPIRED);
        assertThat(product.getCurrentPrice(END.plusSeconds(1))).isEqualByComparingTo("30.00");
        assertThat(product.getStatus(START)).isEqualTo(PromotionStatus.ACTIVE);
        assertThat(product.getPromotionStartDate()).isEqualTo(START);
        assertThat(product.getPromotionEndDate()).isEqualTo(END);
        assertThat(product.getPromotionalPrice()).isEqualByComparingTo("20.00");
    }

    private Product promotionalProduct() {
        Product product = new Product();
        product.setOriginalPrice(new BigDecimal("30.00"));
        product.setPromotionalPrice(new BigDecimal("20.00"));
        product.setPromotionStartDate(START);
        product.setPromotionEndDate(END);
        return product;
    }
}
