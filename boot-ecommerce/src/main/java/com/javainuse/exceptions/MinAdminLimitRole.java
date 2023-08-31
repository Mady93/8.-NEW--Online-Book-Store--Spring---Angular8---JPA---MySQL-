package com.javainuse.exceptions;

public class MinAdminLimitRole extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public MinAdminLimitRole() {
        super();
    }

    public MinAdminLimitRole(String message) {
        super(message);
    }

    public MinAdminLimitRole(String message, Throwable cause) {
        super(message, cause);
    }

}
