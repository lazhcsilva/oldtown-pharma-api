package br.com.oldtown.pharma.product.service.impl;

import br.com.oldtown.pharma.category.entity.Category;
import br.com.oldtown.pharma.category.repository.CategoryRepository;
import br.com.oldtown.pharma.product.dto.request.CreatePromotionalPriceRequest;
import br.com.oldtown.pharma.product.dto.request.UpdatePriceRequest;
import br.com.oldtown.pharma.product.dto.request.CreateProductRequest;
import br.com.oldtown.pharma.product.dto.response.ProductResponse;
import br.com.oldtown.pharma.product.dto.request.UpdateProductRequest;
import br.com.oldtown.pharma.product.dto.response.PromotionalPriceResponse;
import br.com.oldtown.pharma.product.entity.Product;
import br.com.oldtown.pharma.product.entity.ProductType;
import br.com.oldtown.pharma.product.mapper.ProductMapper;
import br.com.oldtown.pharma.product.repository.ProductRepository;
import br.com.oldtown.pharma.product.service.ProductService;
import br.com.oldtown.pharma.product.service.SkuGeneratorService;
import br.com.oldtown.pharma.product.specification.ProductSearchCriteria;
import br.com.oldtown.pharma.product.specification.ProductSpecification;
import br.com.oldtown.pharma.shared.exception.ConflictException;
import br.com.oldtown.pharma.shared.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final SkuGeneratorService skuGeneratorService;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository,
                              SkuGeneratorService skuGeneratorService, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.skuGeneratorService = skuGeneratorService;
        this.productMapper = productMapper;
    }


    @Override
    public Page<ProductResponse> search(ProductSearchCriteria criteria, Pageable pageable) {
        Specification<Product> spec = (root, query, criteriaBuilder) ->
                criteriaBuilder.conjunction();

        if (criteria.name() != null && !criteria.name().isBlank()) {
            spec = spec.and(ProductSpecification.hasName(criteria.name()));
        }

        if (criteria.type() != null) {
            spec = spec.and(ProductSpecification.hasType(criteria.type()));
        }

        if (criteria.therapeuticClass() != null) {
            spec = spec.and(ProductSpecification.hasTherapeuticClass(criteria.therapeuticClass()));
        }

        if (criteria.active() != null) {
            spec = spec.and(ProductSpecification.isActive(criteria.active()));
        }

        if (criteria.minPrice() != null) {
            spec = spec.and(ProductSpecification.priceGreaterThanOrEqual(criteria.minPrice()));
        }

        if (criteria.maxPrice() != null) {
            spec = spec.and(ProductSpecification.priceLessThanOrEqual(criteria.maxPrice()));
        }

        if (criteria.categoryId() != null) {
            spec = spec.and(ProductSpecification.hasCategory(criteria.categoryId()));
        }

        Page<Product> result = productRepository.findAll(spec, pageable);

        System.out.println(result.getContent());

        return productRepository.findAll(spec, pageable).map(productMapper::toResponse);
    }

    @Override
    public ProductResponse findByName(String name) {
        return productRepository.findByName(name)
                .map(productMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Product not found with name: " + name));
    }

    @Override
    public ProductResponse findById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
    }

    @Override
    public ProductResponse create(CreateProductRequest request) {
        if (productRepository.existsByNameIgnoreCase(request.name())) {
            throw new ConflictException("Product already exists.");
        }

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new NotFoundException("Category not found."));

        Product product = request.productType() == ProductType.COMMON
                ? productMapper.toCommonProductEntity(request)
                : productMapper.toMedicineProductEntity(request);

        product.setCategory(category);
        product.setSku(skuGeneratorService.generate(product));
        product.setCurrentPrice(product.getCurrentPrice());

        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    public PromotionalPriceResponse createPromotionalPrice(Long id, CreatePromotionalPriceRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found."));

        product.setPromotionalPrice(request.promotionalPrice());
        product.setPromotionStartDate(request.promotionStartDate());
        product.setPromotionEndDate(request.promotionEndDate());

        return productMapper.toResponsePromotionalPrice(productRepository.save(product));
    }

    @Override
    public ProductResponse updateBasicData(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found."));

        if (productRepository.existsByNameIgnoreCaseAndIdNot(request.name(), id)) {
            throw new ConflictException("Product already exists.");
        }

        productMapper.toUpdateEntity(product, request);
        product.setSku(skuGeneratorService.generate(product));

        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse updatePrice(Long id, UpdatePriceRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found."));
        return null;
    }

    @Override
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found."));
        productRepository.delete(product);
    }
}
