package com.example.michal.inz.OBDConnection.Exceptions;

public class MisunderstoodCommandException extends ResponseException {
    public MisunderstoodCommandException() {
        super("?");
    }
}
