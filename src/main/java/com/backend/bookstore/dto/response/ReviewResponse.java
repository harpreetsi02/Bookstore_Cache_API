package com.backend.bookstore.dto.response;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long id,
        String reviewerName,
        Integer rating,
        String comment,
        LocalDateTime createdAt
) {
}
