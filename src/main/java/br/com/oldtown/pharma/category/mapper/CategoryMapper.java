package br.com.oldtown.pharma.category.mapper;

import br.com.oldtown.pharma.category.dto.CategoryResponse;
import br.com.oldtown.pharma.category.dto.CreateCategoryRequest;
import br.com.oldtown.pharma.category.dto.UpdateCategoryRequest;
import br.com.oldtown.pharma.category.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryResponse toResponse(Category category) {
        if (category == null) {
            return null;
        }

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }

    public Category toEntity(CreateCategoryRequest request) {
        return new Category(request.name(), request.description());
    }

    public void updateEntity(Category category, UpdateCategoryRequest request) {
        category.setName(request.name());
        category.setDescription(request.description());
    }
}
