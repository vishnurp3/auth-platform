package com.vishnu.authplatform.identity.adapter.web.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "User registration response")
public record RegisterResponse(
        @Schema(description = "Unique user identifier") UUID userId,
        @Schema(description = "User's email address") String email,
        @Schema(description = "User account status", example = "PENDING_VERIFICATION") String status
) {
}
