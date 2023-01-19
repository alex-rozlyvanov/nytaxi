package com.goal.taxi.front.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class TokenRefreshException extends ResponseStatusException {
    public TokenRefreshException(final String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
