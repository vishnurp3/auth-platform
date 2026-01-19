package com.vishnu.authplatform.application.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.time.Instant;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Accessors(fluent = true)
public final class Application {
    @NonNull
    private final ApplicationId id;
    @NonNull
    private final ApplicationCode code;
    @NonNull
    private final String name;
    private final String description;
    @NonNull
    private final ApplicationStatus status;
    @NonNull
    private final String createdBy;
    @NonNull
    private final Instant createdAt;
    @NonNull
    private final Instant updatedAt;

    public static Application create(
            ApplicationId id,
            ApplicationCode code,
            String name,
            String description,
            ApplicationStatus status,
            String createdBy,
            Instant now
    ) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("name must not exceed 100 characters");
        }
        if (description != null && description.length() > 500) {
            throw new IllegalArgumentException("description must not exceed 500 characters");
        }
        if (createdBy == null || createdBy.isBlank()) {
            throw new IllegalArgumentException("createdBy is required");
        }
        return new Application(id, code, name.trim(), description, status, createdBy, now, now);
    }

    public static Application reconstitute(
            ApplicationId id,
            ApplicationCode code,
            String name,
            String description,
            ApplicationStatus status,
            String createdBy,
            Instant createdAt,
            Instant updatedAt
    ) {
        return new Application(id, code, name, description, status, createdBy, createdAt, updatedAt);
    }

    public boolean isActive() {
        return this.status == ApplicationStatus.ACTIVE;
    }

    public Application disable(Instant now) {
        if (this.status == ApplicationStatus.DISABLED) {
            throw new IllegalStateException("application is already disabled");
        }
        return new Application(this.id, this.code, this.name, this.description,
                ApplicationStatus.DISABLED, this.createdBy, this.createdAt, now);
    }

    public Application enable(Instant now) {
        if (this.status == ApplicationStatus.ACTIVE) {
            throw new IllegalStateException("application is already active");
        }
        return new Application(this.id, this.code, this.name, this.description,
                ApplicationStatus.ACTIVE, this.createdBy, this.createdAt, now);
    }
}
