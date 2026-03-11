package br.com.oldtown.pharma.category.service.impl;

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
    public List<Category> getAll() {
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            throw new BusinessException("No categories saved.");
        }
        return categories;
    }

    @Override
    public Category findById(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        return category.orElseThrow(() -> new BusinessException("Category not found."));
    }

    @Override
    public Category findByName(String name) {
        Category category = categoryRepository.findByName(name);
        if (category != null) {
            return category;
        } else {
            throw new BusinessException("Category not founded.");
        }
    }

    @Override
    public void insert(Category category) {
        if (category.getName() == null || category.getDescription() == null
            || category.getName().isEmpty() || category.getDescription().isEmpty()) {
            throw new BusinessException("You must fill in all fields.");
        }
        categoryRepository.save(category);
    }

    @Override
    public void update(Long id, Category category) {
        Category categoryExisting = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Category not found."));

        categoryExisting.setName(category.getName());
        categoryExisting.setDescription(category.getDescription());

        categoryRepository.save(categoryExisting);
    }

    @Override
    public void delete(Long id) {
        Category category = findById(id);
        categoryRepository.delete(category);
    }
}
