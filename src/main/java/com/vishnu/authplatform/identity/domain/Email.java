package com.vishnu.authplatform.identity.domain;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.regex.Pattern;

@EqualsAndHashCode
@ToString
public final class Email {

    private static final Pattern EMAIL =
            Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final String value;

    private Email(String value) {
        this.value = value;
    }

    public static Email of(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("email is required");
        }
        String v = raw.trim().toLowerCase();
        if (!EMAIL.matcher(v).matches()) {
            throw new IllegalArgumentException("invalid email");
        }
        return new Email(v);
    }

    public String value() {
        return value;
    }
}
