package com.example.michal.inz.OBDConnection.Exceptions;

public class UnknownErrorException extends ResponseException {
    public UnknownErrorException() {
        super("ERROR");
    }
}
