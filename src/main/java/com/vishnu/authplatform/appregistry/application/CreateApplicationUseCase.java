package com.vishnu.authplatform.appregistry.application;

import com.vishnu.authplatform.appregistry.application.command.CreateApplicationCommand;
import com.vishnu.authplatform.appregistry.application.port.ApplicationRepository;
import com.vishnu.authplatform.appregistry.application.result.ApplicationResult;
import com.vishnu.authplatform.appregistry.domain.Application;
import com.vishnu.authplatform.appregistry.domain.ApplicationCode;
import com.vishnu.authplatform.appregistry.domain.ApplicationId;
import com.vishnu.authplatform.appregistry.domain.ApplicationStatus;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.Instant;

@RequiredArgsConstructor
public final class CreateApplicationUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateApplicationUseCase.class);

    private final ApplicationRepository applicationRepository;
    private final Clock clock;

    public ApplicationResult execute(CreateApplicationCommand cmd, String adminIdentifier) {
        ApplicationCode code = new ApplicationCode(cmd.applicationCode());

        if (applicationRepository.existsByCode(code)) {
            throw new IllegalStateException("application with code '" + code.value() + "' already exists");
        }

        ApplicationStatus status = cmd.status() != null ? cmd.status() : ApplicationStatus.ACTIVE;

        Instant now = Instant.now(clock);
        Application application = Application.create(
                ApplicationId.newId(),
                code,
                cmd.name(),
                cmd.description(),
                status,
                adminIdentifier,
                now
        );

        application = applicationRepository.save(application);

        log.info("Application created: id={}, code={}, name={}, status={}, createdBy={}",
                application.id().value(),
                application.code().value(),
                application.name(),
                application.status(),
                application.createdBy());

        return new ApplicationResult(
                application.id().value(),
                application.code().value(),
                application.name(),
                application.description(),
                application.status().name(),
                application.createdAt(),
                application.updatedAt()
        );
    }
}
