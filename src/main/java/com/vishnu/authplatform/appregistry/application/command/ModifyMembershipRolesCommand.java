package com.vishnu.authplatform.appregistry.application.command;

import java.util.List;
import java.util.UUID;

public record ModifyMembershipRolesCommand(
        UUID membershipId,
        UUID userId,
        String applicationCode,
        List<String> roleCodes,
        List<String> addRoleCodes,
        List<String> removeRoleCodes
) {
    public static ModifyMembershipRolesCommand replace(UUID membershipId, List<String> roleCodes) {
        return new ModifyMembershipRolesCommand(membershipId, null, null, roleCodes, null, null);
    }

    public static ModifyMembershipRolesCommand replace(UUID userId, String applicationCode, List<String> roleCodes) {
        return new ModifyMembershipRolesCommand(null, userId, applicationCode, roleCodes, null, null);
    }

    public static ModifyMembershipRolesCommand patch(UUID membershipId, List<String> addRoleCodes, List<String> removeRoleCodes) {
        return new ModifyMembershipRolesCommand(membershipId, null, null, null, addRoleCodes, removeRoleCodes);
    }

    public static ModifyMembershipRolesCommand patch(UUID userId, String applicationCode, List<String> addRoleCodes, List<String> removeRoleCodes) {
        return new ModifyMembershipRolesCommand(null, userId, applicationCode, null, addRoleCodes, removeRoleCodes);
    }

    public boolean isReplaceMode() {
        return roleCodes != null;
    }

    public boolean isPatchMode() {
        return roleCodes == null && (addRoleCodes != null || removeRoleCodes != null);
    }
}
