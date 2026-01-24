package com.vishnu.authplatform.appregistry.adapter.web.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Role response")
public record RoleResponse(
        @Schema(description = "Unique role identifier") UUID roleId,
        @Schema(description = "Application identifier this role belongs to") UUID applicationId,
        @Schema(description = "Application code this role belongs to") String applicationCode,
        @Schema(description = "Normalized role code (uppercase)") String roleCode,
        @Schema(description = "Display name of the role") String displayName,
        @Schema(description = "Role description") String description,
        @Schema(description = "Role status") String status,
        @Schema(description = "Creation timestamp") Instant createdAt,
        @Schema(description = "Last update timestamp") Instant updatedAt
) {
}
