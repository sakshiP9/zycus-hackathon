package com.hackathon.backend.exception;

public class LLMCallException extends RuntimeException {
    public LLMCallException(String message, Throwable cause) {
        super(message, cause);
    }
}