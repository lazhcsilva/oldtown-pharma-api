package br.com.oldtown.pharma.product.dto.request;

import java.math.BigDecimal;

public record ChangePriceRequest(BigDecimal oldPrice, BigDecimal newPrice) {
}
