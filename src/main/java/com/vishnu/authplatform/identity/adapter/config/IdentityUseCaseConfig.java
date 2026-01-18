package com.vishnu.authplatform.identity.adapter.config;

import com.vishnu.authplatform.identity.application.RegisterUserUseCase;
import com.vishnu.authplatform.identity.application.ResendVerificationEmailUseCase;
import com.vishnu.authplatform.identity.application.VerificationEmailRateLimiter;
import com.vishnu.authplatform.identity.application.VerificationTokenService;
import com.vishnu.authplatform.identity.application.VerifyEmailUseCase;
import com.vishnu.authplatform.identity.application.port.EmailVerificationTokenRepository;
import com.vishnu.authplatform.identity.application.port.PasswordHasher;
import com.vishnu.authplatform.identity.application.port.TokenGenerator;
import com.vishnu.authplatform.identity.application.port.UserRepository;
import com.vishnu.authplatform.identity.application.port.VerificationEmailPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.Duration;

@Configuration
public class IdentityUseCaseConfig {

    @Bean
    public VerificationTokenService verificationTokenService(TokenGenerator tokenGenerator) {
        return new VerificationTokenService(tokenGenerator);
    }

    @Bean
    public VerificationEmailRateLimiter verificationEmailRateLimiter(
            EmailVerificationTokenRepository tokenRepository,
            @Value("${app.verification.resend.min-interval-seconds:60}") long minIntervalSeconds,
            @Value("${app.verification.resend.max-per-hour:5}") int maxPerHour
    ) {
        return new VerificationEmailRateLimiter(
                tokenRepository,
                Duration.ofSeconds(minIntervalSeconds),
                Duration.ofHours(1),
                maxPerHour
        );
    }

    @Bean
    public RegisterUserUseCase registerUserUseCase(
            UserRepository userRepository,
            EmailVerificationTokenRepository tokenRepository,
            PasswordHasher passwordHasher,
            VerificationTokenService verificationTokenService,
            VerificationEmailPublisher verificationEmailPublisher,
            Clock clock
    ) {
        return new RegisterUserUseCase(
                userRepository,
                tokenRepository,
                passwordHasher,
                verificationTokenService,
                verificationEmailPublisher,
                clock
        );
    }

    @Bean
    public VerifyEmailUseCase verifyEmailUseCase(
            EmailVerificationTokenRepository tokenRepository,
            UserRepository userRepository,
            VerificationTokenService verificationTokenService,
            Clock clock
    ) {
        return new VerifyEmailUseCase(tokenRepository, userRepository, verificationTokenService, clock);
    }

    @Bean
    public ResendVerificationEmailUseCase resendVerificationEmailUseCase(
            UserRepository userRepository,
            EmailVerificationTokenRepository tokenRepository,
            VerificationTokenService verificationTokenService,
            VerificationEmailPublisher verificationEmailPublisher,
            VerificationEmailRateLimiter rateLimiter,
            Clock clock
    ) {
        return new ResendVerificationEmailUseCase(
                userRepository,
                tokenRepository,
                verificationTokenService,
                verificationEmailPublisher,
                rateLimiter,
                clock
        );
    }
}
