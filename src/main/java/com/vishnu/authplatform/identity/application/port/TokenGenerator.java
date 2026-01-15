package com.vishnu.authplatform.identity.application.port;

public interface TokenGenerator {
    String generateOpaqueToken();

    String sha256Base64Url(String raw);
}
