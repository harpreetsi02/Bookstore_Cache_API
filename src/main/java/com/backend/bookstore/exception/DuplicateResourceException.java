package com.backend.bookstore.exception;

public class DuplicateResourceException extends RuntimeException{

    public DuplicateResourceException(String resourceType, String fieldName, String value){
        super(resourceType + " with " + fieldName + ": " + value + " already exists!");
    }
}
