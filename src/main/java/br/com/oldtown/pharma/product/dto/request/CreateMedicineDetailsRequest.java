package br.com.oldtown.pharma.product.dto.request;

import br.com.oldtown.pharma.product.entity.ProductPresentation;
import br.com.oldtown.pharma.product.entity.TherapeuticClass;
import br.com.oldtown.pharma.product.entity.UnitMeasure;
import jakarta.validation.constraints.NotNull;

public record CreateMedicineDetailsRequest(

        @NotNull(message = "Anvisa registration is required")
        String anvisaRegistration,

        @NotNull(message = "Dosage is required")
        String dosage,

        @NotNull(message = "A unit of measurement is required")
        UnitMeasure unitMeasure,

        @NotNull(message = "Inform if product is controlled")
        Boolean controlled,

        @NotNull(message = "Presentation is required")
        ProductPresentation presentation,

        @NotNull(message = "Brand name is required")
        String brandName,

        @NotNull(message = "Generic name is required")
        String genericName,

        @NotNull(message = "Inform if product have expiration")
        Boolean expirationControlRequired,

        @NotNull(message = "Therapeutic class is required")
        TherapeuticClass therapeuticClass
) {
}
