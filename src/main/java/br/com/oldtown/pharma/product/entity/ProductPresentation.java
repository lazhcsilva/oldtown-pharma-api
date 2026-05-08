package br.com.oldtown.pharma.product.entity;

public enum ProductPresentation {
    TABLET("Tablet"),
    CAPSULE("Capsule"),
    SYRUP("Syrup"),
    SOLUTION("Solution"),
    CREAM("Cream"),
    OINTMENT("Ointment"),
    INJECTION("Injection"),
    DROPS("Drops"),
    SPRAY("Spray"),
    SUSPENSION("Suspension");

    private final String description;

    ProductPresentation(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
