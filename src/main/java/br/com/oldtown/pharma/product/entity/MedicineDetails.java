package br.com.oldtown.pharma.product.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "medicine_details")
public class MedicineDetails {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private String dosage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProductPresentation presentation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnitMeasure unitMeasure;

    @Column(nullable = false)
    private String brandName;

    @Column(nullable = false)
    private String genericName;

    @Column(name = "anvisa_registration", length = 50)
    private String anvisaRegistration;

    @Column(nullable = false)
    private boolean expirationControlRequired;

    @Enumerated(EnumType.STRING)
    @Column(name = "therapeutic_class", length = 100)
    private TherapeuticClass therapeuticClass;

    protected MedicineDetails() {
    }

    public MedicineDetails(String dosage, ProductPresentation presentation, UnitMeasure unitMeasure,
                           String brandName, String genericName, String anvisaRegistration,
                           boolean expirationControlRequired, TherapeuticClass therapeuticClass) {
        this.dosage = dosage;
        this.presentation = presentation;
        this.unitMeasure = unitMeasure;
        this.brandName = brandName;
        this.genericName = genericName;
        this.anvisaRegistration = anvisaRegistration;
        this.expirationControlRequired = expirationControlRequired;
        this.therapeuticClass = therapeuticClass;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public ProductPresentation getPresentation() {
        return presentation;
    }

    public void setPresentation(ProductPresentation presentation) {
        this.presentation = presentation;
    }

    public UnitMeasure getUnitMeasure() {
        return unitMeasure;
    }

    public void setUnitMeasure(UnitMeasure unitMeasure) {
        this.unitMeasure = unitMeasure;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getGenericName() {
        return genericName;
    }

    public void setGenericName(String genericName) {
        this.genericName = genericName;
    }

    public String getAnvisaRegistration() {
        return anvisaRegistration;
    }

    public void setAnvisaRegistration(String anvisaRegistration) {
        this.anvisaRegistration = anvisaRegistration;
    }

    public boolean isExpirationControlRequired() {
        return expirationControlRequired;
    }

    public void setExpirationControlRequired(boolean expirationControlRequired) {
        this.expirationControlRequired = expirationControlRequired;
    }

    public TherapeuticClass getTherapeuticClass() {
        return therapeuticClass;
    }

    public void setTherapeuticClass(TherapeuticClass therapeuticClass) {
        this.therapeuticClass = therapeuticClass;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
