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
        Product product = productRepository.findByName(name);

        if (product == null) {
            throw new NotFoundException("Product not found with name: " + name);
        }

        return mapper.toResponse(product);
    }

    @Override
    public ProductResponse findById(Long id) {
        Optional<Product> product = productRepository.findById(id);

        if (product.isEmpty()) {
            throw new NotFoundException("Product not found");
        }

        return mapper.toResponse(product.get());
    }

    @Override
    public ProductResponse create(CreateProductRequest request) {
        Category category = categoryRepository.findById(request.categoryID())
                .orElseThrow(() -> new NotFoundException("Category not found."));

        Product product = new Product();
        product.setName(request.name());
        product.setDescription(request.description());
        product.setManufacturer(request.manufacturer());
        if (request.price().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Price cannot be negative.");
        }
        product.setPrice(request.price());
        product.setActive(request.active());
        product.setControlled(request.controlled());
        product.setRequiresPrescription(request.requiresPrescription());
        product.setCategory(category);

        Product p = productRepository.findByName(product.getName());

        if (p != null) {
            if (p.getName().equalsIgnoreCase(request.name())) {
                throw new ConflictException("Product already exists");
            }
        }

        Product savedProduct = productRepository.save(product);

        return mapper.toResponse(savedProduct);
    }

    @Override
    public ProductResponse update(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found."));

        Category category = categoryRepository.findById(request.categoryID())
                .orElseThrow(() -> new NotFoundException("Category not found."));

        product.setName(request.name());
        product.setDescription(request.description());
        product.setManufacturer(request.manufacturer());
        product.setPrice(request.price());
        product.setControlled(request.controlled());
        product.setActive(request.active());
        product.setRequiresPrescription(request.requiresPrescription());
        product.setCategory(category);

        Product productUpdate = productRepository.save(product);

        return mapper.toResponse(productUpdate);
    }

    @Override
    public void delete(long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found."));
        productRepository.delete(product);
    }
}
