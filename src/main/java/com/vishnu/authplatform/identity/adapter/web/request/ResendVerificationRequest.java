package com.vishnu.authplatform.identity.adapter.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Resend verification email request")
public record ResendVerificationRequest(
        @Schema(description = "User's email address", example = "user@example.com")
        @NotBlank String email
) {
}
