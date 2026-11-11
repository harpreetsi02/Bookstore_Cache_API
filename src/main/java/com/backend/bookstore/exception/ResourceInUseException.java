package com.backend.bookstore.exception;

public class ResourceInUseException extends RuntimeException{

    public ResourceInUseException(String resourceType, Long id) {
        super(resourceType + " with id: " + id + " is already in use");
    }
}
