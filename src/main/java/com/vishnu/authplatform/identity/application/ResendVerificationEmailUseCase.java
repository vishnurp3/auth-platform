package com.vishnu.authplatform.identity.application;

import com.vishnu.authplatform.identity.application.port.EmailVerificationTokenRepository;
import com.vishnu.authplatform.identity.application.port.UserRepository;
import com.vishnu.authplatform.identity.application.port.VerificationEmailPublisher;
import com.vishnu.authplatform.identity.domain.Email;
import com.vishnu.authplatform.identity.domain.User;
import lombok.RequiredArgsConstructor;

import java.time.Clock;
import java.time.Instant;

@RequiredArgsConstructor
public final class ResendVerificationEmailUseCase {

    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository tokenRepository;
    private final VerificationTokenService verificationTokenService;
    private final VerificationEmailPublisher verificationEmailPublisher;
    private final VerificationEmailRateLimiter rateLimiter;
    private final Clock clock;

    public void execute(Command cmd) {
        Email email;
        try {
            email = Email.of(cmd.email());
        } catch (IllegalArgumentException e) {
            return;
        }

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return;
        }

        if (!user.canReceiveVerificationEmail()) {
            return;
        }

        Instant now = Instant.now(clock);

        if (!rateLimiter.isAllowed(user.id(), now)) {
            return;
        }

        VerificationTokenService.IssuedTokenPair tokenPair = verificationTokenService.issueToken(user.id(), now);
        tokenRepository.save(tokenPair.token());

        verificationEmailPublisher.publishSendVerificationEmail(
                user.id().value(),
                user.email().value(),
                tokenPair.verificationToken().encode()
        );
    }

    public record Command(String email) {
    }
}
