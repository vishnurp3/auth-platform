package com.vishnu.authplatform.identity.application.port;

import com.vishnu.authplatform.identity.domain.EmailVerificationToken;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface EmailVerificationTokenRepository {
    void save(EmailVerificationToken token);

    Optional<EmailVerificationToken> findByTokenHash(String tokenHash);

    Optional<Instant> findLatestCreatedAtByUserId(UUID userId);

    long countIssuedSince(UUID userId, Instant since);
}
