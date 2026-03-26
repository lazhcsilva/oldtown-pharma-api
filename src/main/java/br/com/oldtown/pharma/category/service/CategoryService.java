package br.com.oldtown.pharma.category.service;

import br.com.oldtown.pharma.category.dto.CategoryResponse;
import br.com.oldtown.pharma.category.dto.CreateCategoryRequest;
import br.com.oldtown.pharma.category.dto.UpdateCategoryRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    Page<CategoryResponse> getAll(Pageable pageable);
    CategoryResponse findById(Long id);
    CategoryResponse findByName(String name);
    CategoryResponse create(CreateCategoryRequest category);
    CategoryResponse update(Long id, UpdateCategoryRequest category);
    void delete(Long id);
}
