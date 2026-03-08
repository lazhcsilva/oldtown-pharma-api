package br.com.oldtown.pharma.category.service;

import br.com.oldtown.pharma.category.entity.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getAll();
    Category findByName(String name);
    void insert(Category category);
    void update(Long id, Category category);
    void delete(Long id);
}
