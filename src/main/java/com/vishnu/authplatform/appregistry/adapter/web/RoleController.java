package com.vishnu.authplatform.appregistry.adapter.web;

import com.vishnu.authplatform.appregistry.adapter.web.request.CreateRoleRequest;
import com.vishnu.authplatform.appregistry.adapter.web.response.RoleResponse;
import com.vishnu.authplatform.appregistry.application.CreateRoleUseCase;
import com.vishnu.authplatform.appregistry.application.command.CreateRoleCommand;
import com.vishnu.authplatform.appregistry.application.result.RoleResult;
import com.vishnu.authplatform.appregistry.domain.RoleStatus;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/admin/applications/{applicationCode}/roles")
@RequiredArgsConstructor
@Tag(name = "Role Management", description = "Admin APIs for managing application-scoped roles")
@SecurityRequirement(name = "AdminApiKey")
public class RoleController {

    private final CreateRoleUseCase createRoleUseCase;

    @Operation(
            summary = "Create a new role",
            description = "Creates a new role scoped to the specified application. " +
                    "Role codes are unique within an application and normalized to uppercase. " +
                    "Roles can be assigned to user memberships and embedded in JWT claims."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Role created successfully",
                    content = @Content(schema = @Schema(implementation = RoleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input (e.g., invalid role code format, missing required fields)"),
            @ApiResponse(responseCode = "403", description = "Missing or invalid admin API key"),
            @ApiResponse(responseCode = "404", description = "Application not found"),
            @ApiResponse(responseCode = "409", description = "Role with this code already exists in the application")
    })
    @PostMapping
    public ResponseEntity<RoleResponse> createRole(
            @PathVariable String applicationCode,
            @Valid @RequestBody CreateRoleRequest request,
            Principal principal
    ) {
        String adminIdentifier = principal != null ? principal.getName() : "system";

        RoleStatus status = null;
        if (request.status() != null) {
            try {
                status = RoleStatus.valueOf(request.status().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("invalid status: " + request.status());
            }
        }

        RoleResult result = createRoleUseCase.execute(
                new CreateRoleCommand(
                        applicationCode,
                        request.roleCode(),
                        request.displayName(),
                        request.description(),
                        status
                ),
                adminIdentifier
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(result));
    }

    private RoleResponse toResponse(RoleResult result) {
        return new RoleResponse(
                result.roleId(),
                result.applicationId(),
                result.applicationCode(),
                result.roleCode(),
                result.displayName(),
                result.description(),
                result.status(),
                result.createdAt(),
                result.updatedAt()
        );
    }
}
