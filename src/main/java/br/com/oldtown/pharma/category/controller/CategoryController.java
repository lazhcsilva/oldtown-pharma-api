package br.com.oldtown.pharma.category.controller;

import br.com.oldtown.pharma.category.dto.CategoryResponse;
import br.com.oldtown.pharma.category.dto.CreateCategoryRequest;
import br.com.oldtown.pharma.category.dto.UpdateCategoryRequest;
import br.com.oldtown.pharma.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Categories", description = "Operations related to categories")
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "Get all categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @GetMapping()
    public ResponseEntity<PagedModel<CategoryResponse>> findAll(
            @Parameter(hidden = true) Pageable pageable) {
        Page<CategoryResponse> page = categoryService.getAll(pageable);
        return ResponseEntity.ok(new PagedModel<>(page));
    }

    @Operation(summary = "Get category by name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @GetMapping("/search")
    public ResponseEntity<CategoryResponse> findByName(
            @Parameter(description = "Category", example = "free")
            @RequestParam String name) {
        return ResponseEntity.ok(categoryService.findByName(name));
    }

    @Operation(summary = "Post a new category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
    })
    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CreateCategoryRequest category) {
        return ResponseEntity.ok(categoryService.create(category));
    }

    @Operation(summary = "Update category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
    })
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(@PathVariable @Positive Long id,
                                                   @Valid @RequestBody UpdateCategoryRequest category) {
        return ResponseEntity.ok(categoryService.update(id, category));
    }

    @Operation(summary = "Delete category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
