package com.backend.bookstore.mapper;

import com.backend.bookstore.dto.request.CategoryRequest;
import com.backend.bookstore.dto.response.CategoryResponse;
import com.backend.bookstore.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryResponse toResponse(Category category){

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getCreatedAt()
        );
    }

    public Category toEntity(CategoryRequest request){

        return new Category(
                request.name(),
                request.description()
        );
    }
}
