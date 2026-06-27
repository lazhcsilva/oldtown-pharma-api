package br.com.oldtown.pharma.product.dto.request;

import java.math.BigDecimal;

public record UpdatePriceRequest(
        BigDecimal costPrice,
        BigDecimal originalPrice,
        BigDecimal promotionalPrice
) {
}
