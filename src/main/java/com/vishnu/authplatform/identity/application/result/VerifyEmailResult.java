package com.vishnu.authplatform.identity.application.result;

import com.vishnu.authplatform.identity.domain.UserStatus;

import java.util.UUID;

public record VerifyEmailResult(UUID userId, UserStatus status) {
}
