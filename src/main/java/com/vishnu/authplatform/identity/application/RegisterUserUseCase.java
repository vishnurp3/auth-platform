package com.vishnu.authplatform.identity.application;

import com.vishnu.authplatform.identity.application.port.*;
import com.vishnu.authplatform.identity.domain.Email;
import com.vishnu.authplatform.identity.domain.EmailVerificationToken;
import com.vishnu.authplatform.identity.domain.User;
import com.vishnu.authplatform.identity.domain.UserId;
import lombok.RequiredArgsConstructor;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
public final class RegisterUserUseCase {
    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository tokenRepository;
    private final PasswordHasher passwordHasher;
    private final TokenGenerator tokenGenerator;
    private final VerificationEmailPublisher verificationEmailPublisher;
    private final Clock clock;
    private final Duration tokenTtl;

    public Result execute(Command cmd) {
        Email email = Email.of(cmd.email());
        validatePasswordPolicy(cmd.password());

        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("email already registered");
        }

        Instant now = Instant.now(clock);
        User user = User.newPending(UserId.newId(), email, passwordHasher.hash(cmd.password()), now);
        user = userRepository.save(user);

        String rawToken = tokenGenerator.generateOpaqueToken();
        String tokenHash = tokenGenerator.sha256Base64Url(rawToken);
        EmailVerificationToken evt = EmailVerificationToken.issue(user.id(), tokenHash, now, now.plus(tokenTtl));
        tokenRepository.save(evt);

        verificationEmailPublisher.publishSendVerificationEmail(user.id().value(), user.email().value(), rawToken);

        return new Result(user.id().value(), user.email().value(), user.status().name());
    }

    private void validatePasswordPolicy(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("password is required");
        }
        if (password.length() < 10) {
            throw new IllegalArgumentException("password must be at least 10 characters");
        }
    }

    public record Command(String email, String password) {
    }

    public record Result(UUID userId, String email, String status) {
    }
}
