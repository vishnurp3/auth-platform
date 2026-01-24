package com.vishnu.authplatform.appregistry.domain;

import java.util.UUID;

public record ApplicationId(UUID value) {
    public static ApplicationId newId() {
        return new ApplicationId(UUID.randomUUID());
    }
}
