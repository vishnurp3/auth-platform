package com.vishnu.authplatform.identity.application.port;

public interface PasswordHasher {
    String hash(String rawPassword);
}
