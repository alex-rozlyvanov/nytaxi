package com.goal.taxi.client.exception;

public class TokenServiceException extends RuntimeException {

    public TokenServiceException(final Exception cause) {
        super(cause);
    }

    public TokenServiceException(final String message) {
        super(message);
    }
}
