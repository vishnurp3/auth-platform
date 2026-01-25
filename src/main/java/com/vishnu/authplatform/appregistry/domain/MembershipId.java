package com.vishnu.authplatform.appregistry.domain;

import java.util.UUID;

public record MembershipId(UUID value) {
    public static MembershipId newId() {
        return new MembershipId(UUID.randomUUID());
    }
}
