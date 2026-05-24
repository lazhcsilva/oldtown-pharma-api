package br.com.oldtown.pharma.product.dto.request;

import br.com.oldtown.pharma.product.entity.ProductType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateProductRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Description is required")
        String description,

        @NotBlank(message = "Manufacturer is required")
        String manufacturer,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "The value must be greater than zero.")
        @DecimalMax(value = "10000.0", message = "Very high value")
        @Digits(integer = 10, fraction = 2, message = "Invalid format (2 decimals)")
        BigDecimal price,

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
                if (productType == ProductType.MEDICINE) {
                        return medicineDetails != null;
                }
                return true;
        }
}
