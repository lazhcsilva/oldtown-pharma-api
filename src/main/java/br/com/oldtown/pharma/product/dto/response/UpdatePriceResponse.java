package br.com.oldtown.pharma.product.dto.response;

import java.math.BigDecimal;

public record UpdatePriceResponse(
        BigDecimal costPrice,
        BigDecimal originalPrice,
        BigDecimal promotionalPrice
) {
}
