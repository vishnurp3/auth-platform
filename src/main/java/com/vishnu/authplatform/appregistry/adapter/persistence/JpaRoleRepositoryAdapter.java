package com.vishnu.authplatform.appregistry.adapter.persistence;

import com.vishnu.authplatform.appregistry.application.port.RoleRepository;
import com.vishnu.authplatform.appregistry.domain.ApplicationId;
import com.vishnu.authplatform.appregistry.domain.Role;
import com.vishnu.authplatform.appregistry.domain.RoleCode;
import com.vishnu.authplatform.appregistry.domain.RoleId;
import com.vishnu.authplatform.appregistry.domain.RoleStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
@RequiredArgsConstructor
public class JpaRoleRepositoryAdapter implements RoleRepository {

    private final SpringDataRoleJpaRepository roleJpa;

    @Override
    public Role save(Role role) {
        RoleEntity entity = new RoleEntity(
                role.id().value(),
                role.applicationId().value(),
                role.code().value(),
                role.displayName(),
                role.description(),
                role.status().name(),
                role.createdBy(),
                role.createdAt(),
                role.updatedAt()
        );

        RoleEntity saved = roleJpa.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Role> findById(RoleId id) {
        return roleJpa.findById(id.value()).map(this::toDomain);
    }

    @Override
    public Optional<Role> findByApplicationIdAndCode(ApplicationId applicationId, RoleCode code) {
        return roleJpa.findByApplicationIdAndCode(applicationId.value(), code.value()).map(this::toDomain);
    }

    @Override
    public boolean existsByApplicationIdAndCode(ApplicationId applicationId, RoleCode code) {
        return roleJpa.existsByApplicationIdAndCode(applicationId.value(), code.value());
    }

    private Role toDomain(RoleEntity entity) {
        return Role.reconstitute(
                new RoleId(entity.getId()),
                new ApplicationId(entity.getApplicationId()),
                new RoleCode(entity.getCode()),
                entity.getDisplayName(),
                entity.getDescription(),
                RoleStatus.valueOf(entity.getStatus()),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
