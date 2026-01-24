package com.vishnu.authplatform.identity.application;

import com.vishnu.authplatform.identity.application.command.RegisterUserCommand;
import com.vishnu.authplatform.identity.application.port.EmailVerificationTokenRepository;
import com.vishnu.authplatform.identity.application.port.PasswordHasher;
import com.vishnu.authplatform.identity.application.port.UserRepository;
import com.vishnu.authplatform.identity.application.port.VerificationEmailPublisher;
import com.vishnu.authplatform.identity.application.result.IssuedTokenPair;
import com.vishnu.authplatform.identity.application.result.RegisterUserResult;
import com.vishnu.authplatform.identity.domain.Email;
import com.vishnu.authplatform.identity.domain.Password;
import com.vishnu.authplatform.identity.domain.User;
import com.vishnu.authplatform.identity.domain.UserId;
import lombok.RequiredArgsConstructor;

import java.time.Clock;
import java.time.Instant;

@RequiredArgsConstructor
public final class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository tokenRepository;
    private final PasswordHasher passwordHasher;
    private final VerificationTokenService verificationTokenService;
    private final VerificationEmailPublisher verificationEmailPublisher;
    private final Clock clock;

    public RegisterUserResult execute(RegisterUserCommand cmd) {
        Email email = new Email(cmd.email());
        Password password = new Password(cmd.password());

        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("email already registered");
        }

        Instant now = Instant.now(clock);
        User user = User.newPending(UserId.newId(), email, passwordHasher.hash(password.value()), now);
        user = userRepository.save(user);

        IssuedTokenPair tokenPair = verificationTokenService.issueToken(user.id(), now);
        tokenRepository.save(tokenPair.token());

        verificationEmailPublisher.publishSendVerificationEmail(
                user.id().value(),
                user.email().value(),
                tokenPair.verificationToken().encode()
        );

        return new RegisterUserResult(user.id().value(), user.email().value(), user.status());
    }
}
