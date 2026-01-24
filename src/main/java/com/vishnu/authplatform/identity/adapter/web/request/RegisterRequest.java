package com.vishnu.authplatform.identity.adapter.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "User registration request")
public record RegisterRequest(
        @Schema(description = "User's email address", example = "user@example.com")
        @NotBlank String email,
        @Schema(description = "Password (minimum 10 characters)", example = "SecureP@ss123")
        @NotBlank String password
) {
}
