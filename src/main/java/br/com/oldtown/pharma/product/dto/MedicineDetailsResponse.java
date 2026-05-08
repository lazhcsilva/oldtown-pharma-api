package br.com.oldtown.pharma.product.dto;

import br.com.oldtown.pharma.product.entity.ProductPresentation;
import br.com.oldtown.pharma.product.entity.TherapeuticClass;
import br.com.oldtown.pharma.product.entity.UnitMeasure;
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
