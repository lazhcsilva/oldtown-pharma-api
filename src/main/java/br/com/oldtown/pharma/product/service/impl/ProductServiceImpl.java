package br.com.oldtown.pharma.product.service.impl;

import br.com.oldtown.pharma.category.entity.Category;
import br.com.oldtown.pharma.category.repository.CategoryRepository;
import br.com.oldtown.pharma.product.dto.request.*;
import br.com.oldtown.pharma.product.dto.response.DeletePromotionalPriceResponse;
import br.com.oldtown.pharma.product.dto.response.ProductResponse;
import br.com.oldtown.pharma.product.dto.response.PromotionalPriceResponse;
import br.com.oldtown.pharma.product.dto.response.UpdatePriceResponse;
import br.com.oldtown.pharma.product.entity.Product;
import br.com.oldtown.pharma.product.entity.enums.ProductType;
import br.com.oldtown.pharma.product.entity.enums.PromotionStatus;
import br.com.oldtown.pharma.product.mapper.ProductMapper;
import br.com.oldtown.pharma.product.repository.ProductRepository;
import br.com.oldtown.pharma.product.service.ProductService;
import br.com.oldtown.pharma.product.service.SkuGeneratorService;
import br.com.oldtown.pharma.product.specification.ProductSearchCriteria;
import br.com.oldtown.pharma.product.specification.ProductSpecification;
import br.com.oldtown.pharma.shared.exception.BadRequestException;
import br.com.oldtown.pharma.shared.exception.ConflictException;
import br.com.oldtown.pharma.shared.exception.InvalidPriceException;
import br.com.oldtown.pharma.shared.exception.NotFoundException;
import br.com.oldtown.pharma.shared.utils.DateUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@Service
@Transactional
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
    @Transactional(readOnly = true)
    public Page<ProductResponse> search(ProductSearchCriteria criteria, Pageable pageable) {
        if ((criteria.minPrice() != null && criteria.minPrice().signum() < 0)
                || (criteria.maxPrice() != null && criteria.maxPrice().signum() < 0)
                || (criteria.minPrice() != null && criteria.maxPrice() != null
                    && criteria.minPrice().compareTo(criteria.maxPrice()) > 0)) {
            throw new BadRequestException("Price bounds must be non-negative and minPrice must not exceed maxPrice.");
        }
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

        return productRepository.findAll(spec, pageable).map(productMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse findByName(String name) {

        return productRepository.findByName(name)
                .map(productMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Product not found with name: " + name));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
    }

    @Override
    public ProductResponse create(CreateProductRequest request) {
        if (request.costPrice() == null || request.originalPrice() == null
                || request.costPrice().signum() <= 0 || request.originalPrice().compareTo(request.costPrice()) <= 0) {
            throw new InvalidPriceException("Original price must be greater than a positive cost price.");
        }
        validatePromotion(request.promotionalPrice(), request.costPrice(), request.promotionStartDate(),
                request.promotionEndDate(), LocalDateTime.now());
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

        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    public PromotionalPriceResponse createPromotionalPrice(Long id, CreatePromotionalPriceRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found."));

        if (!product.isActive()) {
            throw new ConflictException("Inactive product");
        }
        if (request.promotionalPrice() == null || request.promotionalPrice().compareTo(product.getCostPrice()) <= 0) {
            throw new InvalidPriceException("Price must be greater than cost price.");
        }

        LocalDateTime now = LocalDateTime.now();
        PromotionStatus status = product.getStatus(now);
        if (status == PromotionStatus.ACTIVE || status == PromotionStatus.SCHEDULED) {
            throw new ConflictException("Product has am active or scheduled promotion");
        }

        LocalDateTime startDate;
        LocalDateTime endDate;
        try {
            startDate = DateUtils.parseToLocalDateTime(request.promotionStartDate());
            endDate = DateUtils.parseToLocalDateTime(request.promotionEndDate());
        } catch (DateTimeParseException ex) {
            throw new BadRequestException("Promotion dates must use dd/MM/yyyy HH:mm:ss format.");
        }
        if (startDate == null || endDate == null) {
            throw new BadRequestException("Promotion start and end dates are required.");
        }

        validatePromotion(request.promotionalPrice(), product.getCostPrice(), startDate, endDate, now);
        product.setPromotionalPrice(request.promotionalPrice());
        product.setPromotionStartDate(startDate);
        product.setPromotionEndDate(endDate);
        return productMapper.toResponsePromotionalPrice(productRepository.save(product));
    }

    private void validatePromotion(BigDecimal price, BigDecimal cost, LocalDateTime start,
                                   LocalDateTime end, LocalDateTime now) {
        if (price == null) {
            if (start != null || end != null) {
                throw new BadRequestException("Promotion dates require a promotional price.");
            }
            return;
        }
        if (price.compareTo(cost) <= 0) {
            throw new InvalidPriceException("Promotional price must be greater than cost price.");
        }
        if (!DateUtils.isEndDateAfterStartDate(start, end)) {
            throw new BadRequestException("Promotion requires an end date after the start date.");
        }
        if (end.isBefore(now)) {
            throw new BadRequestException("Promotion end date must not be in the past.");
        }
    }

    @Override
    public DeletePromotionalPriceResponse deletePromotionalPrice(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found."));

        if (!product.isActive()) {
            throw new ConflictException("Inactive product");
        }

        PromotionStatus status = product.getStatus(LocalDateTime.now());
        if (status == PromotionStatus.ACTIVE || status == PromotionStatus.SCHEDULED) {
            product.setPromotionalPrice(null);
            product.setPromotionStartDate(null);
            product.setPromotionEndDate(null);
        } else {
            throw new ConflictException("There are no active or scheduled promotions for the product.");
        }

        return productMapper.toResponseDeletePromotionalPrice(productRepository.save(product));
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
    public UpdatePriceResponse updatePrice(Long id, UpdatePriceRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found."));

        if (request.originalPrice().compareTo(product.getCostPrice()) < 0
                || request.originalPrice().compareTo(product.getCostPrice()) == 0) {
            throw new InvalidPriceException("Price cannot be less or equal cost price");
        }

        BigDecimal previousPrice = product.getOriginalPrice();
        product.setOriginalPrice(request.originalPrice());
        productRepository.save(product);

        return new UpdatePriceResponse(id, previousPrice, product.getOriginalPrice());
    }

    @Override
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found."));
        product.setActive(false);
        productRepository.save(product);
    }
}
