package com.vishnu.authplatform.identity.application;

import com.vishnu.authplatform.identity.application.port.EmailVerificationTokenRepository;
import com.vishnu.authplatform.identity.application.port.TokenGenerator;
import com.vishnu.authplatform.identity.application.port.UserRepository;
import com.vishnu.authplatform.identity.application.port.VerificationEmailPublisher;
import com.vishnu.authplatform.identity.domain.Email;
import com.vishnu.authplatform.identity.domain.EmailVerificationToken;
import com.vishnu.authplatform.identity.domain.User;
import com.vishnu.authplatform.identity.domain.UserStatus;
import lombok.RequiredArgsConstructor;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

@RequiredArgsConstructor
public final class ResendVerificationEmailUseCase {

    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository tokenRepository;
    private final TokenGenerator tokenGenerator;
    private final VerificationEmailPublisher verificationEmailPublisher;
    private final Clock clock;

    private final Duration minInterval;
    private final Duration rollingWindow;
    private final int maxPerWindow;

    public void execute(Command cmd) {
        Email email;
        try {
            email = Email.of(cmd.email());
        } catch (IllegalArgumentException e) {
            return;
        }

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return;

        if (user.status() != UserStatus.PENDING_VERIFICATION) {
            return;
        }

        Instant now = Instant.now(clock);

        Instant lastIssuedAt = tokenRepository.findLatestCreatedAtByUserId(user.id().value()).orElse(null);
        if (lastIssuedAt != null && Duration.between(lastIssuedAt, now).compareTo(minInterval) < 0) {
            return;
        }

        Instant windowStart = now.minus(rollingWindow);
        long issuedInWindow = tokenRepository.countIssuedSince(user.id().value(), windowStart);
        if (issuedInWindow >= maxPerWindow) {
            return;
        }

        String rawToken = tokenGenerator.generateOpaqueToken();
        String tokenHash = tokenGenerator.sha256Base64Url(rawToken);

        EmailVerificationToken evt =
                EmailVerificationToken.issue(user.id(), tokenHash, now, now.plus(Duration.ofMinutes(30)));
        tokenRepository.save(evt);

        verificationEmailPublisher.publishSendVerificationEmail(user.id().value(), user.email().value(), rawToken);
    }

    public record Command(String email) {
    }
}
