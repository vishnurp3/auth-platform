package com.vishnu.authplatform.identity.adapter.messaging;

import java.util.UUID;

public record SendVerificationEmailMessage(UUID userId, String email, String rawToken) {
}
