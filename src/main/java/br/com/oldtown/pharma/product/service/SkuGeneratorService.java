package br.com.oldtown.pharma.product.service;

import br.com.oldtown.pharma.product.entity.MedicineDetails;
import br.com.oldtown.pharma.product.entity.Product;
import br.com.oldtown.pharma.product.entity.enums.ProductType;
import br.com.oldtown.pharma.shared.utils.Util;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SkuGeneratorService {
    public String generate(Product product) {
        String category = Util.getPrefix(product.getCategory().getName());
        String name = Util.getPrefix(product.getName()).toUpperCase();

        String suffix = UUID.randomUUID()
                .toString()
                .substring(0, 4)
                .toUpperCase();

        if (product.getProductType() == ProductType.MEDICINE
            && product.getMedicineDetails() != null) {
            MedicineDetails md = product.getMedicineDetails();
            String dosage = Util.getPrefix(md.getDosage()).toUpperCase();
            String presentation = Util.getPrefix(md.getPresentation().name()).toUpperCase();

            return String.format(
                    "%s-%s-%s-%s-%s",
                    category,
                    name,
                    dosage,
                    presentation,
                    suffix
            ).toUpperCase();
        }

        return String.format("%s-%s-%s", category, name, suffix).toUpperCase();
    }
}
