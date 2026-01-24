package com.vishnu.authplatform.appregistry.domain;

import java.util.regex.Pattern;

public record ApplicationCode(String value) {

    private static final Pattern CODE_PATTERN = Pattern.compile("^[A-Z][A-Z0-9_]{1,49}$");

    public ApplicationCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("applicationCode is required");
        }
        value = value.trim().toUpperCase();
        if (!CODE_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(
                    "applicationCode must start with a letter, contain only uppercase letters, digits, and underscores, and be 2-50 characters");
        }
    }
}
