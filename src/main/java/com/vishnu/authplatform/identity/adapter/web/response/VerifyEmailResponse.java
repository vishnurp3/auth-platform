package com.vishnu.authplatform.identity.adapter.web.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Email verification response")
public record VerifyEmailResponse(
        @Schema(description = "Unique user identifier") UUID userId,
        @Schema(description = "Updated user account status", example = "ACTIVE") String status
) {
}
