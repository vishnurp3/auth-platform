package com.vishnu.authplatform.identity.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

interface SpringDataEmailVerificationTokenJpaRepository extends JpaRepository<EmailVerificationTokenEntity, UUID> {

    @Query("select max(t.createdAt) from EmailVerificationTokenEntity t where t.userId = :userId")
    Optional<Instant> findLatestCreatedAtByUserId(UUID userId);

    long countByUserIdAndCreatedAtGreaterThanEqual(UUID userId, Instant since);
}
