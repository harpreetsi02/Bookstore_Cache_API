package com.backend.bookstore.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public record BookResponse(
        Long id,
        String title,
        String isbn,
        String description,
        BigDecimal price,
        LocalDate publishedDate,
        Integer stockQuantity,
        LocalDateTime createdAt,
        CategorySummery category,
        PublisherSummery publisher,
        Set<AuthorSummery> authors
) {
}
