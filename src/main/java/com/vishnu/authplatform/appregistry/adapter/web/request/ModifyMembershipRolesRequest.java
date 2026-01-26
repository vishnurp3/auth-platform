package com.vishnu.authplatform.appregistry.adapter.web.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Request to modify user roles within a membership. " +
        "Use 'roleCodes' for complete replacement, or 'addRoleCodes'/'removeRoleCodes' for incremental changes. " +
        "These modes are mutually exclusive.")
public record ModifyMembershipRolesRequest(
        @Schema(description = "Complete list of role codes to replace all current roles. " +
                "Use this for complete replacement. Cannot be used with addRoleCodes/removeRoleCodes.",
                example = "[\"ADMIN\", \"USER\"]")
        List<String> roleCodes,

        @Schema(description = "Role codes to add to the current role set. " +
                "Used for incremental changes (patch mode). Cannot be used with roleCodes.",
                example = "[\"EDITOR\"]")
        List<String> addRoleCodes,

        @Schema(description = "Role codes to remove from the current role set. " +
                "Used for incremental changes (patch mode). Cannot be used with roleCodes.",
                example = "[\"VIEWER\"]")
        List<String> removeRoleCodes
) {
}
