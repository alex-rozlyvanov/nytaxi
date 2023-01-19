package com.goal.taxi.front.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UnauthorizedException extends ResponseStatusException {
    public UnauthorizedException(final String msg) {
        super(HttpStatus.UNAUTHORIZED, msg);
    }
}
