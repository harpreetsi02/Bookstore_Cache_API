package com.backend.bookstore.service;

import com.backend.bookstore.config.CacheNames;
import com.backend.bookstore.dto.request.CategoryRequest;
import com.backend.bookstore.dto.response.CategoryResponse;
import com.backend.bookstore.entity.Category;
import com.backend.bookstore.exception.CategoryNotFoundException;
import com.backend.bookstore.exception.DuplicateResourceException;
import com.backend.bookstore.exception.ResourceInUseException;
import com.backend.bookstore.mapper.CategoryMapper;
import com.backend.bookstore.repository.BookRepository;
import com.backend.bookstore.repository.CategoryRepository;
import org.hibernate.annotations.Cache;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final BookRepository bookRepository;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper, BookRepository bookRepository) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.bookRepository = bookRepository;
    }

    @CacheEvict(value = CacheNames.CATEGORY_LIST, allEntries = true)
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request){
        if (categoryRepository.existsByName(request.name())){
            throw new DuplicateResourceException("Category", "name", request.name());
        }

        Category saved = categoryRepository.save(categoryMapper.toEntity(request));

        return categoryMapper.toResponse(saved);
    }

    @Cacheable(value = CacheNames.CATEGORY_LIST, key = "'all'")
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories(){

         return categoryRepository.findAll()
                 .stream()
                 .map(categoryMapper::toResponse)
                 .toList();
    }

    @Cacheable(value = CacheNames.CATEGORIES, key = "#categoryId")
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long categoryId){
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        return categoryMapper.toResponse(category);
    }

    @Caching(
            put = @CachePut(value = CacheNames.CATEGORIES, key = "#categoryId"),
            evict = {
                    @CacheEvict(value = CacheNames.CATEGORY_LIST, allEntries = true),
                    @CacheEvict(value = CacheNames.BOOKS, allEntries = true),
                    @CacheEvict(value = CacheNames.BOOKS_LIST, allEntries = true)
            }
    )
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

    @Caching(evict = {
            @CacheEvict(value = CacheNames.CATEGORIES, key = "#categoryId"),
            @CacheEvict(value = CacheNames.CATEGORY_LIST, allEntries = true)
    })
    @Transactional
    public void deleteCategory(Long categoryId){
        Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        if (bookRepository.existsByCategory_Id(categoryId)){
            throw new ResourceInUseException("Category", categoryId);
        }

        categoryRepository.delete(category);
    }
}
