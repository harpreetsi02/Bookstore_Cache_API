package com.backend.bookstore.exception;

public class PublisherNotFoundException extends RuntimeException{

    public PublisherNotFoundException(Long id) {
        super("Publisher with id " + id + " not found");
    }
}
