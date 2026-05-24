package br.com.oldtown.pharma.product.mapper;

import br.com.oldtown.pharma.product.dto.CreateProductRequest;
import br.com.oldtown.pharma.product.dto.ProductResponse;
import br.com.oldtown.pharma.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "category", source = "category.name")
    ProductResponse toResponse(Product product);

    Product toCommonProductEntity(CreateProductRequest product);

    Product toMedicineProductEntity(CreateProductRequest product);

    Product toUpdateEntity(Product product);

}
