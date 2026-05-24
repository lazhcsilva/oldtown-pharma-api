package br.com.oldtown.pharma.product.dto;

import br.com.oldtown.pharma.product.entity.ProductType;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        String description,
        String manufacturer,
        BigDecimal price,
        Boolean active,
        String sku,
        String barcode,
        String category,
        ProductType productType,
        MedicineDetailsResponse medicineDetails
) {
}
