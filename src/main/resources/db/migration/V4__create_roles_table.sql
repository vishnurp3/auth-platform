CREATE TABLE roles
(
    id             BINARY(16) PRIMARY KEY,
    application_id BINARY(16)   NOT NULL,
    code           VARCHAR(50)  NOT NULL,
    display_name   VARCHAR(100) NOT NULL,
    description    VARCHAR(500) NULL,
    status         VARCHAR(20)  NOT NULL,
    created_by     VARCHAR(100) NOT NULL,
    created_at     TIMESTAMP(6) NOT NULL,
    updated_at     TIMESTAMP(6) NOT NULL,
    CONSTRAINT fk_roles_application FOREIGN KEY (application_id) REFERENCES applications (id),
    CONSTRAINT uk_roles_app_code UNIQUE (application_id, code),
    INDEX idx_roles_application_id (application_id),
    INDEX idx_roles_status (status)
);
