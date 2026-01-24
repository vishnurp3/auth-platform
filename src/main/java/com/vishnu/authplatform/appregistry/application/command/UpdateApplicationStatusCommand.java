package com.vishnu.authplatform.appregistry.application.command;

import com.vishnu.authplatform.appregistry.domain.ApplicationStatus;

public record UpdateApplicationStatusCommand(
        String applicationCode,
        ApplicationStatus newStatus
) {
}
