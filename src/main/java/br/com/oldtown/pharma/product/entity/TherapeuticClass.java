package br.com.oldtown.pharma.product.entity;

public enum TherapeuticClass {
    ANALGESIC("Analgesic"),
    ANTI_INFLAMMATORY("Anti Inflammatory"),
    ANTIBIOTIC("Antibiotic"),
    ANTIHISTAMINE("Antihistamine"),
    ANTIPYRETIC("Antipyretic");

    private final String description;

    TherapeuticClass(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
