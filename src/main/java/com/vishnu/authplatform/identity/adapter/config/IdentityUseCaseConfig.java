package com.vishnu.authplatform.identity.adapter.config;

import com.vishnu.authplatform.identity.application.RegisterUserUseCase;
import com.vishnu.authplatform.identity.application.ResendVerificationEmailUseCase;
import com.vishnu.authplatform.identity.application.VerifyEmailUseCase;
import com.vishnu.authplatform.identity.application.port.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.Duration;

@Configuration
public class IdentityUseCaseConfig {

    @Bean
    public RegisterUserUseCase registerUserUseCase(
            UserRepository userRepository,
            EmailVerificationTokenRepository tokenRepository,
            PasswordHasher passwordHasher,
            TokenGenerator tokenGenerator,
            VerificationEmailPublisher verificationEmailPublisher,
            Clock clock
    ) {
        return new RegisterUserUseCase(
                userRepository,
                tokenRepository,
                passwordHasher,
                tokenGenerator,
                verificationEmailPublisher,
                clock,
                Duration.ofMinutes(30)
        );
    }

    @Bean
    public VerifyEmailUseCase verifyEmailUseCase(
            EmailVerificationTokenRepository tokenRepository,
            UserRepository userRepository,
            TokenGenerator tokenGenerator,
            Clock clock
    ) {
        return new VerifyEmailUseCase(tokenRepository, userRepository, tokenGenerator, clock);
    }

    @Bean
    public ResendVerificationEmailUseCase resendVerificationEmailUseCase(
            UserRepository userRepository,
            EmailVerificationTokenRepository tokenRepository,
            TokenGenerator tokenGenerator,
            VerificationEmailPublisher verificationEmailPublisher,
            Clock clock,
            @Value("${app.verification.resend.min-interval-seconds:60}") long minIntervalSeconds,
            @Value("${app.verification.resend.max-per-hour:5}") int maxPerHour
    ) {
        return new ResendVerificationEmailUseCase(
                userRepository,
                tokenRepository,
                tokenGenerator,
                verificationEmailPublisher,
                clock,
                Duration.ofSeconds(minIntervalSeconds),
                Duration.ofHours(1),
                maxPerHour
        );
    }
}
