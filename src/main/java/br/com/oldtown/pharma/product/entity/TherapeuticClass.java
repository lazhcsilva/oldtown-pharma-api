package br.com.oldtown.pharma.product.entity;

public enum TherapeuticClass {
    ANALGESIC("Analgesic"),
    ANTI_INFLAMMATORY("Anti Inflammatory"),
    ANTIBIOTIC("Antibiotic"),
    ANTIHISTAMINE("Antihistamine"),
    ANTIPYRETIC("Antipyretic"),
    ANTIHYPERTENSIVE("Antihypertensive"),
    LIPID_LOWERING("Lipid Lowering"),
    ANTIDIABETIC("Antidiabetic"),
    ANXIOLYTIC("Anxiolytic"),
    ANTIDEPRESSANT("Antidepressant"),
    BRONCHODILATOR("Bronchodilator"),
    ANTACID("Antacid"),
    HORMONE("Hormone");;

    private final String description;

    TherapeuticClass(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
