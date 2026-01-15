package com.vishnu.authplatform.identity.application.port;

import java.util.UUID;

public interface VerificationEmailPublisher {
    void publishSendVerificationEmail(UUID userId, String email, String rawToken);
}
