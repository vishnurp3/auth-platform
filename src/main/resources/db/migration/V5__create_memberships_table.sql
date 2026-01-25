CREATE TABLE memberships
(
    id             BINARY(16) PRIMARY KEY,
    user_id        BINARY(16)   NOT NULL,
    application_id BINARY(16)   NOT NULL,
    status         VARCHAR(20)  NOT NULL,
    created_by     VARCHAR(100) NOT NULL,
    created_at     TIMESTAMP(6) NOT NULL,
    updated_at     TIMESTAMP(6) NOT NULL,
    CONSTRAINT fk_memberships_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_memberships_application FOREIGN KEY (application_id) REFERENCES applications (id),
    CONSTRAINT uk_memberships_user_app UNIQUE (user_id, application_id),
    INDEX idx_memberships_user_id (user_id),
    INDEX idx_memberships_application_id (application_id),
    INDEX idx_memberships_status (status)
);

CREATE TABLE membership_roles
(
    membership_id BINARY(16) NOT NULL,
    role_id       BINARY(16) NOT NULL,
    PRIMARY KEY (membership_id, role_id),
    CONSTRAINT fk_mr_membership FOREIGN KEY (membership_id) REFERENCES memberships (id) ON DELETE CASCADE,
    CONSTRAINT fk_mr_role FOREIGN KEY (role_id) REFERENCES roles (id),
    INDEX idx_mr_role_id (role_id)
);
