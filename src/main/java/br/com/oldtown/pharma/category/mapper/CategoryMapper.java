package br.com.oldtown.pharma.category.mapper;

import br.com.oldtown.pharma.category.dto.CategoryResponse;
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
}
