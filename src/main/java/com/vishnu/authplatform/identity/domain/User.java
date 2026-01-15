package com.vishnu.authplatform.identity.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.time.Instant;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Accessors(fluent = true)
public final class User {
    @NonNull
    private final UserId id;
    @NonNull
    private final Email email;
    @NonNull
    private final String passwordHash;
    @NonNull
    private final UserStatus status;
    @NonNull
    private final Instant createdAt;
    @NonNull
    private final Instant updatedAt;

    public static User newPending(UserId id, Email email, String passwordHash, Instant now) {
        return new User(id, email, passwordHash, UserStatus.PENDING_VERIFICATION, now, now);
    }

    public static User reconstitute(UserId id, Email email, String passwordHash, UserStatus status, Instant createdAt, Instant updatedAt) {
        return new User(id, email, passwordHash, status, createdAt, updatedAt);
    }

    public User activate(Instant now) {
        if (this.status != UserStatus.PENDING_VERIFICATION) {
            return this;
        }
        return new User(this.id, this.email, this.passwordHash, UserStatus.ACTIVE, this.createdAt, now);
    }
}
