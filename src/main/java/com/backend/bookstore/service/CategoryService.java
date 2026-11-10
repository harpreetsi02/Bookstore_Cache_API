package com.backend.bookstore.service;

import com.backend.bookstore.dto.request.CategoryRequest;
import com.backend.bookstore.dto.response.CategoryResponse;
import com.backend.bookstore.entity.Category;
import com.backend.bookstore.exception.CategoryNotFoundException;
import com.backend.bookstore.exception.DuplicateResourceException;
import com.backend.bookstore.mapper.CategoryMapper;
import com.backend.bookstore.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request){
        if (categoryRepository.existsByName(request.name())){
            throw new DuplicateResourceException("Category", "name", request.name());
        }

        Category saved = categoryRepository.save(categoryMapper.toEntity(request));

        return categoryMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories(){

         return categoryRepository.findAll()
                 .stream()
                 .map(categoryMapper::toResponse)
                 .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long categoryId){
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        return categoryMapper.toResponse(category);
    }

    @Transactional
    public CategoryResponse updateCategory(Long categoryId, CategoryRequest request){
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        String previousName = existingCategory.getName();

        if (!previousName.equals(request.name()) && categoryRepository.existsByName(request.name())){
            throw new DuplicateResourceException("Category", "name", request.name());
        }

        existingCategory.setName(request.name());
        existingCategory.setDescription(request.description());

        return categoryMapper.toResponse(existingCategory);
    }

    @Transactional
    public void deleteCategory(Long categoryId){
        Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        categoryRepository.delete(category);
    }
}
