package com.example.michal.inz.OBDConnection.Exceptions;

public class StoppedException extends ResponseException {
    public StoppedException() {
        super("STOPPED");
    }
}
