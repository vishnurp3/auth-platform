package com.vishnu.authplatform.identity.domain;

public record Password(String value) {

    private static final int MIN_LENGTH = 10;

    public Password {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("password is required");
        }
        if (value.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("password must be at least " + MIN_LENGTH + " characters");
        }
    }
}
