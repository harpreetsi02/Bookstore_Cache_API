package com.backend.bookstore.service;

import com.backend.bookstore.dto.request.PublisherRequest;
import com.backend.bookstore.dto.response.PublisherResponse;
import com.backend.bookstore.entity.Publisher;
import com.backend.bookstore.exception.DuplicateResourceException;
import com.backend.bookstore.exception.PublisherNotFoundException;
import com.backend.bookstore.mapper.PublisherMapper;
import com.backend.bookstore.repository.PublisherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PublisherService {

    private final PublisherRepository publisherRepository;
    private final PublisherMapper publisherMapper;

    public PublisherService(PublisherRepository publisherRepository, PublisherMapper publisherMapper) {
        this.publisherRepository = publisherRepository;
        this.publisherMapper = publisherMapper;
    }

    @Transactional
    public PublisherResponse createPublisher(PublisherRequest request){
        if (publisherRepository.existsByName(request.name())){
            throw new DuplicateResourceException("Publisher", request.name());
        }

        Publisher savedPublisher = publisherRepository.save(publisherMapper.toEntity(request));

        return publisherMapper.toResponse(savedPublisher);
    }

    @Transactional(readOnly = true)
    public List<PublisherResponse> getPublishers(){

        return publisherRepository.findAll()
                .stream()
                .map(publisherMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PublisherResponse getPublisherById(Long publisherId){
        Publisher publisher = publisherRepository.findById(publisherId)
                .orElseThrow(() -> new PublisherNotFoundException(publisherId));

        return publisherMapper.toResponse(publisher);
    }

    @Transactional
    public PublisherResponse updatePublisher(Long publisherId, PublisherRequest request){
        Publisher publisher = publisherRepository.findById(publisherId)
                .orElseThrow(() -> new PublisherNotFoundException(publisherId));

        String previousPublisher = publisher.getName();

        if (!previousPublisher.equals(request.name()) && publisherRepository.existsByName(request.name())){
            throw new DuplicateResourceException("Publisher", request.name());
        }

        publisher.setName(request.name());
        publisher.setCountry(request.country());

        return publisherMapper.toResponse(publisher);
    }

    @Transactional
    public void deletePublisherById(Long publisherId){
        Publisher publisher = publisherRepository.findById(publisherId)
                        .orElseThrow(() -> new PublisherNotFoundException(publisherId));

        publisherRepository.delete(publisher);
    }
}
