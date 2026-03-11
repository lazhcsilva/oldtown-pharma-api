package br.com.oldtown.pharma.category.controller;

import br.com.oldtown.pharma.category.entity.Category;
import br.com.oldtown.pharma.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "Get all categories")
    @GetMapping()
    public ResponseEntity<List<Category>> findAll() {
        return ResponseEntity.ok(categoryService.getAll());
    }

    @Operation(summary = "Get category by name")
    @GetMapping("/{name}")
    public ResponseEntity<Category> findByName(@PathVariable String name) {
        return ResponseEntity.ok(categoryService.findByName(name));
    }

    @Operation(summary = "Post a new category")
    @PostMapping
    public ResponseEntity<Category> insertCategory(@RequestBody Category category) {
        categoryService.insert(category);
        return ResponseEntity.ok(category);
    }

    @Operation(summary = "Update category")
    @PutMapping("/{id}")
    public ResponseEntity<Category> update(@PathVariable Long id, @RequestBody Category category) {
        categoryService.update(id, category);
        return ResponseEntity.ok(category);
    }

    @Operation(summary = "Delete category")
    @DeleteMapping("/{id}")
    public ResponseEntity<Category> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.ok().build();
    }

}
