package com.vishnu.authplatform.appregistry.adapter.persistence;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "memberships", uniqueConstraints = {
        @UniqueConstraint(name = "uk_memberships_user_app", columnNames = {"user_id", "application_id"})
})
@Getter
public class MembershipEntity {
    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "user_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID userId;

    @Column(name = "application_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID applicationId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "membership_roles",
            joinColumns = @JoinColumn(name = "membership_id")
    )
    @Column(name = "role_id", columnDefinition = "BINARY(16)")
    private Set<UUID> roleIds = new HashSet<>();

    @Setter
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "created_by", nullable = false, length = 100)
    private String createdBy;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Setter
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected MembershipEntity() {
    }

    public MembershipEntity(
            UUID id,
            UUID userId,
            UUID applicationId,
            Set<UUID> roleIds,
            String status,
            String createdBy,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.applicationId = applicationId;
        this.roleIds = roleIds != null ? new HashSet<>(roleIds) : new HashSet<>();
        this.status = status;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
