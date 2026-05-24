package br.com.oldtown.pharma.product.entity;

public enum UnitMeasure {
    UNIT("Unit"),
    MG("Milligram"),
    ML("Milliliter"),
    MCG_ML("Microgram per Milliliter"),
    IU("International Unit");

    private final String description;

    UnitMeasure(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
