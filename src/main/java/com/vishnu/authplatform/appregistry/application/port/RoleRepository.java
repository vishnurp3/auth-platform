package com.vishnu.authplatform.appregistry.application.port;

import com.vishnu.authplatform.appregistry.domain.ApplicationId;
import com.vishnu.authplatform.appregistry.domain.Role;
import com.vishnu.authplatform.appregistry.domain.RoleCode;
import com.vishnu.authplatform.appregistry.domain.RoleId;

import java.util.Optional;

public interface RoleRepository {
    Role save(Role role);

    Optional<Role> findById(RoleId id);

    Optional<Role> findByApplicationIdAndCode(ApplicationId applicationId, RoleCode code);

    boolean existsByApplicationIdAndCode(ApplicationId applicationId, RoleCode code);
}
