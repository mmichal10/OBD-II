package com.example.michal.inz.OBDConnection.Exceptions;

public class NonNumericResponseException extends ResponseException {
    public NonNumericResponseException(String message) {
        super("Error reading response: " + message);
    }
}
