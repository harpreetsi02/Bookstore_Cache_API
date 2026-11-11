package com.backend.bookstore.service;

import com.backend.bookstore.config.CacheNames;
import com.backend.bookstore.dto.request.PublisherRequest;
import com.backend.bookstore.dto.response.PublisherResponse;
import com.backend.bookstore.entity.Publisher;
import com.backend.bookstore.exception.DuplicateResourceException;
import com.backend.bookstore.exception.PublisherNotFoundException;
import com.backend.bookstore.exception.ResourceInUseException;
import com.backend.bookstore.mapper.PublisherMapper;
import com.backend.bookstore.repository.BookRepository;
import com.backend.bookstore.repository.PublisherRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PublisherService {

    private final PublisherRepository publisherRepository;
    private final PublisherMapper publisherMapper;
    private final BookRepository bookRepository;

    public PublisherService(
            PublisherRepository publisherRepository,
            PublisherMapper publisherMapper,
            BookRepository bookRepository
    ) {
        this.publisherRepository = publisherRepository;
        this.publisherMapper = publisherMapper;
        this.bookRepository = bookRepository;
    }

    @CacheEvict(value = CacheNames.PUBLISHER_LIST, allEntries = true)
    @Transactional
    public PublisherResponse createPublisher(PublisherRequest request){
        if (publisherRepository.existsByName(request.name())){
            throw new DuplicateResourceException("Publisher", "name", request.name());
        }

        Publisher savedPublisher = publisherRepository.save(publisherMapper.toEntity(request));

        return publisherMapper.toResponse(savedPublisher);
    }

    @Cacheable(value = CacheNames.PUBLISHER_LIST, key = "'all'")
    @Transactional(readOnly = true)
    public List<PublisherResponse> getPublishers(){

        return publisherRepository.findAll()
                .stream()
                .map(publisherMapper::toResponse)
                .toList();
    }

    @Cacheable(value = CacheNames.PUBLISHERS, key = "#publisherId")
    @Transactional(readOnly = true)
    public PublisherResponse getPublisherById(Long publisherId){
        Publisher publisher = publisherRepository.findById(publisherId)
                .orElseThrow(() -> new PublisherNotFoundException(publisherId));

        return publisherMapper.toResponse(publisher);
    }

    @Caching(
            put = @CachePut(value =CacheNames.PUBLISHERS, key = "#publisherId"),
            evict = {
                    @CacheEvict(value = CacheNames.PUBLISHER_LIST, allEntries = true),
                    @CacheEvict(value = CacheNames.BOOKS, allEntries = true),
                    @CacheEvict(value = CacheNames.BOOKS_LIST, allEntries = true)
            }
    )
    @Transactional
    public PublisherResponse updatePublisher(Long publisherId, PublisherRequest request){
        Publisher publisher = publisherRepository.findById(publisherId)
                .orElseThrow(() -> new PublisherNotFoundException(publisherId));

        String previousPublisher = publisher.getName();

        if (!previousPublisher.equals(request.name()) && publisherRepository.existsByName(request.name())){
            throw new DuplicateResourceException("Publisher", "name", request.name());
        }

        publisher.setName(request.name());
        publisher.setCountry(request.country());

        return publisherMapper.toResponse(publisher);
    }

    @Caching(evict = {
            @CacheEvict(value = CacheNames.PUBLISHERS, key = "#publisherId"),
            @CacheEvict(value = CacheNames.PUBLISHER_LIST, allEntries = true)
    })
    @Transactional
    public void deletePublisherById(Long publisherId){
        Publisher publisher = publisherRepository.findById(publisherId)
                        .orElseThrow(() -> new PublisherNotFoundException(publisherId));

        if (bookRepository.existsByPublisher_Id(publisherId)){
            throw new ResourceInUseException("Publisher", publisherId);
        }

        publisherRepository.delete(publisher);
    }
}
