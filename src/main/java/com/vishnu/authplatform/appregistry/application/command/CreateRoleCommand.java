package com.vishnu.authplatform.appregistry.application.command;

import com.vishnu.authplatform.appregistry.domain.RoleStatus;

public record CreateRoleCommand(
        String applicationCode,
        String roleCode,
        String displayName,
        String description,
        RoleStatus status
) {
}
