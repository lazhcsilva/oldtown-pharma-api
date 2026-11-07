package br.com.oldtown.pharma.product.mapper;

import br.com.oldtown.pharma.product.dto.request.CreateProductRequest;
import br.com.oldtown.pharma.product.dto.response.DeletePromotionalPriceResponse;
import br.com.oldtown.pharma.product.dto.response.ProductResponse;
import br.com.oldtown.pharma.product.dto.request.UpdateProductRequest;
import br.com.oldtown.pharma.product.dto.response.PromotionalPriceResponse;
import br.com.oldtown.pharma.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
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
    DeletePromotionalPriceResponse toResponseDeletePromotionalPrice(Product product);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "manufacturer", source = "manufacturer")
    @Mapping(target = "costPrice", source = "costPrice")
    @Mapping(target = "originalPrice", source = "originalPrice")
    @Mapping(target = "promotionalPrice", source = "promotionalPrice")
    @Mapping(target = "promotionStartDate", source = "promotionStartDate")
    @Mapping(target = "promotionEndDate", source = "promotionEndDate")
    @Mapping(target = "barcode", source = "barcode")
    @Mapping(target = "productType", source = "productType")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "medicineDetails", ignore = true)
    @Mapping(target = "sku", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product toCommonProductEntity(CreateProductRequest request);

    @InheritConfiguration(name = "toCommonProductEntity")
    @Mapping(target = "medicineDetails", source = "medicineDetails")
    Product toMedicineProductEntity(CreateProductRequest request);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    Product toUpdateEntity(@MappingTarget Product product, UpdateProductRequest request);
}
