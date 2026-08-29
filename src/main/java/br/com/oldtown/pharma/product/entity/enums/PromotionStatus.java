package br.com.oldtown.pharma.product.entity.enums;

public enum PromotionStatus {
    SCHEDULED("Scheduled"),
    ACTIVE("Active"),
    EXPIRED("Expired"),
    NONE("None");

    private final String description;

    PromotionStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
