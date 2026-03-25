package br.com.oldtown.pharma.category.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateCategoryRequest(
        @NotBlank(message = "Name is required")
        String name,
        String description
) {
}
