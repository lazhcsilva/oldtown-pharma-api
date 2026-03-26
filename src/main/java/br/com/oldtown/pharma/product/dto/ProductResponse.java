package br.com.oldtown.pharma.product.dto;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        String description,
        String manufacturer,
        BigDecimal price,
        boolean controlled,
        boolean requiresPrescription,
        String category
) {
}
