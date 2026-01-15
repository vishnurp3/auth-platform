package com.vishnu.authplatform.identity.application.port;

import com.vishnu.authplatform.identity.domain.EmailVerificationToken;

import java.util.Optional;

public interface EmailVerificationTokenRepository {
    void save(EmailVerificationToken token);

    Optional<EmailVerificationToken> findByTokenHash(String tokenHash);
}
