package br.com.oldtown.pharma.product.dto.request;

import br.com.oldtown.pharma.product.entity.enums.ProductType;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateProductRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 150)
        String name,

        @NotBlank(message = "Description is required")
        @Size(max = 255)
        String description,

        @NotBlank(message = "Manufacturer is required")
        @Size(max = 150)
        String manufacturer,

        @NotNull(message = "Cost price is required")
        @DecimalMin(value = "0.0", inclusive = false)
        @Digits(integer = 8, fraction = 2)
        BigDecimal costPrice,

        @NotNull(message = "Original price is required")
        @DecimalMin(value = "0.0", inclusive = false)
        @Digits(integer = 8, fraction = 2)
        BigDecimal originalPrice,

        @Positive
        @Digits(integer = 8, fraction = 2)
        BigDecimal promotionalPrice,

        LocalDateTime promotionStartDate,

        LocalDateTime promotionEndDate,

        @NotBlank(message = "Barcode is required")
        String barcode,

        @NotNull(message = "Category is required")
        @Positive
        Long categoryId,

        @NotNull(message = "Product type is required")
        ProductType productType,

        @Valid
        CreateMedicineDetailsRequest medicineDetails
) {
        @AssertTrue(message = "Medicine details must be provided only for medicines")
        public boolean isMedicineDetailsValid() {
                if (productType == null) return true;
                return productType == ProductType.MEDICINE ? medicineDetails != null : medicineDetails == null;
        }

        @AssertTrue(message = "Promotion requires a price and an end date after the start date")
        public boolean isPromotionValid() {
                if (promotionalPrice == null) {
                        return promotionStartDate == null && promotionEndDate == null;
                }
                return promotionStartDate != null && promotionEndDate != null
                        && promotionEndDate.isAfter(promotionStartDate);
        }

        @AssertTrue(message = "Original price must be greater than cost price")
        public boolean isPriceValid() {
                return originalPrice == null || costPrice == null || originalPrice.compareTo(costPrice) > 0;
        }

        @AssertTrue(message = "Promotional price must be greater than cost price")
        public boolean isPromotionalPriceValid() {
                return promotionalPrice == null || costPrice == null || promotionalPrice.compareTo(costPrice) > 0;
        }
}
