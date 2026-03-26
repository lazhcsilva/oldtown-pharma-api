package br.com.oldtown.pharma.product.mapper;

import br.com.oldtown.pharma.product.dto.ProductResponse;
import br.com.oldtown.pharma.product.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product product) {
        if (product == null) {
            return null;
        }

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
}
