CREATE TABLE applications
(
    id          BINARY(16) PRIMARY KEY,
    code        VARCHAR(50)  NOT NULL,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(500) NULL,
    status      VARCHAR(20)  NOT NULL,
    created_by  VARCHAR(100) NOT NULL,
    created_at  TIMESTAMP(6) NOT NULL,
    updated_at  TIMESTAMP(6) NOT NULL,
    CONSTRAINT uk_applications_code UNIQUE (code),
    INDEX idx_applications_status (status)
);
