package br.com.oldtown.pharma.product.mapper;

import br.com.oldtown.pharma.category.mapper.CategoryMapper;
import br.com.oldtown.pharma.product.dto.CreateProductRequest;
import br.com.oldtown.pharma.product.dto.ProductResponse;
import br.com.oldtown.pharma.product.dto.UpdateProductRequest;
import br.com.oldtown.pharma.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(
        componentModel = "spring",
        uses = {
                MedicineDetailsMapper.class
        }
)
public interface ProductMapper {

    ProductResponse toResponse(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "medicineDetails", ignore = true)
    @Mapping(target = "sku", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product toCommonProductEntity(CreateProductRequest request);

    Product toMedicineProductEntity(CreateProductRequest request);

    Product toUpdateEntity(@MappingTarget Product product, UpdateProductRequest request);

}
