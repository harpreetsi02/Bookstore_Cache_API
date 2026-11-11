package com.backend.bookstore.service;

import com.backend.bookstore.config.CacheNames;
import com.backend.bookstore.dto.request.BookRequest;
import com.backend.bookstore.dto.response.BookResponse;
import com.backend.bookstore.entity.Author;
import com.backend.bookstore.entity.Book;
import com.backend.bookstore.entity.Category;
import com.backend.bookstore.entity.Publisher;
import com.backend.bookstore.exception.*;
import com.backend.bookstore.mapper.BookMapper;
import com.backend.bookstore.repository.AuthorRepository;
import com.backend.bookstore.repository.BookRepository;
import com.backend.bookstore.repository.CategoryRepository;
import com.backend.bookstore.repository.PublisherRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;
    private final BookMapper bookMapper;

    public BookService(
            BookRepository bookRepository,
            CategoryRepository categoryRepository,
            AuthorRepository authorRepository,
            PublisherRepository publisherRepository,
            BookMapper bookMapper
    ) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.authorRepository = authorRepository;
        this.publisherRepository = publisherRepository;
        this.bookMapper = bookMapper;
    }

    @CacheEvict(value = CacheNames.BOOKS_LIST, allEntries = true)
    @Transactional
    public BookResponse createBook(BookRequest request){
        if (bookRepository.existsByIsbn(request.isbn())){
            throw new DuplicateResourceException("Book", "isbn",  request.isbn());
        }

        Category category = findCategory(request.categoryId());
        Publisher publisher = findPublisher(request.publisherId());
        Set<Author> authors = findAuthors(request.authorIds());

        Book book = bookMapper.toEntity(request, category, publisher, authors);
        Book savedBook = bookRepository.save(book);

        return bookMapper.toResponse(savedBook);
    }

    @Cacheable(value = CacheNames.BOOKS_LIST, key = "'all'")
    @Transactional(readOnly = true)
    public List<BookResponse> getAllBooks(){
        return bookRepository.findAll()
                .stream()
                .map(bookMapper::toResponse)
                .toList();
    }

    @Cacheable(value = CacheNames.BOOKS, key = "#bookId")
    @Transactional(readOnly = true)
    public BookResponse getBookById(Long bookId){
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        return bookMapper.toResponse(book);
    }

    @Caching(
            put = @CachePut(value = CacheNames.BOOKS, key = "#bookId"),
            evict = @CacheEvict(value = CacheNames.BOOKS_LIST, allEntries = true)
    )
    @Transactional
    public BookResponse updateBook(Long bookId, BookRequest request){
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        if (!book.getIsbn().equals(request.isbn()) && bookRepository.existsByIsbn(request.isbn())){
            throw new DuplicateResourceException("Book", "isbn", request.isbn());
        }

        Category category = findCategory(request.categoryId());
        Publisher publisher = findPublisher(request.publisherId());
        Set<Author> authors = findAuthors(request.authorIds());

        bookMapper.updateEntity(book, request, category, publisher, authors);

        return bookMapper.toResponse(book);
    }

    @Caching(evict = {
            @CacheEvict(value = CacheNames.BOOKS, key = "#bookId"),
            @CacheEvict(value = CacheNames.BOOKS_LIST, allEntries = true)
    })
    @Transactional
    public void deleteBook(Long bookId){
        Book book =  bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        bookRepository.delete(book);
    }

    private Category findCategory(Long categoryId){
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
    }

    private Publisher findPublisher(Long publisherId){
        return publisherRepository.findById(publisherId)
                .orElseThrow(() -> new PublisherNotFoundException(publisherId));
    }

    private Set<Author> findAuthors(Set<Long> authorIds){
        Set<Author> authors = new HashSet<>(authorRepository.findAllById(authorIds));

        if (authors.size() != authorIds.size()){
            Set<Long> foundIds = authors.stream()
                    .map(Author::getId)
                    .collect(Collectors.toSet());

            Long missingId = authorIds.stream()
                    .filter(author -> !foundIds.contains(author))
                    .findFirst()
                    .orElseThrow();

            throw new AuthorNotFoundException(missingId);
        }

        return authors;
    }
}
