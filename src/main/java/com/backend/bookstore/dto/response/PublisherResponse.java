package com.backend.bookstore.dto.response;

import java.time.LocalDateTime;

public record PublisherResponse(
        Long id,
        String name,
        String country,
        LocalDateTime createdAt
) {
}