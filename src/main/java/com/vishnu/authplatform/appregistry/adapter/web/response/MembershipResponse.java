package com.vishnu.authplatform.appregistry.adapter.web.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(description = "Membership response - represents a user's membership in an application with assigned roles")
public record MembershipResponse(
        @Schema(description = "Unique membership identifier") UUID membershipId,
        @Schema(description = "User identifier") UUID userId,
        @Schema(description = "Application identifier") UUID applicationId,
        @Schema(description = "Application code") String applicationCode,
        @Schema(description = "List of assigned roles") List<AssignedRoleResponse> assignedRoles,
        @Schema(description = "Membership status") String status,
        @Schema(description = "Creation timestamp") Instant createdAt,
        @Schema(description = "Last update timestamp") Instant updatedAt
) {
    @Schema(description = "Assigned role information")
    public record AssignedRoleResponse(
            @Schema(description = "Role identifier") UUID roleId,
            @Schema(description = "Role code") String roleCode,
            @Schema(description = "Role display name") String displayName
    ) {
    }
}
