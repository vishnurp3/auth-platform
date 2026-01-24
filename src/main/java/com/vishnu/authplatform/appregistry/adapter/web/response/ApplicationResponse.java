package com.vishnu.authplatform.appregistry.adapter.web.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Application response")
public record ApplicationResponse(
        @Schema(description = "Unique application identifier") UUID applicationId,
        @Schema(description = "Normalized application code (uppercase)") String applicationCode,
        @Schema(description = "Application name") String name,
        @Schema(description = "Application description") String description,
        @Schema(description = "Application status") String status,
        @Schema(description = "Creation timestamp") Instant createdAt,
        @Schema(description = "Last update timestamp") Instant updatedAt
) {
}
