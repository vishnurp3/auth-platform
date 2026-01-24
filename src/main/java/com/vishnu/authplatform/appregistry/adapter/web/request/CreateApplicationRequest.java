package com.vishnu.authplatform.appregistry.adapter.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Create application request")
public record CreateApplicationRequest(
        @Schema(description = "Unique application code (2-50 chars, letters/digits/underscores, starts with letter). Will be normalized to uppercase.",
                example = "my_app")
        @NotBlank String applicationCode,
        @Schema(description = "Human-readable application name", example = "My Application")
        @NotBlank String name,
        @Schema(description = "Optional description of the application", example = "Main customer-facing application")
        String description,
        @Schema(description = "Application status (ACTIVE or DISABLED). Defaults to ACTIVE.", example = "ACTIVE")
        String status
) {
}
