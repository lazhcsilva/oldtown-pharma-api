package br.com.oldtown.pharma.product.dto.request;

import br.com.oldtown.pharma.product.entity.enums.ProductPresentation;
import br.com.oldtown.pharma.product.entity.enums.TherapeuticClass;
import br.com.oldtown.pharma.product.entity.enums.UnitMeasure;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateMedicineDetailsRequest(

        @NotBlank(message = "Anvisa registration is required")
        @Size(max = 50)
        String anvisaRegistration,

        @NotBlank(message = "Dosage is required")
        String dosage,

        @NotNull(message = "A unit of measurement is required")
        UnitMeasure unitMeasure,

        @NotNull(message = "Inform if product is controlled")
        Boolean controlled,

        @NotNull(message = "Presentation is required")
        ProductPresentation presentation,

        @NotBlank(message = "Brand name is required")
        String brandName,

        @NotBlank(message = "Generic name is required")
        String genericName,

        @NotNull(message = "Inform if product have expiration")
        Boolean expirationControlRequired,

        @NotNull(message = "Therapeutic class is required")
        TherapeuticClass therapeuticClass
) {
}
