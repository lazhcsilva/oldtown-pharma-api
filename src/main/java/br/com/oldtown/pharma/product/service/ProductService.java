package br.com.oldtown.pharma.product.service;

import br.com.oldtown.pharma.product.dto.CreateProductRequest;
import br.com.oldtown.pharma.product.dto.ProductResponse;
import br.com.oldtown.pharma.product.dto.UpdateProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Page<ProductResponse> getAll(Pageable pageable);
    ProductResponse findByName(String name);
    ProductResponse findById(Long id);
    ProductResponse create(CreateProductRequest request);
    ProductResponse update(Long id, UpdateProductRequest request);
    void delete(Long id);
}
