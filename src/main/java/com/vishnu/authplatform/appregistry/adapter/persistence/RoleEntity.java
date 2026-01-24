package com.vishnu.authplatform.appregistry.adapter.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "roles", uniqueConstraints = {
        @UniqueConstraint(name = "uk_roles_app_code", columnNames = {"application_id", "code"})
})
@Getter
public class RoleEntity {
    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "application_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID applicationId;

    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Column(name = "description", length = 500)
    private String description;

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

    protected RoleEntity() {
    }

    public RoleEntity(
            UUID id,
            UUID applicationId,
            String code,
            String displayName,
            String description,
            String status,
            String createdBy,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.applicationId = applicationId;
        this.code = code;
        this.displayName = displayName;
        this.description = description;
        this.status = status;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
