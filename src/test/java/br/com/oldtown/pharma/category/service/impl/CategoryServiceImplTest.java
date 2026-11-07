package br.com.oldtown.pharma.category.service.impl;

import br.com.oldtown.pharma.category.dto.CategoryResponse;
import br.com.oldtown.pharma.category.dto.CreateCategoryRequest;
import br.com.oldtown.pharma.category.dto.UpdateCategoryRequest;
import br.com.oldtown.pharma.category.entity.Category;
import br.com.oldtown.pharma.category.mapper.CategoryMapper;
import br.com.oldtown.pharma.category.repository.CategoryRepository;
import br.com.oldtown.pharma.shared.exception.ConflictException;
import br.com.oldtown.pharma.shared.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryServiceImpl(categoryRepository, categoryMapper);
    }

    @Test
    void shouldFindCategoryById() {
        Long categoryId = 1L;

        Category category = new Category("Medicines", "Medicinal products");
        category.setId(categoryId);

        CategoryResponse expectedResponse = new CategoryResponse(
                categoryId, "Medicines", "Medicinal products");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryMapper.toResponse(category)).thenReturn(expectedResponse);

        // Act
        CategoryResponse response = categoryService.findById(categoryId);

        // Assert
        assertThat(response).isEqualTo(expectedResponse);

        verify(categoryRepository).findById(categoryId);
        verify(categoryMapper).toResponse(category);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenCategoryDoesNotExist() {
        // Arrange
        Long categoryId = 999L;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> categoryService.findById(categoryId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Category not found");

        verify(categoryRepository).findById(categoryId);
        verifyNoInteractions(categoryMapper);
    }

    @Test
    void shouldFindCategoryByName() {
        // Arrange
        String categoryName = "Medicines";

        Category category = new Category(categoryName, "Medicinal products");
        category.setId(1L);

        CategoryResponse expectedResponse = new CategoryResponse(
                1L, categoryName, "Medicinal products");

        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.of(category));

        when(categoryMapper.toResponse(category)).thenReturn(expectedResponse);

        // Act
        CategoryResponse response = categoryService.findByName(categoryName);

        // Assert
        assertThat(response).isEqualTo(expectedResponse);

        verify(categoryRepository).findByName(categoryName);
        verify(categoryMapper).toResponse(category);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenCategoryNameDoesNotExist() {
        // Arrange
        String categoryName = "DVD";

        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> categoryService.findByName(categoryName))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Category not found with name: " + categoryName);

        verify(categoryRepository).findByName(categoryName);
        verifyNoInteractions(categoryMapper);
    }

    @Test
    void shouldReturnAllCategories() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        Category medicines = new Category("Medicines", "Medicinal products");
        medicines.setId(1L);

        Category cosmetics = new Category("Cosmetics", "Cosmetic products");
        cosmetics.setId(2L);

        CategoryResponse medicinesResponse = new CategoryResponse(
                1L, "Medicines", "Medicinal products");

        CategoryResponse cosmeticsResponse = new CategoryResponse(
                2L, "Cosmetics", "Cosmetics products");

        Page<Category> categoryPage = new PageImpl<>(
                List.of(medicines, cosmetics), pageable,2);

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toResponse(medicines)).thenReturn(medicinesResponse);
        when(categoryMapper.toResponse(cosmetics)).thenReturn(cosmeticsResponse);

        // Act
        Page<CategoryResponse> result = categoryService.getAll(pageable);

        // Assert
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getNumber()).isZero();
        assertThat(result.getSize()).isEqualTo(10);
        assertThat(result.getTotalElements()).isEqualTo(2);

        verify(categoryRepository).findAll(pageable);
        verify(categoryMapper).toResponse(medicines);
        verify(categoryMapper).toResponse(cosmetics);
    }

    @Test
    void shouldReturnEmptyPageWhenNoCategoriesExist() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> emptyPage = Page.empty(pageable);

        when(categoryRepository.findAll(pageable)).thenReturn(emptyPage);
        when(categoryRepository.findAll(pageable)).thenReturn(emptyPage);

        // Act
        Page<CategoryResponse> result = categoryService.getAll(pageable);

        // Assert
        assertThat(result.isEmpty());
        assertThat(result.getTotalElements()).isZero();

        verify(categoryRepository).findAll(pageable);
        verifyNoInteractions(categoryMapper);
    }

    @Test
    void shouldCreateCategory() {
        // Arrange
        CreateCategoryRequest request = new CreateCategoryRequest(
                "Medicines", "Medicinal products");
        Category categoryToSave = new Category("Medicines", "Medicinal products");
        Category savedCategory = new Category("Medicines", "Medicinal products");

        savedCategory.setId(1L);

        CategoryResponse expectedResponse = new CategoryResponse(
                1L, "Medicines", "Medicinal products");

        when(categoryRepository.existsByName(request.name())).thenReturn(false);
        when(categoryMapper.toEntity(request)).thenReturn(categoryToSave);
        when(categoryRepository.save(categoryToSave)).thenReturn(savedCategory);
        when(categoryMapper.toResponse(savedCategory)).thenReturn(expectedResponse);

        // Act
        CategoryResponse result = categoryService.create(request);

        // Assert
        assertThat(result).isEqualTo(expectedResponse);

        verify(categoryRepository).existsByName(request.name());
        verify(categoryMapper).toEntity(request);
        verify(categoryRepository).save(categoryToSave);
        verify(categoryMapper).toResponse(savedCategory);
    }

    @Test
    void shouldThrowConflictExceptionWhenCreatingCategoryWithExistingName() {
        // Arrange
        CreateCategoryRequest request = new CreateCategoryRequest(
                "Medicines", "Medical products");

        when(categoryRepository.existsByName(request.name())).thenReturn(true);

        // Act + Assert
        assertThatThrownBy(() -> categoryService.create(request))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Category already exists");

        verify(categoryRepository).existsByName(request.name());
        verify(categoryRepository, never()).save(any(Category.class));

        verifyNoInteractions(categoryMapper);
    }

    @Test
    void shouldUpdateCategory() {
        // Arrange
        Long categoryId = 1L;

        UpdateCategoryRequest request = new UpdateCategoryRequest(
                "Updated Medicines",
                "Updated description");

        Category existingCategory = new Category(
                "Medicines",
                "Medicinal products");
        existingCategory.setId(categoryId);

        CategoryResponse expectedResponse = new CategoryResponse(
                categoryId,
                "Updated Medicines",
                "Updated description"
        );

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.existsByNameAndIdNot(request.name(), categoryId)).thenReturn(false);
        when(categoryRepository.save(existingCategory)).thenReturn(existingCategory);
        when(categoryMapper.toResponse(existingCategory)).thenReturn(expectedResponse);

        // Act
        CategoryResponse result = categoryService.update(categoryId, request);

        // Assert
        assertThat(result).isEqualTo(expectedResponse);

        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).existsByNameAndIdNot(request.name(), categoryId);
        verify(categoryMapper).updateEntity(request, existingCategory);
        verify(categoryRepository).save(existingCategory);
        verify(categoryMapper).toResponse(existingCategory);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUpdatingNonexistentCategory() {
        // Arrange
        Long categoryId = 999L;

        UpdateCategoryRequest request = new UpdateCategoryRequest(
                "Updated name",
                "Updated description");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> categoryService.update(categoryId, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Category not found");

        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository, never()).existsByNameAndIdNot(anyString(), anyLong());
        verify(categoryRepository, never()).save(any(Category.class));

        verifyNoInteractions(categoryMapper);
    }

    @Test
    void shouldThrowConflictExceptionWhenUpdatingToExistingCategoryName() {
        // Arrange
        Long categoryId = 1L;

        UpdateCategoryRequest request = new UpdateCategoryRequest(
                "Cosmetics",
                "Updated description");

        Category existingCategory = new Category(
                "Medicines",
                "Medicinal products");
        existingCategory.setId(categoryId);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.existsByNameAndIdNot(request.name(), categoryId)).thenReturn(true);

        // Act + Assert
        assertThatThrownBy(() -> categoryService.update(categoryId, request))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Category already exists");

        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).existsByNameAndIdNot(request.name(), categoryId);
        verify(categoryRepository, never()).save(any(Category.class));

        verifyNoInteractions(categoryMapper);
    }

    @Test
    void shouldDeleteCategory() {
        // Arrange
        Long categoryId = 1L;

        Category category = new Category(
                "Medicines",
                "Medicinal products");
        category.setId(categoryId);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // Act
        categoryService.delete(categoryId);

        // Assert
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).delete(category);
        verifyNoInteractions(categoryMapper);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenDeletingNonexistentCategory() {
        // Arrange
        Long categoryId = 999L;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> categoryService.delete(categoryId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Category not found");

        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository, never()).delete(any(Category.class));

        verifyNoInteractions(categoryMapper);
    }
}
