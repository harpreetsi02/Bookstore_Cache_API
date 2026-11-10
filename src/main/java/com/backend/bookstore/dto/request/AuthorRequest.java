package com.backend.bookstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthorRequest(
        @NotBlank(message = "Name is required!")
        @Size(max = 100, message = "Name must be at most 100 characters!")
        String name,

        @Size(max = 1000, message = "Bio must be at most 1000 characters!")
        String bio
) {
}
