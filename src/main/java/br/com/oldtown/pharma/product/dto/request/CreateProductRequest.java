package br.com.oldtown.pharma.product.dto.request;

import br.com.oldtown.pharma.product.entity.enums.ProductType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateProductRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Description is required")
        String description,

        @NotBlank(message = "Manufacturer is required")
        String manufacturer,

        @NotNull(message = "Cost price is required")
        @DecimalMin(value = "0.0", inclusive = false)
        BigDecimal costPrice,

        @NotNull(message = "Original price is required")
        @DecimalMin(value = "0.0", inclusive = false)
        BigDecimal originalPrice,

        BigDecimal promotionalPrice,

        LocalDateTime promotionStartDate,

        LocalDateTime promotionEndDate,

        @NotBlank(message = "Barcode is required")
        String barcode,

        @NotNull(message = "Category is required")
        Long categoryId,

        @NotNull(message = "Product type is required")
        ProductType productType,

        CreateMedicineDetailsRequest medicineDetails
) {
        @AssertTrue(message = "Medicine details are required when product type is MEDICINE")
        public boolean isMedicineDetailsValid() {
                return productType != null
                        && productType != ProductType.MEDICINE
                        || medicineDetails != null;
        }

        @AssertTrue(message = "Promotion dates are required when promotional price is informed")
        public boolean isPromotionValid() {
                if (promotionalPrice != null) {
                        return promotionStartDate != null
                                && promotionEndDate != null
                                && !promotionEndDate.isBefore(promotionStartDate);
                }
                return true;
        }

        @AssertTrue(message = "Original price must be greater than cost price")
        public boolean isPriceValid() {
                return originalPrice.compareTo(costPrice) > 0;
        }
}
