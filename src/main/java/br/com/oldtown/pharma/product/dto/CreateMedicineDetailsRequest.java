package br.com.oldtown.pharma.product.dto;

import br.com.oldtown.pharma.product.entity.ProductPresentation;
import br.com.oldtown.pharma.product.entity.TherapeuticClass;
import br.com.oldtown.pharma.product.entity.UnitMeasure;
import jakarta.validation.constraints.NotNull;

public record CreateMedicineDetailsRequest(

        @NotNull(message = "Anvisa registration is required")
        String anvisaRegistration,

        @NotNull(message = "Dosage is required")
        String dosage,

        @NotNull(message = "")
        UnitMeasure unitMeasure,

        @NotNull(message = "Inform if product is controlled")
        Boolean controlled,

        @NotNull(message = "Presentation is required")
        ProductPresentation presentation,

        @NotNull(message = "")
        String brandName,

        @NotNull(message = "")
        String genericName,

        @NotNull(message = "Inform if product have expiration")
        Boolean expirationControlRequired,

        @NotNull(message = "")
        TherapeuticClass therapeuticClass
) {
}
