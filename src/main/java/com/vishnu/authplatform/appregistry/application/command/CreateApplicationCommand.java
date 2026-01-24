package com.vishnu.authplatform.appregistry.application.command;

import com.vishnu.authplatform.appregistry.domain.ApplicationStatus;

public record CreateApplicationCommand(
        String applicationCode,
        String name,
        String description,
        ApplicationStatus status
) {
}
