package com.vishnu.authplatform.identity.application;

import com.vishnu.authplatform.identity.application.port.EmailVerificationTokenRepository;
import com.vishnu.authplatform.identity.domain.UserId;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.Instant;

@RequiredArgsConstructor
public final class VerificationEmailRateLimiter {

    private final EmailVerificationTokenRepository tokenRepository;
    private final Duration minInterval;
    private final Duration rollingWindow;
    private final int maxPerWindow;

    public boolean isAllowed(UserId userId, Instant now) {
        if (isWithinMinInterval(userId, now)) {
            return false;
        }
        return !isWindowLimitExceeded(userId, now);
    }

    private boolean isWithinMinInterval(UserId userId, Instant now) {
        Instant lastIssuedAt = tokenRepository.findLatestCreatedAtByUserId(userId).orElse(null);
        return lastIssuedAt != null && Duration.between(lastIssuedAt, now).compareTo(minInterval) < 0;
    }

    private boolean isWindowLimitExceeded(UserId userId, Instant now) {
        Instant windowStart = now.minus(rollingWindow);
        long issuedInWindow = tokenRepository.countIssuedSince(userId, windowStart);
        return issuedInWindow >= maxPerWindow;
    }
}
