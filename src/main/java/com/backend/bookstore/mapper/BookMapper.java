package com.backend.bookstore.mapper;

import com.backend.bookstore.dto.request.BookRequest;
import com.backend.bookstore.dto.response.AuthorSummery;
import com.backend.bookstore.dto.response.BookResponse;
import com.backend.bookstore.dto.response.CategorySummery;
import com.backend.bookstore.dto.response.PublisherSummery;
import com.backend.bookstore.entity.Author;
import com.backend.bookstore.entity.Book;
import com.backend.bookstore.entity.Category;
import com.backend.bookstore.entity.Publisher;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BookMapper {

    public BookResponse toResponse(Book book){

        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getDescription(),
                book.getPrice(),
                book.getPublishDate(),
                book.getStockQuantity(),
                book.getCreatedAt(),
                new CategorySummery(book.getCategory().getId(), book.getCategory().getName()),
                new PublisherSummery(book.getPublisher().getId(), book.getPublisher().getName()),
                book.getAuthors().stream()
                        .map(author -> new AuthorSummery(author.getId(), author.getName()))
                        .collect(Collectors.toSet())
        );
    }

    public Book toEntity(
            BookRequest request, Category category,
            Publisher publisher, Set<Author> authors
    ) {
       return new Book(
               request.title(),
               request.isbn(),
               request.description(),
               request.price(),
               request.publishedDate(),
               request.stockQuantity(),
               category,
               publisher,
               authors
       );
    }

    public void updateEntity(
            Book book, BookRequest request, Category category,
            Publisher publisher, Set<Author> authors
    ) {
        book.setTitle(request.title());
        book.setIsbn(request.isbn());
        book.setDescription(request.description());
        book.setPrice(request.price());
        book.setPublishDate(request.publishedDate());
        book.setStockQuantity(request.stockQuantity());
        book.setCategory(category);
        book.setPublisher(publisher);
        book.getAuthors().clear();
        book.getAuthors().addAll(authors);
    }
}
