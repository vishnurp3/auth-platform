package com.vishnu.authplatform.appregistry.application.port;

import com.vishnu.authplatform.appregistry.domain.Application;
import com.vishnu.authplatform.appregistry.domain.ApplicationCode;
import com.vishnu.authplatform.appregistry.domain.ApplicationId;

import java.util.Optional;

public interface ApplicationRepository {
    Application save(Application application);

    Optional<Application> findById(ApplicationId id);

    Optional<Application> findByCode(ApplicationCode code);

    boolean existsByCode(ApplicationCode code);
}
