package br.com.oldtown.pharma.product.dto.response;

import br.com.oldtown.pharma.category.dto.CategoryResponse;
import br.com.oldtown.pharma.product.entity.enums.ProductType;
import br.com.oldtown.pharma.product.entity.enums.PromotionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
        Long id,
        String name,
        String description,
        String manufacturer,
        BigDecimal costPrice,
        BigDecimal originalPrice,
        BigDecimal promotionalPrice,
        BigDecimal currentPrice,
        LocalDateTime promotionStartDate,
        LocalDateTime promotionEndDate,
        PromotionStatus status,
        Boolean active,
        String sku,
        String barcode,
        CategoryResponse category,
        ProductType productType,
        MedicineDetailsResponse medicineDetails
) {
}
