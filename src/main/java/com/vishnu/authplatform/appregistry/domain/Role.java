package com.vishnu.authplatform.appregistry.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.time.Instant;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Accessors(fluent = true)
public final class Role {
    @NonNull
    private final RoleId id;
    @NonNull
    private final ApplicationId applicationId;
    @NonNull
    private final RoleCode code;
    @NonNull
    private final String displayName;
    private final String description;
    @NonNull
    private final RoleStatus status;
    @NonNull
    private final String createdBy;
    @NonNull
    private final Instant createdAt;
    @NonNull
    private final Instant updatedAt;

    public static Role create(
            RoleId id,
            ApplicationId applicationId,
            RoleCode code,
            String displayName,
            String description,
            RoleStatus status,
            String createdBy,
            Instant now
    ) {
        if (displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("displayName is required");
        }
        if (displayName.length() > 100) {
            throw new IllegalArgumentException("displayName must not exceed 100 characters");
        }
        if (description != null && description.length() > 500) {
            throw new IllegalArgumentException("description must not exceed 500 characters");
        }
        if (createdBy == null || createdBy.isBlank()) {
            throw new IllegalArgumentException("createdBy is required");
        }
        return new Role(id, applicationId, code, displayName.trim(), description, status, createdBy, now, now);
    }

    public static Role reconstitute(
            RoleId id,
            ApplicationId applicationId,
            RoleCode code,
            String displayName,
            String description,
            RoleStatus status,
            String createdBy,
            Instant createdAt,
            Instant updatedAt
    ) {
        return new Role(id, applicationId, code, displayName, description, status, createdBy, createdAt, updatedAt);
    }

    public boolean isActive() {
        return this.status == RoleStatus.ACTIVE;
    }

    public boolean isAssignable() {
        return isActive();
    }

    public Role disable(Instant now) {
        if (this.status == RoleStatus.DISABLED) {
            throw new IllegalStateException("role is already disabled");
        }
        return new Role(this.id, this.applicationId, this.code, this.displayName, this.description,
                RoleStatus.DISABLED, this.createdBy, this.createdAt, now);
    }

    public Role enable(Instant now) {
        if (this.status == RoleStatus.ACTIVE) {
            throw new IllegalStateException("role is already active");
        }
        return new Role(this.id, this.applicationId, this.code, this.displayName, this.description,
                RoleStatus.ACTIVE, this.createdBy, this.createdAt, now);
    }
}
