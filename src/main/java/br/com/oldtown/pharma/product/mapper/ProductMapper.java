package br.com.oldtown.pharma.product.mapper;

import br.com.oldtown.pharma.category.entity.Category;
import br.com.oldtown.pharma.product.dto.CreateProductRequest;
import br.com.oldtown.pharma.product.dto.ProductResponse;
import br.com.oldtown.pharma.product.dto.UpdateProductRequest;
import br.com.oldtown.pharma.product.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getManufacturer(),
                product.getPrice(),
                product.isControlled(),
                product.isRequiresPrescription(),
                product.getCategory().getName());
    }

    public Product toEntity(CreateProductRequest request, Category category) {
        return new Product(
                request.name(),
                request.description(),
                request.manufacturer(),
                request.price(),
                request.active(),
                request.controlled(),
                request.requiresPrescription(),
                category
        );
    }

    public void updateEntity(Product product, UpdateProductRequest request, Category category) {
        product.setName(request.name());
        product.setDescription(request.description());
        product.setManufacturer(request.manufacturer());
        product.setPrice(request.price());
        product.setControlled(request.controlled());
        product.setActive(request.active());
        product.setRequiresPrescription(request.requiresPrescription());
        product.setCategory(category);
    }
}
