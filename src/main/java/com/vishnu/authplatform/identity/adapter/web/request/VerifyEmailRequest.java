package com.vishnu.authplatform.identity.adapter.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Email verification request")
public record VerifyEmailRequest(
        @Schema(description = "Verification token from email")
        @NotBlank String token
) {
}
