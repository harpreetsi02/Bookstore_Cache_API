package com.backend.bookstore.controller;

import com.backend.bookstore.dto.request.BookRequest;
import com.backend.bookstore.dto.response.BookResponse;
import com.backend.bookstore.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@Tag(name = "Books", description = "Book catalog management")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Operation(summary = "Create a new book")
    @ApiResponse(responseCode = "201", description = "Book created")
    @ApiResponse(responseCode = "400", description = "Validation failed")
    @ApiResponse(responseCode = "404", description = "Category, publisher or author not found")
    @ApiResponse(responseCode = "409", description = "ISBN already exists")
    @PostMapping
    public ResponseEntity<BookResponse> createBook(
            @Valid @RequestBody BookRequest request
    ) {
        BookResponse response =
                bookService.createBook(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(summary = "List all books")
    @ApiResponse(responseCode = "200", description = "Book fetched")
    @GetMapping
    public ResponseEntity<List<BookResponse>> getAllBooks(){
        List<BookResponse> responses =
                bookService.getAllBooks();

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get book by id")
    @ApiResponse(responseCode = "200", description = "Book fetched")
    @ApiResponse(responseCode = "404", description = "Book not found")
    @GetMapping("/{bookId}")
    public ResponseEntity<BookResponse> getBookById(
            @PathVariable Long bookId
    ) {
        BookResponse response =
                bookService.getBookById(bookId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update book by id")
    @ApiResponse(responseCode = "200", description = "Book updated")
    @ApiResponse(responseCode = "400", description = "Validation failed")
    @ApiResponse(responseCode = "404", description = "Category, publisher or author not found")
    @ApiResponse(responseCode = "409", description = "ISBN already exists")
    @PutMapping("/{bookId}")
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable Long bookId,
            @Valid @RequestBody BookRequest request
    ) {
        BookResponse response =
                bookService.updateBook(bookId, request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete book by id")
    @ApiResponse(responseCode = "204", description = "Book deleted")
    @ApiResponse(responseCode = "404", description = "Book not found")
    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteBook(
            @PathVariable Long bookId
    ) {
        bookService.deleteBook(bookId);

        return ResponseEntity.noContent().build();
    }
}
