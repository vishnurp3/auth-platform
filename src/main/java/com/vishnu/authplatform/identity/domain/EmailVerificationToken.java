package com.vishnu.authplatform.identity.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Accessors(fluent = true)
public final class EmailVerificationToken {
    @NonNull
    private final UUID id;
    @NonNull
    private final UserId userId;
    @NonNull
    private final String tokenHash;
    @NonNull
    private final Instant expiresAt;
    private final Instant usedAt;
    @NonNull
    private final Instant createdAt;

    public static EmailVerificationToken issue(UUID id, UserId userId, String tokenHash, Instant now, Instant expiresAt) {
        if (id == null) {
            throw new IllegalArgumentException("id is required");
        }
        if (!expiresAt.isAfter(now)) {
            throw new IllegalArgumentException("expiresAt must be after now");
        }
        return new EmailVerificationToken(id, userId, tokenHash, expiresAt, null, now);
    }

    public static EmailVerificationToken reconstitute(UUID id, UserId userId, String tokenHash, Instant expiresAt, Instant usedAt, Instant createdAt) {
        return new EmailVerificationToken(id, userId, tokenHash, expiresAt, usedAt, createdAt);
    }

    public boolean isExpired(Instant now) {
        return now.isAfter(expiresAt);
    }

    public boolean isUsed() {
        return usedAt != null;
    }

    public EmailVerificationToken markUsed(Instant now) {
        if (isUsed()) {
            return this;
        }
        return new EmailVerificationToken(id, userId, tokenHash, expiresAt, now, createdAt);
    }

    public void validateForUse(Instant now) {
        if (isUsed()) {
            throw new IllegalStateException("token already used");
        }
        if (isExpired(now)) {
            throw new IllegalStateException("token expired");
        }
    }
}
