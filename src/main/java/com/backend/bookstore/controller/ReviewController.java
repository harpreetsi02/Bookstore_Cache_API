package com.backend.bookstore.controller;

import com.backend.bookstore.dto.request.ReviewRequest;
import com.backend.bookstore.dto.response.BookRatingResponse;
import com.backend.bookstore.dto.response.BookRatingSummary;
import com.backend.bookstore.dto.response.ReviewResponse;
import com.backend.bookstore.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@Tag(name = "Review", description = "Book reviews and ratings")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Operation(summary = "Add a review to a book")
    @ApiResponse(responseCode = "201", description = "Review created")
    @ApiResponse(responseCode = "400", description = "Validation failed")
    @ApiResponse(responseCode = "404", description = "Book not found")
    @PostMapping("/{bookId}/reviews")
    public ResponseEntity<ReviewResponse> createReview(
            @PathVariable Long bookId,
            @Valid @RequestBody ReviewRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(reviewService.createReview(bookId, request));
    }

    @Operation(summary = "List reviews of a book (paginated, newest first)")
    @ApiResponse(responseCode = "200", description = "Reviews fetched")
    @ApiResponse(responseCode = "404", description = "Book not found")
    @GetMapping("/{bookId}/reviews")
    public ResponseEntity<PagedModel<ReviewResponse>> getReviews(
            @PathVariable Long bookId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Page<ReviewResponse> page = reviewService.getReviews(bookId, pageable);

        return ResponseEntity.ok(new PagedModel<>(page));
    }

    @Operation(summary = "Get average rating and review count of a book")
    @ApiResponse(responseCode = "200", description = "Rating fetched")
    @ApiResponse(responseCode = "404", description = "Book not found")
    @GetMapping("/{bookId}/rating")
    public ResponseEntity<BookRatingResponse> getBookRating(@PathVariable Long bookId) {
        return ResponseEntity.ok(reviewService.getBookRating(bookId));
    }

    @Operation(summary = "Top rated books (minimum review count required)")
    @ApiResponse(responseCode = "200", description = "Top rated books fetched")
    @GetMapping("/top-rated")
    public ResponseEntity<List<BookRatingSummary>> getTopRatedBooks(
            @RequestParam(defaultValue = "2") long minReviews,
            @RequestParam(defaultValue = "5") int limit
    ) {
        long safeMin = Math.clamp(minReviews, 1, 100);
        int safeLimit = Math.clamp(limit, 1, 50);
        return ResponseEntity.ok(reviewService.getTopRatedBooks(safeMin, safeLimit));
    }
}