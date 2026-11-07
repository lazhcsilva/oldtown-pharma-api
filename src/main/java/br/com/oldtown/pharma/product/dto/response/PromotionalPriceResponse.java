package br.com.oldtown.pharma.product.dto.response;

import br.com.oldtown.pharma.product.entity.enums.PromotionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PromotionalPriceResponse(
        Long id,
        String name,
        String manufacturer,
        BigDecimal originalPrice,
        BigDecimal promotionalPrice,
        BigDecimal currentPrice,
        LocalDateTime promotionStartDate,
        LocalDateTime promotionEndDate,
        PromotionStatus status
) {
}
