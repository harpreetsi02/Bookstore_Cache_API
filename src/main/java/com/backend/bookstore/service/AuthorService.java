package com.backend.bookstore.service;

import com.backend.bookstore.dto.request.AuthorRequest;
import com.backend.bookstore.dto.response.AuthorResponse;
import com.backend.bookstore.entity.Author;
import com.backend.bookstore.exception.AuthorNotFoundException;
import com.backend.bookstore.mapper.AuthorMapper;
import com.backend.bookstore.repository.AuthorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    public AuthorService(AuthorRepository authorRepository, AuthorMapper authorMapper) {
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
    }

    @Transactional
    public AuthorResponse createAuthor(AuthorRequest request){

        Author author = authorMapper.toEntity(request);

        Author savedAuthor = authorRepository.save(author);

        return authorMapper.toResponse(savedAuthor);
    }

    @Transactional(readOnly = true)
    public List<AuthorResponse> findAllAuthors(){

        return authorRepository.findAll()
                .stream()
                .map(authorMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AuthorResponse findAuthorById(Long authorId){
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new AuthorNotFoundException(authorId));

        return authorMapper.toResponse(author);
    }

    @Transactional
    public AuthorResponse updateAuthor(Long authorId, AuthorRequest request){
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new AuthorNotFoundException(authorId));

        author.setName(request.name());
        author.setBio(request.bio());

        return authorMapper.toResponse(author);
    }

    @Transactional
    public void deleteAuthor(Long authorId){
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new AuthorNotFoundException(authorId));

        authorRepository.delete(author);
    }
}
