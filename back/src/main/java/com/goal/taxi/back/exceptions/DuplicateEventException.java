package com.goal.taxi.back.exceptions;

public class DuplicateEventException extends RuntimeException {
    public DuplicateEventException(String message, Throwable e) {
        super(message, e);
    }
}
