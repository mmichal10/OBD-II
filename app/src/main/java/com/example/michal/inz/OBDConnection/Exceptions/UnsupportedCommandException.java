package com.example.michal.inz.OBDConnection.Exceptions;

public class UnsupportedCommandException extends ResponseException {
    public UnsupportedCommandException() {
        super("7F 0[0-A] 1[1-2]", true);
    }
}
