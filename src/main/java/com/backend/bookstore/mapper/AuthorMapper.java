package com.backend.bookstore.mapper;

import com.backend.bookstore.dto.request.AuthorRequest;
import com.backend.bookstore.dto.response.AuthorResponse;
import com.backend.bookstore.entity.Author;
import org.springframework.stereotype.Component;

@Component
public class AuthorMapper {

    public AuthorResponse toResponse(Author author){

        return new AuthorResponse(
                author.getId(),
                author.getName(),
                author.getBio(),
                author.getCreatedAt()
        );
    }

    public Author toEntity(AuthorRequest request){

        return new Author(
                request.name(),
                request.bio()
        );
    }
}
