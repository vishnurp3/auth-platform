package com.vishnu.authplatform.identity.application.port;

import com.vishnu.authplatform.identity.domain.Email;
import com.vishnu.authplatform.identity.domain.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    boolean existsByEmail(Email email);

    User save(User user);

    Optional<User> findByEmail(Email email);

    Optional<User> findById(UUID userId);
}
