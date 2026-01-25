package com.vishnu.authplatform.appregistry.application.result;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MembershipResult(
        UUID membershipId,
        UUID userId,
        UUID applicationId,
        String applicationCode,
        List<AssignedRole> assignedRoles,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
    public record AssignedRole(
            UUID roleId,
            String roleCode,
            String displayName
    ) {
    }
}
