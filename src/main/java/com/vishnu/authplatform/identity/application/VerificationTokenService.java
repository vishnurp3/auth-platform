package com.vishnu.authplatform.identity.application;

import com.vishnu.authplatform.identity.application.port.TokenGenerator;
import com.vishnu.authplatform.identity.domain.EmailVerificationToken;
import com.vishnu.authplatform.identity.domain.UserId;
import com.vishnu.authplatform.identity.domain.VerificationToken;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
public final class VerificationTokenService {

    public static final Duration TOKEN_VALIDITY_DURATION = Duration.ofMinutes(30);

    private final TokenGenerator tokenGenerator;

    public IssuedTokenPair issueToken(UserId userId, Instant now) {
        UUID tokenId = UUID.randomUUID();
        String secret = tokenGenerator.generateOpaqueToken();
        String secretHash = tokenGenerator.sha256Base64Url(secret);
        Instant expiresAt = now.plus(TOKEN_VALIDITY_DURATION);

        EmailVerificationToken token = EmailVerificationToken.issue(tokenId, userId, secretHash, now, expiresAt);
        VerificationToken verificationToken = VerificationToken.of(tokenId, secret);

        return new IssuedTokenPair(token, verificationToken);
    }

    public VerificationToken parseToken(String encodedToken) {
        return VerificationToken.parse(encodedToken);
    }

    public boolean verifySecret(String providedSecret, String storedHash) {
        String providedHash = tokenGenerator.sha256Base64Url(providedSecret);
        return constantTimeEquals(providedHash, storedHash);
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }

    public record IssuedTokenPair(EmailVerificationToken token, VerificationToken verificationToken) {
    }
}
