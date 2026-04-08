package br.com.oldtown.pharma.product.service.impl;

import br.com.oldtown.pharma.category.entity.Category;
import br.com.oldtown.pharma.category.repository.CategoryRepository;
import br.com.oldtown.pharma.product.dto.CreateProductRequest;
import br.com.oldtown.pharma.product.dto.ProductResponse;
import br.com.oldtown.pharma.product.dto.UpdateProductRequest;
import br.com.oldtown.pharma.product.entity.Product;
import br.com.oldtown.pharma.product.mapper.ProductMapper;
import br.com.oldtown.pharma.product.repository.ProductRepository;
import br.com.oldtown.pharma.product.service.ProductService;
import br.com.oldtown.pharma.shared.exception.BadRequestException;
import br.com.oldtown.pharma.shared.exception.ConflictException;
import br.com.oldtown.pharma.shared.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper mapper;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper mapper,
                              CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.mapper = mapper;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Page<ProductResponse> getAll(Pageable pageable) {
        return productRepository.findAll(pageable).map(mapper::toResponse);
    }

    @Override
    public ProductResponse findByName(String name) {
        return productRepository.findByName(name)
                .map(mapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Product not found with name: " + name));
    }

    @Override
    public ProductResponse findById(Long id) {
        return productRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
    }

    @Override
    public ProductResponse create(CreateProductRequest request) {
        if (productRepository.existsByNameIgnoreCase(request.name())) {
            throw new ConflictException("Product already exists.");
        }

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new NotFoundException("Category not found."));

        Product product = mapper.toEntity(request, category);
        return mapper.toResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse update(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found."));

        if (productRepository.existsByNameIgnoreCaseAndIdNot(request.name(), id)) {
            throw new ConflictException("Product already exists.");
        }

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new NotFoundException("Category not found."));

        mapper.updateEntity(product, request, category);

        return mapper.toResponse(productRepository.save(product));
    }

    @Override
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found."));
        productRepository.delete(product);
    }
}
