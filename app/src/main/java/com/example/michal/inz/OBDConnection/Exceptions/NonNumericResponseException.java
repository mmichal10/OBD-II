package com.example.michal.inz.OBDConnection.Exceptions;

public class NonNumericResponseException extends RuntimeException {
    public NonNumericResponseException(String message) {
        super("Error reading response: " + message);
    }
}
