package com.vishnu.authplatform.appregistry.application;

import com.vishnu.authplatform.appregistry.application.command.CreateRoleCommand;
import com.vishnu.authplatform.appregistry.application.exception.ApplicationNotFoundException;
import com.vishnu.authplatform.appregistry.application.port.ApplicationRepository;
import com.vishnu.authplatform.appregistry.application.port.RoleRepository;
import com.vishnu.authplatform.appregistry.application.result.RoleResult;
import com.vishnu.authplatform.appregistry.domain.Application;
import com.vishnu.authplatform.appregistry.domain.ApplicationCode;
import com.vishnu.authplatform.appregistry.domain.Role;
import com.vishnu.authplatform.appregistry.domain.RoleCode;
import com.vishnu.authplatform.appregistry.domain.RoleId;
import com.vishnu.authplatform.appregistry.domain.RoleStatus;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.Instant;

@RequiredArgsConstructor
public final class CreateRoleUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateRoleUseCase.class);

    private final ApplicationRepository applicationRepository;
    private final RoleRepository roleRepository;
    private final Clock clock;

    public RoleResult execute(CreateRoleCommand cmd, String adminIdentifier) {
        ApplicationCode applicationCode = new ApplicationCode(cmd.applicationCode());
        RoleCode roleCode = new RoleCode(cmd.roleCode());

        Application application = applicationRepository.findByCode(applicationCode)
                .orElseThrow(() -> new ApplicationNotFoundException(
                        "application with code '" + applicationCode.value() + "' not found"));

        if (roleRepository.existsByApplicationIdAndCode(application.id(), roleCode)) {
            throw new IllegalStateException(
                    "role with code '" + roleCode.value() + "' already exists in application '" + applicationCode.value() + "'");
        }

        RoleStatus status = cmd.status() != null ? cmd.status() : RoleStatus.ACTIVE;

        Instant now = Instant.now(clock);
        Role role = Role.create(
                RoleId.newId(),
                application.id(),
                roleCode,
                cmd.displayName(),
                cmd.description(),
                status,
                adminIdentifier,
                now
        );

        role = roleRepository.save(role);

        log.info("Role created: id={}, applicationCode={}, roleCode={}, displayName={}, status={}, createdBy={}",
                role.id().value(),
                applicationCode.value(),
                role.code().value(),
                role.displayName(),
                role.status(),
                role.createdBy());

        return new RoleResult(
                role.id().value(),
                application.id().value(),
                applicationCode.value(),
                role.code().value(),
                role.displayName(),
                role.description(),
                role.status().name(),
                role.createdAt(),
                role.updatedAt()
        );
    }
}
