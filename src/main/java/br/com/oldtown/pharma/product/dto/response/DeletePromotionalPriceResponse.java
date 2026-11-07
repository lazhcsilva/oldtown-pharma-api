package br.com.oldtown.pharma.product.dto.response;

import br.com.oldtown.pharma.product.entity.enums.PromotionStatus;

import java.math.BigDecimal;

public record DeletePromotionalPriceResponse(
        Long id,
        String name,
        String manufacturer,
        BigDecimal originalPrice,
        BigDecimal promotionalPrice,
        BigDecimal currentPrice,
        PromotionStatus status
) {
}
