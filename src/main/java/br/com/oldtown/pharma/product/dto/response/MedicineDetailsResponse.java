package br.com.oldtown.pharma.product.dto.response;

import br.com.oldtown.pharma.product.entity.enums.ProductPresentation;
import br.com.oldtown.pharma.product.entity.enums.TherapeuticClass;
import br.com.oldtown.pharma.product.entity.enums.UnitMeasure;
import br.com.oldtown.pharma.shared.utils.EnumResponse;

public record MedicineDetailsResponse(
        String dosage,
        EnumResponse<ProductPresentation> presentation,
        EnumResponse<UnitMeasure> unitMeasure,
        String brandName,
        String genericName,
        String anvisaRegistration,
        Boolean expirationControlRequired,
        EnumResponse<TherapeuticClass> therapeuticClass
) {
}
