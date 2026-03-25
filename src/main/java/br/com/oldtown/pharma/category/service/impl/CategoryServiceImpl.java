package br.com.oldtown.pharma.category.service.impl;

import br.com.oldtown.pharma.category.dto.CategoryResponse;
import br.com.oldtown.pharma.category.dto.CreateCategoryRequest;
import br.com.oldtown.pharma.category.dto.UpdateCategoryRequest;
import br.com.oldtown.pharma.category.entity.Category;
import br.com.oldtown.pharma.category.repository.CategoryRepository;
import br.com.oldtown.pharma.category.service.CategoryService;
import br.com.oldtown.pharma.shared.handler.BusinessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<CategoryResponse> getAll() {
        List<Category> categories = categoryRepository.findAll();

        if (categories.isEmpty()) {
            throw new BusinessException("No categories saved.");
        }

        return categories.stream()
                .map(category -> new CategoryResponse(
                        category.getId(),
                        category.getName(),
                        category.getDescription()
                )).toList();
    }

    @Override
    public CategoryResponse findById(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            return new CategoryResponse(category.get().getId(),
                    category.get().getName(),
                    category.get().getDescription());
        } else {
            throw new BusinessException("Category not found");
        }
    }

    @Override
    public CategoryResponse findByName(String name) {
        Category category = categoryRepository.findByName(name);

        if (category != null) {
            return new CategoryResponse(category.getId(),
                    category.getName(),
                    category.getDescription());
        } else {
            throw new BusinessException("Category not found");
        }
    }

    @Override
    public CategoryResponse create(CreateCategoryRequest request) {
        Category category = categoryRepository.findByName(request.name());

        if (category != null) {
            throw new BusinessException("Category already exists");
        }

        Category newCategory = new Category();
        newCategory.setName(request.name());
        newCategory.setDescription(request.description());

        Category categorySaved = categoryRepository.save(newCategory);
        return new CategoryResponse(categorySaved.getId(), categorySaved.getName(), categorySaved.getDescription());
    }

    @Override
    public CategoryResponse update(Long id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Category not found"));

        Category categoryUpdated = new Category();
        categoryUpdated.setId(id);
        categoryUpdated.setName(request.name());
        categoryUpdated.setDescription(request.description());
        categoryRepository.save(categoryUpdated);
        return new CategoryResponse(category.getId(), category.getName(), category.getDescription());
    }

    @Override
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Category not found"));

        categoryRepository.delete(category);
    }
}
