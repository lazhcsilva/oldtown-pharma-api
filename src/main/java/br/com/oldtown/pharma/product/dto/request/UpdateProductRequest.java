package br.com.oldtown.pharma.product.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record UpdateProductRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Description is required")
        String description

) {
}
