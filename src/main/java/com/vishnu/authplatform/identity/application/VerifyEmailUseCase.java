package com.vishnu.authplatform.identity.application;

import com.vishnu.authplatform.identity.application.port.EmailVerificationTokenRepository;
import com.vishnu.authplatform.identity.application.port.TokenGenerator;
import com.vishnu.authplatform.identity.application.port.UserRepository;
import com.vishnu.authplatform.identity.domain.EmailVerificationToken;
import com.vishnu.authplatform.identity.domain.User;
import lombok.RequiredArgsConstructor;

import java.time.Clock;
import java.time.Instant;

@RequiredArgsConstructor
public final class VerifyEmailUseCase {

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final TokenGenerator tokenGenerator;
    private final Clock clock;

    public Result execute(Command cmd) {
        if (cmd.token() == null || cmd.token().isBlank()) {
            throw new IllegalArgumentException("token is required");
        }

        Instant now = Instant.now(clock);
        String tokenHash = tokenGenerator.sha256Base64Url(cmd.token());

        EmailVerificationToken evt =
                tokenRepository.findByTokenHash(tokenHash).orElseThrow(() -> new IllegalStateException("invalid token"));

        if (evt.isUsed()) {
            throw new IllegalStateException("token already used");
        }
        if (evt.isExpired(now)) {
            throw new IllegalStateException("token expired");
        }

        User user =
                userRepository.findById(evt.userId().value()).orElseThrow(() -> new IllegalStateException("user not found"));

        User activated = user.activate(now);
        userRepository.save(activated);

        tokenRepository.save(evt.markUsed(now));

        return new Result(activated.id().value().toString(), activated.status().name());
    }

    public record Command(String token) {
    }

    public record Result(String userId, String status) {
    }
}
