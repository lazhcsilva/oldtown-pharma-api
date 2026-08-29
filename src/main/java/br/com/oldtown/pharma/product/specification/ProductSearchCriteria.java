package br.com.oldtown.pharma.product.specification;

import br.com.oldtown.pharma.product.entity.enums.ProductType;
import br.com.oldtown.pharma.product.entity.enums.TherapeuticClass;

import java.math.BigDecimal;

public record ProductSearchCriteria(
        String name,
        ProductType type,
        TherapeuticClass therapeuticClass,
        Boolean active,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        Long categoryId
) {
}
