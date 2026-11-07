package br.com.oldtown.pharma.product.entity.enums;

public enum ProductType {
    COMMON("Common"),
    MEDICINE("Medicine");

    private final String description;

    ProductType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
