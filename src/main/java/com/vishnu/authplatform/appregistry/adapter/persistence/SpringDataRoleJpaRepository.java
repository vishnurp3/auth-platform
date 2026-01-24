package com.vishnu.authplatform.appregistry.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface SpringDataRoleJpaRepository extends JpaRepository<RoleEntity, UUID> {
    boolean existsByApplicationIdAndCode(UUID applicationId, String code);

    Optional<RoleEntity> findByApplicationIdAndCode(UUID applicationId, String code);
}
