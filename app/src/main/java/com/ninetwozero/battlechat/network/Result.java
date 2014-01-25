package com.ninetwozero.battlechat.network;

public class Result<T> {
    private final Status status;
    private final String message;
    private T data;

    public Result(final Status status, final String message) {
        this.status = status;
        this.message = message;
    }

    public Result(final Status status, final String message, final T data) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Result{" +
            "status=" + status +
            ", message='" + message + '\'' +
            ", data=" + data +
            '}';
    }

    public enum Status {
        SUCCESS,
        FAILURE,
        ERROR,
        NETWORK_FAILURE(),
        CANCELLED()
    }
}
