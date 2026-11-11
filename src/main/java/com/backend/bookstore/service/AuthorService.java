package com.backend.bookstore.service;

import com.backend.bookstore.config.CacheNames;
import com.backend.bookstore.dto.request.AuthorRequest;
import com.backend.bookstore.dto.response.AuthorResponse;
import com.backend.bookstore.entity.Author;
import com.backend.bookstore.exception.AuthorNotFoundException;
import com.backend.bookstore.exception.ResourceInUseException;
import com.backend.bookstore.mapper.AuthorMapper;
import com.backend.bookstore.repository.AuthorRepository;
import com.backend.bookstore.repository.BookRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;
    private final BookRepository bookRepository;

    public AuthorService(
            AuthorRepository authorRepository,
            AuthorMapper authorMapper,
            BookRepository bookRepository
    ) {
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
        this.bookRepository = bookRepository;
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

    @Caching(evict = {
            @CacheEvict(value = CacheNames.BOOKS, allEntries = true),
            @CacheEvict(value = CacheNames.BOOKS_LIST, allEntries = true)
    })
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

        if (bookRepository.existsByAuthors_Id(authorId)){
            throw new ResourceInUseException("Author", authorId);
        }

        authorRepository.delete(author);
    }
}
