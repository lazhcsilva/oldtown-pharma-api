package br.com.oldtown.pharma.category.service.impl;

import br.com.oldtown.pharma.category.dto.CategoryResponse;
import br.com.oldtown.pharma.category.dto.CreateCategoryRequest;
import br.com.oldtown.pharma.category.dto.UpdateCategoryRequest;
import br.com.oldtown.pharma.category.entity.Category;
import br.com.oldtown.pharma.category.mapper.CategoryMapper;
import br.com.oldtown.pharma.category.repository.CategoryRepository;
import br.com.oldtown.pharma.category.service.CategoryService;
import br.com.oldtown.pharma.shared.handler.BusinessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper mapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper mapper) {
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
    }

    @Override
    public Page<CategoryResponse> getAll(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(mapper::toResponse);
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

        category.setId(id);
        category.setName(request.name());
        category.setDescription(request.description());
        categoryRepository.save(category);

        return new CategoryResponse(category.getId(), category.getName(), category.getDescription());
    }

    @Override
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Category not found"));

        categoryRepository.delete(category);
    }
}
