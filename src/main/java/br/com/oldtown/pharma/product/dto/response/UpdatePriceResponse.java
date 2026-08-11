package br.com.oldtown.pharma.product.dto.response;

import java.math.BigDecimal;

public record UpdatePriceResponse(
        Long id,
        BigDecimal previousPrice,
        BigDecimal newPrice
) {
}
