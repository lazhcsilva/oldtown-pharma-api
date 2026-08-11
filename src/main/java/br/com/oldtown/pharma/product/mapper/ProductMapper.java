package br.com.oldtown.pharma.product.mapper;

import br.com.oldtown.pharma.product.dto.request.CreateProductRequest;
import br.com.oldtown.pharma.product.dto.request.UpdatePriceRequest;
import br.com.oldtown.pharma.product.dto.response.ProductResponse;
import br.com.oldtown.pharma.product.dto.request.UpdateProductRequest;
import br.com.oldtown.pharma.product.dto.response.PromotionalPriceResponse;
import br.com.oldtown.pharma.product.dto.response.UpdatePriceResponse;
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
    PromotionalPriceResponse toResponsePromotionalPrice(Product product);

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
