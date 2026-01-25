package com.vishnu.authplatform.appregistry.application.command;

import com.vishnu.authplatform.appregistry.domain.MembershipStatus;

import java.util.List;
import java.util.UUID;

public record CreateMembershipCommand(
        UUID userId,
        String applicationCode,
        List<String> roleCodes,
        MembershipStatus status
) {
}
