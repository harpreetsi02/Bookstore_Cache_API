package com.backend.bookstore.service;

import com.backend.bookstore.dto.request.ReviewRequest;
import com.backend.bookstore.dto.response.BookRatingResponse;
import com.backend.bookstore.dto.response.BookRatingSummary;
import com.backend.bookstore.dto.response.ReviewResponse;
import com.backend.bookstore.entity.Book;
import com.backend.bookstore.entity.Review;
import com.backend.bookstore.exception.BookNotFoundException;
import com.backend.bookstore.mapper.ReviewMapper;
import com.backend.bookstore.repository.BookRepository;
import com.backend.bookstore.repository.ReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final ReviewMapper reviewMapper;

    public ReviewService(ReviewRepository reviewRepository,
                         BookRepository bookRepository,
                         ReviewMapper reviewMapper) {
        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
        this.reviewMapper = reviewMapper;
    }

    @Transactional
    public ReviewResponse createReview(Long bookId, ReviewRequest request) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        Review saved = reviewRepository.save(reviewMapper.toEntity(request, book));

        return reviewMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviews(Long bookId, Pageable pageable) {
        if (!bookRepository.existsById(bookId)) {
            throw new BookNotFoundException(bookId);
        }

        return reviewRepository.findByBook_Id(bookId, pageable)
                .map(reviewMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public BookRatingResponse getBookRating(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new BookNotFoundException(bookId);
        }

        Double average = reviewRepository.findAverageRatingByBookId(bookId);
        long count = reviewRepository.countByBook_Id(bookId);

        double rounded = (average == null) ? 0.0 : Math.round(average * 100.0) / 100.0;

        return new BookRatingResponse(bookId, rounded, count);
    }

    @Transactional(readOnly = true)
    public List<BookRatingSummary> getTopRatedBooks(long minReviews, int limit) {
        long safeMinReviews = Math.max(minReviews, 1);
        int safeLimit = Math.clamp(limit, 1, 50);

        return reviewRepository.findTopRatedBooks(safeMinReviews, PageRequest.of(0, safeLimit));
    }
}