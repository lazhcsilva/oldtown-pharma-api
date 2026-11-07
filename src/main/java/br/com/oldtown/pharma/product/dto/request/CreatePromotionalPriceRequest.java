package br.com.oldtown.pharma.product.dto.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreatePromotionalPriceRequest(
        @NotNull(message = "The price cannot be null")
        @Positive(message = "The price must be greater than zero.")
        @Digits(integer = 6, fraction = 2, message = "Maximum of 6 integer digits and 2 decimal places")
        BigDecimal promotionalPrice,
        @NotBlank(message = "Promotion start date is required")
        String promotionStartDate,
        @NotBlank(message = "Promotion end date is required")
        String promotionEndDate
) {
}
