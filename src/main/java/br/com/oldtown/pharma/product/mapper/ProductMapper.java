package br.com.oldtown.pharma.product.mapper;

import br.com.oldtown.pharma.category.entity.Category;
import br.com.oldtown.pharma.product.dto.*;
import br.com.oldtown.pharma.product.entity.MedicineDetails;
import br.com.oldtown.pharma.product.entity.Product;
import br.com.oldtown.pharma.shared.utils.EnumResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getManufacturer(),
                product.getPrice(),
                product.isActive(),
                product.getSku(),
                product.getBarcode(),
                product.getCategory().getName(),
                product.getProductType(),
                toMedicineDetailsResponse(product.getMedicineDetails())
        );
    }

    public Product toCommonProductEntity(CreateProductRequest request, Category category) {
        return new Product(
                request.name(),
                request.description(),
                request.manufacturer(),
                request.price(),
                request.barcode(),
                request.productType(),
                category
        );
    }

    public Product toMedicineProductEntity(CreateProductRequest request, Category category) {
        Product product = new Product(
                request.name(),
                request.description(),
                request.manufacturer(),
                request.price(),
                request.barcode(),
                request.productType(),
                category
        );

        MedicineDetails details = toMedicineDetailsEntity(request.medicineDetails());

        product.setMedicineDetails(details);

        return product;
    }

    private MedicineDetails toMedicineDetailsEntity(CreateMedicineDetailsRequest request) {
        return new MedicineDetails(
                request.dosage(),
                request.presentation(),
                request.unitMeasure(),
                request.brandName(),
                request.genericName(),
                request.anvisaRegistration(),
                request.expirationControlRequired(),
                request.therapeuticClass()
        );
    }

    public void updateEntity(Product product, UpdateProductRequest request, Category category) {
        product.setName(request.name());
        product.setDescription(request.description());
        product.setManufacturer(request.manufacturer());
        product.setPrice(request.price());
        product.setActive(request.active());
        product.setCategory(category);
        product.setUpdatedAt(LocalDateTime.now());
    }

    private <E extends Enum<E>> EnumResponse<E> toEnum(E e) {
        if (e == null) {
            return null;
        }

        return new EnumResponse<>(e.name(), formatLabel(e.name()));
    }

    private MedicineDetailsResponse toMedicineDetailsResponse(MedicineDetails details) {
        if (details == null) {
            return null;
        }

        return new MedicineDetailsResponse(
                details.getDosage(),
                toEnum(details.getPresentation()),
                toEnum(details.getUnitMeasure()),
                details.getBrandName(),
                details.getGenericName(),
                details.getAnvisaRegistration(),
                details.isExpirationControlRequired(),
                toEnum(details.getTherapeuticClass())
        );
    }

    private String formatLabel(String value) {
        return value.replace("_", " ").toLowerCase();
    }

    private String generateSku() {
        return UUID.randomUUID().toString();
    }
}
