package com.ninetwozero.battlechat.datatypes;

public class ApiServiceError {
    private final String message;
    public ApiServiceError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
