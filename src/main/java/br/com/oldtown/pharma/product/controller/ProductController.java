package br.com.oldtown.pharma.product.controller;

import br.com.oldtown.pharma.product.dto.request.*;
import br.com.oldtown.pharma.product.dto.response.DeletePromotionalPriceResponse;
import br.com.oldtown.pharma.product.dto.response.ProductResponse;
import br.com.oldtown.pharma.product.dto.response.PromotionalPriceResponse;
import br.com.oldtown.pharma.product.dto.response.UpdatePriceResponse;
import br.com.oldtown.pharma.product.entity.enums.ProductType;
import br.com.oldtown.pharma.product.entity.enums.TherapeuticClass;
import br.com.oldtown.pharma.product.service.ProductService;
import br.com.oldtown.pharma.shared.exception.BadRequestException;
import br.com.oldtown.pharma.product.specification.ProductSearchCriteria;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Set;

@Tag(name = "Products", description = "Operations related to products")
@RestController
@RequestMapping("/api/v1/products")
@Validated
public class ProductController {

    private static final Set<String> ALLOWED_SORT_PROPERTIES = Set.of(
            "id", "name", "description", "manufacturer", "costPrice", "originalPrice",
            "promotionalPrice", "promotionStartDate", "promotionEndDate", "active",
            "sku", "barcode", "productType", "status", "createdAt", "updatedAt"
    );

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Get all products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @Parameters({
            @Parameter(name = "page", in = ParameterIn.QUERY, description = "Zero-based page index",
                    schema = @Schema(type = "integer", defaultValue = "0")),
            @Parameter(name = "size", in = ParameterIn.QUERY, description = "Page size",
                    schema = @Schema(type = "integer", defaultValue = "10")),
            @Parameter(name = "sort", in = ParameterIn.QUERY,
                    description = "Property and optional direction (asc or desc). Repeat for multiple orders. "
                            + "Allowed properties: id, name, description, manufacturer, costPrice, originalPrice, "
                            + "promotionalPrice, promotionStartDate, promotionEndDate, active, sku, barcode, "
                            + "productType, status, createdAt, updatedAt. currentPrice is not sortable.",
                    array = @ArraySchema(schema = @Schema(type = "string", example = "name,asc")),
                    example = "[\"name,asc\"]")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponse>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) ProductType type,
            @RequestParam(required = false) TherapeuticClass therapeuticClass,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Long categoryId,
            @PageableDefault(page = 0, size = 10, direction = Sort.Direction.ASC)
            @Parameter(hidden = true) Pageable pageable) {
        validateSort(pageable.getSort());
        ProductSearchCriteria criteria = new ProductSearchCriteria(name, type, therapeuticClass, active,
                minPrice, maxPrice, categoryId);
        return ResponseEntity.ok(productService.search(criteria, pageable));
    }

    private void validateSort(Sort sort) {
        for (Sort.Order order : sort) {
            if (!ALLOWED_SORT_PROPERTIES.contains(order.getProperty())) {
                throw new BadRequestException("Invalid sort property. Use an allowed property, "
                        + "optionally followed by ,asc or ,desc (example: name,asc).");
            }
        }
    }

    @Operation(summary = "Gey product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @Operation(summary = "Create a new Product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "409", description = "Product already exists"),
            @ApiResponse(responseCode = "500", description = "Connect")
    })
    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(request));
    }

    @Operation(summary = "Create promotional price to product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Promotional created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PatchMapping("/{id}/create-promotion")
    public ResponseEntity<PromotionalPriceResponse> createPromotionalPrice(@PathVariable @Positive Long id,
                                                                           @Valid @RequestBody CreatePromotionalPriceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createPromotionalPrice(id, request));
    }

    @Operation(summary = "Delete promotional price to product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Promotional deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PatchMapping("/{id}/delete-promotion")
    public ResponseEntity<DeletePromotionalPriceResponse> deletePromotionalPrice(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(productService.deletePromotionalPrice(id));
    }

    @Operation(summary = "Update a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateBasicData(@PathVariable @Positive Long id,
                                                           @Valid @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(productService.updateBasicData(id, request));
    }

    @Operation(summary = "Change price")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update product price"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Product noy found")
    })
    @PatchMapping("/{id}/price")
    public ResponseEntity<UpdatePriceResponse> updatePrice(@PathVariable @Positive Long id,
                                                           @Valid @RequestBody UpdatePriceRequest request) {
        return ResponseEntity.ok(productService.updatePrice(id, request));
    }

    @Operation(summary = "Delete a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
