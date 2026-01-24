package com.vishnu.authplatform.appregistry.adapter.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Update application status request")
public record UpdateApplicationStatusRequest(
        @Schema(description = "New status for the application (ACTIVE or DISABLED)", example = "DISABLED")
        @NotNull String status
) {
}
