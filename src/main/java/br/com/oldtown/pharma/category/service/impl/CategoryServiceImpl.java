package br.com.oldtown.pharma.category.service.impl;

import br.com.oldtown.pharma.category.dto.CategoryResponse;
import br.com.oldtown.pharma.category.dto.CreateCategoryRequest;
import br.com.oldtown.pharma.category.dto.UpdateCategoryRequest;
import br.com.oldtown.pharma.category.entity.Category;
import br.com.oldtown.pharma.category.mapper.CategoryMapper;
import br.com.oldtown.pharma.category.repository.CategoryRepository;
import br.com.oldtown.pharma.category.service.CategoryService;
import br.com.oldtown.pharma.shared.exception.ConflictException;
import br.com.oldtown.pharma.shared.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
        return categoryRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Category ot found"));
    }

    @Override
    public CategoryResponse findByName(String name) {
      return categoryRepository.findByName(name)
              .map(mapper::toResponse)
              .orElseThrow(() -> new NotFoundException("Category not found with name: " + name));
    }

    @Override
    public CategoryResponse create(CreateCategoryRequest request) {
        if (categoryRepository.existsByName(request.name())) {
            throw new ConflictException("Category already exists");
        }

        Category newCategory = mapper.toEntity(request);
        Category categorySaved = categoryRepository.save(newCategory);
        return mapper.toResponse(categorySaved);
    }

    @Override
    public CategoryResponse update(Long id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        if (categoryRepository.existsByNameAndIdNot(request.name(), id)) {
            throw new ConflictException("Category already exists");
        }

        category = mapper.toUpdateEntity(request);

        Category savedCategory = categoryRepository.save(category);

        return new CategoryResponse(savedCategory.getId(), savedCategory.getName(), savedCategory.getDescription());
    }

    @Override
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        categoryRepository.delete(category);
    }
}
