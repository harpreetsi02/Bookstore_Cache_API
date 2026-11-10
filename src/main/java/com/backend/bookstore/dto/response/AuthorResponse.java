package com.backend.bookstore.dto.response;

import java.time.LocalDateTime;

public record AuthorResponse(
        Long id,
        String name,
        String bio,
        LocalDateTime createdAt
) {
}
