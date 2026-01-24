package com.vishnu.authplatform.appregistry.application.result;

import java.time.Instant;
import java.util.UUID;

public record RoleResult(
        UUID roleId,
        UUID applicationId,
        String applicationCode,
        String roleCode,
        String displayName,
        String description,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
}
