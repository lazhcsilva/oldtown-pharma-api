package br.com.oldtown.pharma.category.service.impl;

import br.com.oldtown.pharma.category.entity.Category;
import br.com.oldtown.pharma.category.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Override
    public List<Category> getAll() {
        return List.of();
    }

    @Override
    public Category findByName(String name) {
        return null;
    }

    @Override
    public void insert(Category category) {

    }

    @Override
    public void update(Long id, Category category) {

    }

    @Override
    public void delete(Long id) {

    }
}
