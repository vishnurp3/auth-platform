package com.vishnu.authplatform.identity.application.result;

import com.vishnu.authplatform.identity.domain.UserStatus;

import java.util.UUID;

public record RegisterUserResult(UUID userId, String email, UserStatus status) {
}
