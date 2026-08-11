package br.com.oldtown.pharma.product.service;

import br.com.oldtown.pharma.product.dto.request.CreatePromotionalPriceRequest;
import br.com.oldtown.pharma.product.dto.request.UpdatePriceRequest;
import br.com.oldtown.pharma.product.dto.request.CreateProductRequest;
import br.com.oldtown.pharma.product.dto.response.ProductResponse;
import br.com.oldtown.pharma.product.dto.request.UpdateProductRequest;
import br.com.oldtown.pharma.product.dto.response.PromotionalPriceResponse;
import br.com.oldtown.pharma.product.dto.response.UpdatePriceResponse;
import br.com.oldtown.pharma.product.specification.ProductSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Page<ProductResponse> search(ProductSearchCriteria criteria, Pageable pageable);
    ProductResponse findByName(String name);
    ProductResponse findById(Long id);
    ProductResponse create(CreateProductRequest request);
    PromotionalPriceResponse createPromotionalPrice(Long id, CreatePromotionalPriceRequest request);
    ProductResponse updateBasicData(Long id, UpdateProductRequest request);
    UpdatePriceResponse updatePrice(Long id, UpdatePriceRequest request);
    void delete(Long id);
}
