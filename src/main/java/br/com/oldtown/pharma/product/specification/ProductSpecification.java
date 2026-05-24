package br.com.oldtown.pharma.product.specification;

import br.com.oldtown.pharma.product.entity.Product;
import br.com.oldtown.pharma.product.entity.ProductType;
import br.com.oldtown.pharma.product.entity.TherapeuticClass;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {

    public static Specification<Product> hasName(String name) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                        "%" + name.toLowerCase() + "%");
    }

    public static Specification<Product> hasType(ProductType type) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("productType"), type));
    }

    public static Specification<Product> hasTherapeuticClass(TherapeuticClass therapeuticClass) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.join("medicineDetails", JoinType.LEFT)
                        .get("therapeuticClass"), therapeuticClass));
    }

    public static Specification<Product> isActive(Boolean active) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("active"), active));
    }

    public static Specification<Product> priceGreaterThanOrEqual(BigDecimal minPrice) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
    }

    public static Specification<Product> priceLessThanOrEqual(BigDecimal maxPrice) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
    }

    public static Specification<Product> hasCategory(Long categoryId) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("category").get("id"), categoryId));
    }
}
