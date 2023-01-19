package com.goal.taxi.front.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

public class WebExchangeBindExceptionWrapper extends ResponseStatusException {

    public WebExchangeBindExceptionWrapper(final BindingResult bindingResult) {
        super(HttpStatus.BAD_REQUEST, collectDefaultErrorMessages(bindingResult));
    }

    private static String collectDefaultErrorMessages(final BindingResult bindingResult) {
        return bindingResult.getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
    }
}
