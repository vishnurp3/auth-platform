package com.vishnu.authplatform.appregistry.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface SpringDataMembershipJpaRepository extends JpaRepository<MembershipEntity, UUID> {
    boolean existsByUserIdAndApplicationId(UUID userId, UUID applicationId);

    Optional<MembershipEntity> findByUserIdAndApplicationId(UUID userId, UUID applicationId);
}
