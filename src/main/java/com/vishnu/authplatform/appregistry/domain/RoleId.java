package com.vishnu.authplatform.appregistry.domain;

import java.util.UUID;

public record RoleId(UUID value) {
    public static RoleId newId() {
        return new RoleId(UUID.randomUUID());
    }
}
