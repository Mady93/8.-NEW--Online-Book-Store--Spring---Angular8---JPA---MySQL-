package com.javainuse.exceptions;

public class MaxAdminLimitExceededException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public MaxAdminLimitExceededException() {
        super();
    }

    public MaxAdminLimitExceededException(String message) {
        super(message);
    }

    public MaxAdminLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }
	
}
