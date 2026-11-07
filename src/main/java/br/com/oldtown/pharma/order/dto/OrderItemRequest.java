package br.com.oldtown.pharma.order.dto;

import jakarta.validation.constraints.*;

public record OrderItemRequest(
        @NotNull @Positive Long productId,
        @NotNull @Positive @Max(1000000) Integer quantity
) {}
