CREATE TABLE users
(
    id            BINARY(16) PRIMARY KEY,
    email         VARCHAR(320) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    status        VARCHAR(30)  NOT NULL,
    created_at    TIMESTAMP(6) NOT NULL,
    updated_at    TIMESTAMP(6) NOT NULL,
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE email_verification_tokens
(
    id         BINARY(16) PRIMARY KEY,
    user_id    BINARY(16)   NOT NULL,
    token_hash VARCHAR(200) NOT NULL,
    expires_at TIMESTAMP(6) NOT NULL,
    used_at    TIMESTAMP(6) NULL,
    created_at TIMESTAMP(6) NOT NULL,
    CONSTRAINT fk_evt_user FOREIGN KEY (user_id) REFERENCES users (id),
    INDEX idx_evt_user_id (user_id),
    INDEX idx_evt_expires_at (expires_at)
);
