package com.backend.bookstore.controller;

import com.backend.bookstore.dto.request.CategoryRequest;
import com.backend.bookstore.dto.response.CategoryResponse;
import com.backend.bookstore.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Categories", description = "Book categories management")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "Create a new category")
    @ApiResponse(responseCode = "201", description = "Category created")
    @ApiResponse(responseCode = "400", description = "Validation failed")
    @ApiResponse(responseCode = "409", description = "Category name already exists")
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryRequest categoryRequest
    ) {
        CategoryResponse response =
                categoryService.createCategory(categoryRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(summary = "List all categories")
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories(){
        List<CategoryResponse> responses =
                categoryService.getAllCategories();

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get category by id")
    @ApiResponse(responseCode = "200", description = "Category get successfully")
    @ApiResponse(responseCode = "404", description = "Category not found")
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> getCategoryById(
            @PathVariable Long categoryId
    ){
        CategoryResponse response =
                categoryService.getCategoryById(categoryId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update category by id")
    @ApiResponse(responseCode = "200", description = "Category update successfully")
    @ApiResponse(responseCode = "400", description = "Validation failed")
    @ApiResponse(responseCode = "404", description = "Category not found")
    @ApiResponse(responseCode = "409", description = "Category name already exists")
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long categoryId,
            @Valid @RequestBody CategoryRequest categoryRequest
    ) {
        CategoryResponse response =
                categoryService.updateCategory(categoryId, categoryRequest);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Deleting category")
    @ApiResponse(responseCode = "204", description = "Category delete successful")
    @ApiResponse(responseCode = "404", description = "Category not found")
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long categoryId
    ) {
        categoryService.deleteCategory(categoryId);

        return ResponseEntity.noContent().build();
    }
}
