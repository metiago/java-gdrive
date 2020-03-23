package io.zbx.exceptions;

public class SessionNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    private String errorMessage;

    public SessionNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
