package br.com.oldtown.pharma.product.dto.response;

import br.com.oldtown.pharma.category.dto.CategoryResponse;
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
        CategoryResponse category,
        ProductType productType,
        MedicineDetailsResponse medicineDetails
) {
}
