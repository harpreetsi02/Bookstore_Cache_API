package com.backend.bookstore.controller;

import com.backend.bookstore.dto.request.AuthorRequest;
import com.backend.bookstore.dto.response.AuthorResponse;
import com.backend.bookstore.service.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
@Tag(name = "Author", description = "Authors details")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @Operation(summary = "Create a new author")
    @ApiResponse(responseCode = "201", description = "Author created")
    @ApiResponse(responseCode = "400", description = "Validation failed")
    @PostMapping
    public ResponseEntity<AuthorResponse> createAuthor(
            @Valid @RequestBody AuthorRequest request
    ) {
        AuthorResponse response =
                authorService.createAuthor(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(summary = "List all authors")
    @ApiResponse(responseCode = "200", description = "Authors get successful")
    @GetMapping
    public ResponseEntity<List<AuthorResponse>> findAllAuthors(){
        List<AuthorResponse> response =
                authorService.findAllAuthors();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get author by id")
    @ApiResponse(responseCode = "200", description = "Author get successful")
    @ApiResponse(responseCode = "404", description = "Author not found")
    @GetMapping("/{authorId}")
    public ResponseEntity<AuthorResponse> findAuthorById(
            @PathVariable Long authorId
    ) {
        AuthorResponse response =
                authorService.findAuthorById(authorId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update author by id")
    @ApiResponse(responseCode = "200", description = "Author update successfully")
    @ApiResponse(responseCode = "400", description = "Validation failed")
    @ApiResponse(responseCode = "404", description = "Author not found")
    @PutMapping("/{authorId}")
    public ResponseEntity<AuthorResponse> updateAuthor(
            @PathVariable Long authorId,
            @Valid @RequestBody AuthorRequest request
    ) {
        AuthorResponse response =
                authorService.updateAuthor(authorId, request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Deleting author")
    @ApiResponse(responseCode = "204", description = "Author delete successful")
    @ApiResponse(responseCode = "404", description = "Author not found")
    @DeleteMapping("/{authorId}")
    public ResponseEntity<Void> deleteAuthor(
            @PathVariable Long authorId
    ) {
        authorService.deleteAuthor(authorId);

        return ResponseEntity.noContent().build();
    }
}
