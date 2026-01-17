CREATE INDEX idx_evt_user_created_at
    ON email_verification_tokens (user_id, created_at);

CREATE INDEX idx_evt_created_at
    ON email_verification_tokens (created_at);
