package com.vishnu.authplatform.appregistry.domain;

import java.util.regex.Pattern;

public record RoleCode(String value) {

    private static final Pattern CODE_PATTERN = Pattern.compile("^[A-Z][A-Z0-9_]{1,49}$");

    public RoleCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("roleCode is required");
        }
        value = value.trim().toUpperCase();
        if (!CODE_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(
                    "roleCode must start with a letter, contain only uppercase letters, digits, and underscores, and be 2-50 characters");
        }
    }
}
