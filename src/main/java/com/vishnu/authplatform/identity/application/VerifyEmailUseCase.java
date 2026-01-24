package com.vishnu.authplatform.identity.application;

import com.vishnu.authplatform.identity.application.command.VerifyEmailCommand;
import com.vishnu.authplatform.identity.application.port.EmailVerificationTokenRepository;
import com.vishnu.authplatform.identity.application.port.UserRepository;
import com.vishnu.authplatform.identity.application.result.VerifyEmailResult;
import com.vishnu.authplatform.identity.domain.EmailVerificationToken;
import com.vishnu.authplatform.identity.domain.User;
import com.vishnu.authplatform.identity.domain.VerificationToken;
import lombok.RequiredArgsConstructor;

import java.time.Clock;
import java.time.Instant;

@RequiredArgsConstructor
public final class VerifyEmailUseCase {

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final VerificationTokenService verificationTokenService;
    private final Clock clock;

    public VerifyEmailResult execute(VerifyEmailCommand cmd) {
        VerificationToken verificationToken = verificationTokenService.parseToken(cmd.token());

        EmailVerificationToken token = tokenRepository.findById(verificationToken.tokenId())
                .orElseThrow(() -> new IllegalStateException("invalid token"));

        Instant now = Instant.now(clock);
        token.validateForUse(now);

        if (!verificationTokenService.verifySecret(verificationToken.secret(), token.tokenHash())) {
            throw new IllegalStateException("invalid token");
        }

        User user = userRepository.findById(token.userId())
                .orElseThrow(() -> new IllegalStateException("user not found"));

        User activated = user.activate(now);
        userRepository.save(activated);

        tokenRepository.save(token.markUsed(now));

        return new VerifyEmailResult(activated.id().value(), activated.status());
    }
}
