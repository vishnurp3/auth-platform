package com.vishnu.authplatform.appregistry.application.result;

import java.time.Instant;
import java.util.UUID;

public record ApplicationResult(
        UUID applicationId,
        String applicationCode,
        String name,
        String description,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
}
