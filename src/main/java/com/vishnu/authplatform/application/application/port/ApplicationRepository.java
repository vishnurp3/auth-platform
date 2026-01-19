package com.vishnu.authplatform.application.application.port;

import com.vishnu.authplatform.application.domain.Application;
import com.vishnu.authplatform.application.domain.ApplicationCode;
import com.vishnu.authplatform.application.domain.ApplicationId;

import java.util.Optional;

public interface ApplicationRepository {
    Application save(Application application);

    Optional<Application> findById(ApplicationId id);

    Optional<Application> findByCode(ApplicationCode code);

    boolean existsByCode(ApplicationCode code);
}
