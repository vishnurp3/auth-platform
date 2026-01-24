package com.vishnu.authplatform.appregistry.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface SpringDataApplicationJpaRepository extends JpaRepository<ApplicationEntity, UUID> {
    boolean existsByCode(String code);

    Optional<ApplicationEntity> findByCode(String code);
}
