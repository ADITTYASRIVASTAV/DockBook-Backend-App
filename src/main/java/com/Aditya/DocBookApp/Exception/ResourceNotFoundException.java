package com.Aditya.DocBookApp.Exception;

public class ResourceNotFoundException extends  RuntimeException
{
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
