package br.com.oldtown.pharma.product.entity.enums;

public enum UnitMeasure {
    UNIT("Unit"),
    MG("Milligram"),
    ML("Milliliter"),
    MCG_ML("Microgram per Milliliter"),
    IU("International Unit"),
    MCG("Microgram");

    private final String description;

    UnitMeasure(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
