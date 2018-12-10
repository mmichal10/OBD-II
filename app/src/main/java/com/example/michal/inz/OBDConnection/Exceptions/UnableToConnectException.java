package com.example.michal.inz.OBDConnection.Exceptions;

public class UnableToConnectException extends ResponseException {
    public UnableToConnectException() {
        super("UNABLE TO CONNECT");
    }
}
