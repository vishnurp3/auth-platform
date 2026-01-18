package com.vishnu.authplatform.identity.application.port;

import com.vishnu.authplatform.identity.domain.EmailVerificationToken;
import com.vishnu.authplatform.identity.domain.UserId;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface EmailVerificationTokenRepository {
    void save(EmailVerificationToken token);

    Optional<EmailVerificationToken> findById(UUID tokenId);

    Optional<Instant> findLatestCreatedAtByUserId(UserId userId);

    long countIssuedSince(UserId userId, Instant since);
}
