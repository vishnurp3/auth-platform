package com.vishnu.authplatform.application.application;

import com.vishnu.authplatform.application.application.port.ApplicationRepository;
import com.vishnu.authplatform.application.domain.Application;
import com.vishnu.authplatform.application.domain.ApplicationCode;
import com.vishnu.authplatform.application.domain.ApplicationId;
import com.vishnu.authplatform.application.domain.ApplicationStatus;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
public final class CreateApplicationUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateApplicationUseCase.class);

    private final ApplicationRepository applicationRepository;
    private final Clock clock;

    public Result execute(Command cmd, String adminIdentifier) {
        ApplicationCode code = ApplicationCode.of(cmd.applicationCode());

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

        return new Result(
                application.id().value(),
                application.code().value(),
                application.name(),
                application.description(),
                application.status().name(),
                application.createdAt(),
                application.updatedAt()
        );
    }

    public record Command(
            String applicationCode,
            String name,
            String description,
            ApplicationStatus status
    ) {
    }

    public record Result(
            UUID applicationId,
            String applicationCode,
            String name,
            String description,
            String status,
            Instant createdAt,
            Instant updatedAt
    ) {
    }
}
