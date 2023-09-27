package com.javainuse.exceptions;

public class MinCancelledBookLimitException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public MinCancelledBookLimitException() {
        super();
    }

    public MinCancelledBookLimitException(String message) {
        super(message);
    }

    public MinCancelledBookLimitException(String message, Throwable cause) {
        super(message, cause);
    }

}
