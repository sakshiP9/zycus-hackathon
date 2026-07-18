package com.hackathon.backend.exception;

public class SuggestionNotFoundException extends RuntimeException {
    public SuggestionNotFoundException(Long id) {
        super("Suggestion not found: " + id);
    }
}