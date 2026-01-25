package com.vishnu.authplatform.appregistry.adapter.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

@Schema(description = "Create membership request - assigns a user to an application with optional roles")
public record CreateMembershipRequest(
        @Schema(description = "User ID to assign to the application", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull UUID userId,
        @Schema(description = "List of role codes to assign (optional, can be empty). Roles must exist and be ACTIVE in the application.",
                example = "[\"ADMIN\", \"USER\"]")
        List<String> roleCodes,
        @Schema(description = "Membership status (ACTIVE or INACTIVE). Defaults to ACTIVE.", example = "ACTIVE")
        String status
) {
}
