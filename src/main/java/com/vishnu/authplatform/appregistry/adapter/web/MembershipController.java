package com.vishnu.authplatform.appregistry.adapter.web;

import com.vishnu.authplatform.appregistry.adapter.web.request.CreateMembershipRequest;
import com.vishnu.authplatform.appregistry.adapter.web.request.ModifyMembershipRolesRequest;
import com.vishnu.authplatform.appregistry.adapter.web.response.MembershipResponse;
import com.vishnu.authplatform.appregistry.application.CreateMembershipUseCase;
import com.vishnu.authplatform.appregistry.application.ModifyMembershipRolesUseCase;
import com.vishnu.authplatform.appregistry.application.command.CreateMembershipCommand;
import com.vishnu.authplatform.appregistry.application.command.ModifyMembershipRolesCommand;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/applications/{applicationCode}/memberships")
@RequiredArgsConstructor
@Tag(name = "Membership Management", description = "Admin APIs for managing user-application memberships with role assignments")
@SecurityRequirement(name = "AdminApiKey")
public class MembershipController {

    private final CreateMembershipUseCase createMembershipUseCase;
    private final ModifyMembershipRolesUseCase modifyMembershipRolesUseCase;

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

    @Operation(
            summary = "Modify membership roles",
            description = "Modifies the roles assigned to a user within an application membership. " +
                    "Supports two modes: replace (using 'roleCodes' to completely replace all roles) " +
                    "or patch (using 'addRoleCodes' and/or 'removeRoleCodes' for incremental changes). " +
                    "Membership can be identified by membershipId path parameter, or by userId query parameter. " +
                    "All role changes are atomic. Changes affect subsequent token issuance."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Roles modified successfully",
                    content = @Content(schema = @Schema(implementation = MembershipResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input (e.g., both replace and patch modes specified, invalid role code format)"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid admin API key"),
            @ApiResponse(responseCode = "404", description = "Membership, application, or role not found"),
            @ApiResponse(responseCode = "409", description = "Role is not active/assignable")
    })
    @PatchMapping("/{membershipId}/roles")
    public ResponseEntity<MembershipResponse> modifyMembershipRolesByMembershipId(
            @PathVariable String applicationCode,
            @PathVariable UUID membershipId,
            @Valid @RequestBody ModifyMembershipRolesRequest request,
            Principal principal
    ) {
        String adminIdentifier = principal != null ? principal.getName() : "system";

        validateModifyRolesRequest(request);

        ModifyMembershipRolesCommand cmd = buildModifyRolesCommand(membershipId, null, applicationCode, request);

        MembershipResult result = modifyMembershipRolesUseCase.execute(cmd, adminIdentifier);

        return ResponseEntity.ok(toResponse(result));
    }

    @Operation(
            summary = "Modify membership roles by user ID",
            description = "Modifies the roles assigned to a user within an application membership, " +
                    "identified by user ID. " +
                    "Supports two modes: replace (using 'roleCodes' to completely replace all roles) " +
                    "or patch (using 'addRoleCodes' and/or 'removeRoleCodes' for incremental changes). " +
                    "All role changes are atomic. Changes affect subsequent token issuance."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Roles modified successfully",
                    content = @Content(schema = @Schema(implementation = MembershipResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input (e.g., both replace and patch modes specified, invalid role code format)"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid admin API key"),
            @ApiResponse(responseCode = "404", description = "Membership, application, or role not found"),
            @ApiResponse(responseCode = "409", description = "Role is not active/assignable")
    })
    @PatchMapping("/by-user/{userId}/roles")
    public ResponseEntity<MembershipResponse> modifyMembershipRolesByUserId(
            @PathVariable String applicationCode,
            @PathVariable UUID userId,
            @Valid @RequestBody ModifyMembershipRolesRequest request,
            Principal principal
    ) {
        String adminIdentifier = principal != null ? principal.getName() : "system";

        validateModifyRolesRequest(request);

        ModifyMembershipRolesCommand cmd = buildModifyRolesCommand(null, userId, applicationCode, request);

        MembershipResult result = modifyMembershipRolesUseCase.execute(cmd, adminIdentifier);

        return ResponseEntity.ok(toResponse(result));
    }

    private void validateModifyRolesRequest(ModifyMembershipRolesRequest request) {
        boolean hasReplace = request.roleCodes() != null;
        boolean hasPatch = request.addRoleCodes() != null || request.removeRoleCodes() != null;

        if (hasReplace && hasPatch) {
            throw new IllegalArgumentException(
                    "cannot use both replace mode (roleCodes) and patch mode (addRoleCodes/removeRoleCodes) in the same request");
        }

        if (!hasReplace && !hasPatch) {
            throw new IllegalArgumentException(
                    "either roleCodes (replace) or addRoleCodes/removeRoleCodes (patch) must be provided");
        }
    }

    private ModifyMembershipRolesCommand buildModifyRolesCommand(
            UUID membershipId,
            UUID userId,
            String applicationCode,
            ModifyMembershipRolesRequest request
    ) {
        return new ModifyMembershipRolesCommand(
                membershipId,
                userId,
                applicationCode,
                request.roleCodes(),
                request.addRoleCodes(),
                request.removeRoleCodes()
        );
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
