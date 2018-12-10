package com.example.michal.inz.OBDConnection.Exceptions;

public class BaseException extends RuntimeException {
    private String message;
    private String response;
    

    protected BaseException(String msg) {
        message = msg;
    }

    private static String prepare(String s) {
        if(s != null)
            return s.replaceAll("\\s", "").toLowerCase();
        return "";
    }

    public boolean isError(String res) {
        response = res;

        return prepare(res).contains(prepare(message));
    }

    @Override
    public String getMessage() {
        return "Response: " + response;
    }

}
