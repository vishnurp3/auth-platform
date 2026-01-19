package com.vishnu.authplatform.application.adapter.web;

import com.vishnu.authplatform.application.application.CreateApplicationUseCase;
import com.vishnu.authplatform.application.domain.ApplicationStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/applications")
@RequiredArgsConstructor
@Tag(name = "Application Management", description = "Admin APIs for managing application namespaces")
@SecurityRequirement(name = "AdminApiKey")
public class ApplicationController {

    private final CreateApplicationUseCase createApplicationUseCase;

    @Operation(
            summary = "Create a new application",
            description = "Creates a new application namespace that tokens can be scoped to. " +
                    "The application code is normalized to uppercase and must be unique."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Application created successfully",
                    content = @Content(schema = @Schema(implementation = CreateApplicationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input (e.g., invalid code format, missing required fields)"),
            @ApiResponse(responseCode = "403", description = "Missing or invalid admin API key"),
            @ApiResponse(responseCode = "409", description = "Application with this code already exists")
    })
    @PostMapping
    public ResponseEntity<CreateApplicationResponse> createApplication(
            @Valid @RequestBody CreateApplicationRequest request,
            Principal principal
    ) {
        String adminIdentifier = principal != null ? principal.getName() : "system";

        ApplicationStatus status = null;
        if (request.status() != null) {
            try {
                status = ApplicationStatus.valueOf(request.status().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("invalid status: " + request.status());
            }
        }

        CreateApplicationUseCase.Result result = createApplicationUseCase.execute(
                new CreateApplicationUseCase.Command(
                        request.applicationCode(),
                        request.name(),
                        request.description(),
                        status
                ),
                adminIdentifier
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateApplicationResponse(
                        result.applicationId(),
                        result.applicationCode(),
                        result.name(),
                        result.description(),
                        result.status(),
                        result.createdAt(),
                        result.updatedAt()
                ));
    }

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

    @Schema(description = "Create application response")
    public record CreateApplicationResponse(
            @Schema(description = "Unique application identifier") UUID applicationId,
            @Schema(description = "Normalized application code (uppercase)") String applicationCode,
            @Schema(description = "Application name") String name,
            @Schema(description = "Application description") String description,
            @Schema(description = "Application status") String status,
            @Schema(description = "Creation timestamp") Instant createdAt,
            @Schema(description = "Last update timestamp") Instant updatedAt
    ) {
    }
}
