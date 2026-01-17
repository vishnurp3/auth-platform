package com.vishnu.authplatform.identity.adapter.persistence;

import com.vishnu.authplatform.identity.application.port.EmailVerificationTokenRepository;
import com.vishnu.authplatform.identity.application.port.UserRepository;
import com.vishnu.authplatform.identity.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
@RequiredArgsConstructor
public class JpaIdentityRepositoryAdapter implements UserRepository, EmailVerificationTokenRepository {

    private final SpringDataUserJpaRepository userJpa;
    private final SpringDataEmailVerificationTokenJpaRepository tokenJpa;


    @Override
    public boolean existsByEmail(Email email) {
        return userJpa.existsByEmail(email.value());
    }

    @Override
    public User save(User user) {
        UserEntity entity =
                new UserEntity(
                        user.id().value(),
                        user.email().value(),
                        user.passwordHash(),
                        user.status().name(),
                        user.createdAt(),
                        user.updatedAt()
                );

        UserEntity saved = userJpa.save(entity);
        return User.reconstitute(new UserId(saved.getId()), Email.of(saved.getEmail()), saved.getPasswordHash(),
                UserStatus.valueOf(saved.getStatus()), saved.getCreatedAt(), saved.getUpdatedAt());
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return userJpa.findByEmail(email.value()).map(this::toDomainUser);
    }

    @Override
    public Optional<User> findById(UUID userId) {
        return userJpa.findById(userId).map(this::toDomainUser);
    }

    @Override
    public void save(EmailVerificationToken token) {
        EmailVerificationTokenEntity entity =
                new EmailVerificationTokenEntity(
                        token.id(),
                        token.userId().value(),
                        token.tokenHash(),
                        token.expiresAt(),
                        token.usedAt(),
                        token.createdAt()
                );
        tokenJpa.save(entity);
    }

    @Override
    public Optional<EmailVerificationToken> findByTokenHash(String tokenHash) {
        return tokenJpa.findByTokenHash(tokenHash).map(saved ->
                EmailVerificationToken.reconstitute(
                        saved.getId(),
                        new UserId(saved.getUserId()),
                        saved.getTokenHash(),
                        saved.getExpiresAt(),
                        saved.getUsedAt(),
                        saved.getCreatedAt()
                )
        );
    }

    @Override
    public Optional<Instant> findLatestCreatedAtByUserId(UUID userId) {
        return tokenJpa.findLatestCreatedAtByUserId(userId);
    }

    @Override
    public long countIssuedSince(UUID userId, Instant since) {
        return tokenJpa.countByUserIdAndCreatedAtGreaterThanEqual(userId, since);
    }


    private User toDomainUser(UserEntity e) {
        return User.reconstitute(new UserId(e.getId()), Email.of(e.getEmail()), e.getPasswordHash(),
                UserStatus.valueOf(e.getStatus()), e.getCreatedAt(), e.getUpdatedAt());
    }
}
