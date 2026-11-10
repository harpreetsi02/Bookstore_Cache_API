package com.backend.bookstore.mapper;

import com.backend.bookstore.dto.request.PublisherRequest;
import com.backend.bookstore.dto.response.PublisherResponse;
import com.backend.bookstore.entity.Publisher;
import org.springframework.stereotype.Component;

@Component
public class PublisherMapper {

    public PublisherResponse toResponse(Publisher publisher) {

        return new PublisherResponse(
                publisher.getId(),
                publisher.getName(),
                publisher.getCountry(),
                publisher.getCreatedAt()
        );
    }

    public Publisher toEntity(PublisherRequest request){

        return new Publisher(
                request.name(),
                request.country()
        );
    }
}
