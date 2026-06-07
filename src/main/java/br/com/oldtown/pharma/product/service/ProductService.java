package br.com.oldtown.pharma.product.service;

import br.com.oldtown.pharma.product.dto.request.ChangePriceRequest;
import br.com.oldtown.pharma.product.dto.request.CreateProductRequest;
import br.com.oldtown.pharma.product.dto.response.ProductResponse;
import br.com.oldtown.pharma.product.dto.request.UpdateProductRequest;
import br.com.oldtown.pharma.product.specification.ProductSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Page<ProductResponse> search(ProductSearchCriteria criteria, Pageable pageable);
    ProductResponse findByName(String name);
    ProductResponse findById(Long id);
    ProductResponse create(CreateProductRequest request);
    ProductResponse updateBasicData(Long id, UpdateProductRequest request);
    ProductResponse changePrice(Long id, ChangePriceRequest request);
    void delete(Long id);
}
