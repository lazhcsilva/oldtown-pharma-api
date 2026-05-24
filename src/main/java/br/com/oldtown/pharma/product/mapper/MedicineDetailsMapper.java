package br.com.oldtown.pharma.product.mapper;

import br.com.oldtown.pharma.product.dto.CreateMedicineDetailsRequest;
import br.com.oldtown.pharma.product.dto.MedicineDetailsResponse;
import br.com.oldtown.pharma.product.entity.MedicineDetails;
import br.com.oldtown.pharma.shared.utils.EnumResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MedicineDetailsMapper {

    MedicineDetails toEntity(CreateMedicineDetailsRequest request);

    @Mapping(
            target = "presentation",
            expression = "java(toEnum(details.getPresentation()))"
    )
    MedicineDetailsResponse toResponse(MedicineDetails details);

    default <E extends Enum<E>>EnumResponse<E> toEnum(E e) {
        if (e == null) {
            return null;
        }

        return new EnumResponse<>(
                e.name(),
                e.name().replace("_", " ").toLowerCase()
        );
    }
}
