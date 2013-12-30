package com.ninetwozero.battlechat.network.exception;

public class Failure extends Exception {
    public Failure(final Exception e) {
        super(e);
    }

    public Failure(final String message) {
        super(message);
    }
}
