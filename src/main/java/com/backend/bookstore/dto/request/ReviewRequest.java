package com.backend.bookstore.dto.request;

import jakarta.validation.constraints.*;

public record ReviewRequest(

        @NotBlank(message = "Reviewer name is required!")
        @Size(max = 100, message = "Reviewer name must be at most 100 characters!")
        String reviewerName,

        @NotNull(message = "Rating is required!")
        @Min(value = 1, message = "Rating must be at least 1!")
        @Max(value = 5, message = "Rating must be at most 5!")
        Integer rating,

        @Size(max = 1000, message = "Comment must be at most 1000 characters!")
        String comment
) {
}
