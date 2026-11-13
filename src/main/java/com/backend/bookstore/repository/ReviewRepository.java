package com.backend.bookstore.repository;

import com.backend.bookstore.dto.response.BookRatingSummary;
import com.backend.bookstore.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByBook_Id(Long bookId, Pageable pageable);

    long countByBook_Id(Long bookId);

    void deleteByBook_Id(Long bookId);

    @Modifying
    @Query("delete from Review r where r.book.id = :bookId")
    void deleteAllByBookId(@Param("bookId") Long bookId);

    @Query("select avg(r.rating) from Review r where r.book.id = :bookId")
    Double findAverageRatingByBookId(@Param("bookId") Long bookId);

    @Query("""
        select new com.backend.bookstore.dto.response.BookRatingSummary(
                b.id, b.title, avg(r.rating), count(r)) 
        from Review r 
        join r.book b
        group by b.id, b.title
        having count(r) >= :minReviews
        order by avg(r.rating) desc
        """)
    List<BookRatingSummary> findTopRatedBooks(@Param("minReviews") long minReviews, Pageable pageable);
}
