package br.com.oldtown.pharma.product.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreatePromotionalPriceRequest(
        BigDecimal promotionalPrice,
        LocalDateTime promotionStartDate,
        LocalDateTime promotionEndDate
) {
}
