package com.vishnu.authplatform.appregistry.domain;

import com.vishnu.authplatform.identity.domain.UserId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Accessors(fluent = true)
public final class Membership {
    @NonNull
    private final MembershipId id;
    @NonNull
    private final UserId userId;
    @NonNull
    private final ApplicationId applicationId;
    @NonNull
    private final Set<RoleId> roleIds;
    @NonNull
    private final MembershipStatus status;
    @NonNull
    private final String createdBy;
    @NonNull
    private final Instant createdAt;
    @NonNull
    private final Instant updatedAt;

    public static Membership create(
            MembershipId id,
            UserId userId,
            ApplicationId applicationId,
            Set<RoleId> roleIds,
            MembershipStatus status,
            String createdBy,
            Instant now
    ) {
        if (createdBy == null || createdBy.isBlank()) {
            throw new IllegalArgumentException("createdBy is required");
        }
        return reconstitute(id, userId, applicationId, roleIds, status, createdBy, now, now);
    }

    public static Membership reconstitute(
            MembershipId id,
            UserId userId,
            ApplicationId applicationId,
            Set<RoleId> roleIds,
            MembershipStatus status,
            String createdBy,
            Instant createdAt,
            Instant updatedAt
    ) {
        Set<RoleId> roles = roleIds != null ? new HashSet<>(roleIds) : new HashSet<>();
        return new Membership(id, userId, applicationId, Collections.unmodifiableSet(roles), status, createdBy, createdAt, updatedAt);
    }

    public boolean isActive() {
        return this.status == MembershipStatus.ACTIVE;
    }

    public Membership deactivate(Instant now) {
        if (this.status == MembershipStatus.INACTIVE) {
            throw new IllegalStateException("membership is already inactive");
        }
        return new Membership(this.id, this.userId, this.applicationId, this.roleIds,
                MembershipStatus.INACTIVE, this.createdBy, this.createdAt, now);
    }

    public Membership activate(Instant now) {
        if (this.status == MembershipStatus.ACTIVE) {
            throw new IllegalStateException("membership is already active");
        }
        return new Membership(this.id, this.userId, this.applicationId, this.roleIds,
                MembershipStatus.ACTIVE, this.createdBy, this.createdAt, now);
    }

    public Membership updateRoles(Set<RoleId> newRoleIds, Instant now) {
        Set<RoleId> roles = newRoleIds != null ? new HashSet<>(newRoleIds) : new HashSet<>();
        return new Membership(this.id, this.userId, this.applicationId, Collections.unmodifiableSet(roles),
                this.status, this.createdBy, this.createdAt, now);
    }
}
