package com.backend.bookstore.exception;

public class DuplicateResourceException extends RuntimeException{

    public DuplicateResourceException(String resourceType, String name){
        super(resourceType + " with name: " + name + " already exists!");
    }
}
