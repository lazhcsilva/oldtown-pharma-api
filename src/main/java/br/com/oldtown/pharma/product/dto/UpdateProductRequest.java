package br.com.oldtown.pharma.product.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record UpdateProductRequest(
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

        @NotNull(message = "Inform if product is required")
        Boolean controlled,

        @NotNull(message = "Inform if product requires prescription")
        Boolean requiresPrescription,

        @NotNull(message = "The property is required")
        Boolean active,

        @NotNull(message = "Category is required")
        long categoryID
) {
}
