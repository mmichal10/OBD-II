package com.example.michal.inz.OBDConnection.Exceptions;

public class WrongCommandException extends BaseException {
    public WrongCommandException() {
        super("?");
    }
}
