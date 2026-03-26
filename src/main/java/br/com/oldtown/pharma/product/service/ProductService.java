package br.com.oldtown.pharma.product.service;

import br.com.oldtown.pharma.product.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Page<ProductResponse> getALl(Pageable pageable);
}
