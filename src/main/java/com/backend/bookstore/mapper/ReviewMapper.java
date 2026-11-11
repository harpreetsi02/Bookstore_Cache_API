package com.backend.bookstore.mapper;

import com.backend.bookstore.dto.request.ReviewRequest;
import com.backend.bookstore.dto.response.ReviewResponse;
import com.backend.bookstore.entity.Book;
import com.backend.bookstore.entity.Review;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class ReviewMapper {

    public ReviewResponse toResponse(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getReviewerName(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }

    public Review toEntity(ReviewRequest request, Book book) {
        return new Review(
                request.reviewerName(),
                request.rating(),
                request.comment(),
                book
        );
    }
}
