package com.vishnu.authplatform.appregistry.adapter.persistence;

import com.vishnu.authplatform.appregistry.application.port.ApplicationRepository;
import com.vishnu.authplatform.appregistry.domain.Application;
import com.vishnu.authplatform.appregistry.domain.ApplicationCode;
import com.vishnu.authplatform.appregistry.domain.ApplicationId;
import com.vishnu.authplatform.appregistry.domain.ApplicationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
@RequiredArgsConstructor
public class JpaApplicationRepositoryAdapter implements ApplicationRepository {

    private final SpringDataApplicationJpaRepository applicationJpa;

    @Override
    public Application save(Application application) {
        ApplicationEntity entity = new ApplicationEntity(
                application.id().value(),
                application.code().value(),
                application.name(),
                application.description(),
                application.status().name(),
                application.createdBy(),
                application.createdAt(),
                application.updatedAt()
        );

        ApplicationEntity saved = applicationJpa.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Application> findById(ApplicationId id) {
        return applicationJpa.findById(id.value()).map(this::toDomain);
    }

    @Override
    public Optional<Application> findByCode(ApplicationCode code) {
        return applicationJpa.findByCode(code.value()).map(this::toDomain);
    }

    @Override
    public boolean existsByCode(ApplicationCode code) {
        return applicationJpa.existsByCode(code.value());
    }

    private Application toDomain(ApplicationEntity entity) {
        return Application.reconstitute(
                new ApplicationId(entity.getId()),
                new ApplicationCode(entity.getCode()),
                entity.getName(),
                entity.getDescription(),
                ApplicationStatus.valueOf(entity.getStatus()),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
