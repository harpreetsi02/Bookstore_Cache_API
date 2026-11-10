package com.backend.bookstore.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public record BookRequest(

        @NotBlank(message = "Title is required!")
        @Size(max = 200, message = "Title must be at most 200 characters!")
        String title,

        @NotBlank(message = "ISBN is required!")
        @Size(max = 20, message = "ISBN must be at most 20 characters!")
        String isbn,

        @Size(max = 2000, message = "Description must be at most 2000 characters!")
        String description,

        @NotNull(message = "Price is required!")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0!")
        @Digits(integer = 8, fraction = 2, message = "Price format is invalid!")
        BigDecimal price,

        @PastOrPresent(message = "published date cannot be in the future!")
        LocalDate publishedDate,

        @NotNull(message = "Stock quantity is required!")
        @Min(value = 0, message = "Stock quantity cannot be negative!")
        Integer stockQuantity,

        @NotNull(message = "Category ID is required!")
        Long categoryId,

        @NotNull(message = "Publisher ID is required!")
        Long publisherId,

        @NotEmpty(message = "At least one author is required!")
        Set<Long> authorIds
) {
}
