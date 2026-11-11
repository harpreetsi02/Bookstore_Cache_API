package com.backend.bookstore.dto.response;

public record BookRatingSummary(
        Long bookId,
        String title,
        Double averageRating,
        Long reviewCount
) {
}