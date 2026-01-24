package com.vishnu.authplatform.appregistry.adapter.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Create role request")
public record CreateRoleRequest(
        @Schema(description = "Unique role code within the application (2-50 chars, letters/digits/underscores, starts with letter). Will be normalized to uppercase.",
                example = "ADMIN")
        @NotBlank String roleCode,
        @Schema(description = "Human-readable display name for the role", example = "Administrator")
        @NotBlank String displayName,
        @Schema(description = "Optional description of the role", example = "Full administrative access to all features")
        String description,
        @Schema(description = "Role status (ACTIVE or DISABLED). Defaults to ACTIVE.", example = "ACTIVE")
        String status
) {
}
