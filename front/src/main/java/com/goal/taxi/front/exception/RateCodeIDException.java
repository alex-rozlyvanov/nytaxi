package com.goal.taxi.front.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class RateCodeIDException extends ResponseStatusException {
    public RateCodeIDException(final String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
