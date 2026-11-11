package com.backend.bookstore.dto.response;

public record BookRatingResponse(
        Long bookId,
        Double averageRating,
        Long reviewCount
) {
}
