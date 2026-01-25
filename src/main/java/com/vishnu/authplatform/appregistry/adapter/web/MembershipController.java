package com.vishnu.authplatform.appregistry.adapter.web;

import com.vishnu.authplatform.appregistry.adapter.web.request.CreateMembershipRequest;
import com.vishnu.authplatform.appregistry.adapter.web.response.MembershipResponse;
import com.vishnu.authplatform.appregistry.application.CreateMembershipUseCase;
import com.vishnu.authplatform.appregistry.application.command.CreateMembershipCommand;
import com.vishnu.authplatform.appregistry.application.result.MembershipResult;
import com.vishnu.authplatform.appregistry.domain.MembershipStatus;
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
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/applications/{applicationCode}/memberships")
@RequiredArgsConstructor
@Tag(name = "Membership Management", description = "Admin APIs for managing user-application memberships with role assignments")
@SecurityRequirement(name = "AdminApiKey")
public class MembershipController {

    private final CreateMembershipUseCase createMembershipUseCase;

    @Operation(
            summary = "Create a new membership",
            description = "Assigns a user to an application with optional roles. " +
                    "This creates a membership record that enables the user to authenticate against the application. " +
                    "The user must exist and the application must be ACTIVE. " +
                    "All specified roles must exist in the application and be ACTIVE. " +
                    "Roles can be empty - the user will still be able to login but with no roles in their token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Membership created successfully",
                    content = @Content(schema = @Schema(implementation = MembershipResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input (e.g., invalid role code format, invalid status)"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid admin API key"),
            @ApiResponse(responseCode = "404", description = "User, application, or role not found"),
            @ApiResponse(responseCode = "409", description = "Membership already exists for this user and application, or application/role is not active")
    })
    @PostMapping
    public ResponseEntity<MembershipResponse> createMembership(
            @PathVariable String applicationCode,
            @Valid @RequestBody CreateMembershipRequest request,
            Principal principal
    ) {
        String adminIdentifier = principal != null ? principal.getName() : "system";

        MembershipStatus status = null;
        if (request.status() != null) {
            try {
                status = MembershipStatus.valueOf(request.status().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("invalid status: " + request.status());
            }
        }

        MembershipResult result = createMembershipUseCase.execute(
                new CreateMembershipCommand(
                        request.userId(),
                        applicationCode,
                        request.roleCodes(),
                        status
                ),
                adminIdentifier
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(result));
    }

    private MembershipResponse toResponse(MembershipResult result) {
        List<MembershipResponse.AssignedRoleResponse> assignedRoles = result.assignedRoles().stream()
                .map(role -> new MembershipResponse.AssignedRoleResponse(
                        role.roleId(),
                        role.roleCode(),
                        role.displayName()))
                .toList();

        return new MembershipResponse(
                result.membershipId(),
                result.userId(),
                result.applicationId(),
                result.applicationCode(),
                assignedRoles,
                result.status(),
                result.createdAt(),
                result.updatedAt()
        );
    }
}
