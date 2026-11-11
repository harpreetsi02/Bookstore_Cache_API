package com.backend.bookstore.repository;

import com.backend.bookstore.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByIsbn(String isbn);

    boolean existsByCategory_Id(Long categoryId);
    boolean existsByPublisher_Id(Long publisherId);
    boolean existsByAuthors_Id(Long authorId);
}
