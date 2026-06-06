package br.com.oldtown.pharma.product.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record UpdateProductRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "The value must be greater than zero.")
        @DecimalMax(value = "10000.0", message = "Very high value")
        @Digits(integer = 10, fraction = 2, message = "Invalid format (2 decimals)")
        BigDecimal price

) {
}
