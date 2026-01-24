package com.vishnu.authplatform.appregistry.adapter.web;

import com.vishnu.authplatform.appregistry.adapter.web.request.CreateApplicationRequest;
import com.vishnu.authplatform.appregistry.adapter.web.request.UpdateApplicationStatusRequest;
import com.vishnu.authplatform.appregistry.adapter.web.response.ApplicationResponse;
import com.vishnu.authplatform.appregistry.application.CreateApplicationUseCase;
import com.vishnu.authplatform.appregistry.application.UpdateApplicationStatusUseCase;
import com.vishnu.authplatform.appregistry.application.command.CreateApplicationCommand;
import com.vishnu.authplatform.appregistry.application.command.UpdateApplicationStatusCommand;
import com.vishnu.authplatform.appregistry.application.result.ApplicationResult;
import com.vishnu.authplatform.appregistry.domain.ApplicationStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/admin/applications")
@RequiredArgsConstructor
@Tag(name = "Application Management", description = "Admin APIs for managing application namespaces")
@SecurityRequirement(name = "AdminApiKey")
public class ApplicationController {

    private final CreateApplicationUseCase createApplicationUseCase;
    private final UpdateApplicationStatusUseCase updateApplicationStatusUseCase;

    @Operation(
            summary = "Create a new application",
            description = "Creates a new application namespace that tokens can be scoped to. " +
                    "The application code is normalized to uppercase and must be unique."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Application created successfully",
                    content = @Content(schema = @Schema(implementation = ApplicationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input (e.g., invalid code format, missing required fields)"),
            @ApiResponse(responseCode = "403", description = "Missing or invalid admin API key"),
            @ApiResponse(responseCode = "409", description = "Application with this code already exists")
    })
    @PostMapping
    public ResponseEntity<ApplicationResponse> createApplication(
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

        ApplicationResult result = createApplicationUseCase.execute(
                new CreateApplicationCommand(
                        request.applicationCode(),
                        request.name(),
                        request.description(),
                        status
                ),
                adminIdentifier
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(result));
    }

    @Operation(
            summary = "Update application status",
            description = "Enables or disables an application. When disabled, the application " +
                    "will not be eligible for token issuance and membership operations."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application status updated successfully",
                    content = @Content(schema = @Schema(implementation = ApplicationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input (e.g., invalid status value)"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid admin API key"),
            @ApiResponse(responseCode = "404", description = "Application not found")
    })
    @PatchMapping("/{applicationCode}/status")
    public ResponseEntity<ApplicationResponse> updateApplicationStatus(
            @PathVariable String applicationCode,
            @Valid @RequestBody UpdateApplicationStatusRequest request
    ) {
        ApplicationStatus newStatus;
        try {
            newStatus = ApplicationStatus.valueOf(request.status().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("invalid status: " + request.status() +
                    ". Must be one of: ACTIVE, DISABLED");
        }

        ApplicationResult result = updateApplicationStatusUseCase.execute(
                new UpdateApplicationStatusCommand(applicationCode, newStatus)
        );

        return ResponseEntity.ok(toResponse(result));
    }

    private ApplicationResponse toResponse(ApplicationResult result) {
        return new ApplicationResponse(
                result.applicationId(),
                result.applicationCode(),
                result.name(),
                result.description(),
                result.status(),
                result.createdAt(),
                result.updatedAt()
        );
    }
}
