package com.vishnu.authplatform.application.application;

import com.vishnu.authplatform.application.application.port.ApplicationRepository;
import com.vishnu.authplatform.application.domain.Application;
import com.vishnu.authplatform.application.domain.ApplicationCode;
import com.vishnu.authplatform.application.domain.ApplicationStatus;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
public final class UpdateApplicationStatusUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateApplicationStatusUseCase.class);

    private final ApplicationRepository applicationRepository;
    private final Clock clock;

    public Result execute(Command cmd) {
        ApplicationCode code = ApplicationCode.of(cmd.applicationCode());

        Application application = applicationRepository.findByCode(code)
                .orElseThrow(() -> new ApplicationNotFoundException(
                        "application with code '" + code.value() + "' not found"));

        ApplicationStatus newStatus = cmd.newStatus();
        if (application.status() == newStatus) {
            log.debug("Application {} already has status {}, no change required",
                    code.value(), newStatus);
            return toResult(application);
        }

        Instant now = Instant.now(clock);
        Application updated = switch (newStatus) {
            case ACTIVE -> application.enable(now);
            case DISABLED -> application.disable(now);
        };

        updated = applicationRepository.save(updated);

        log.info("Application status updated: id={}, code={}, oldStatus={}, newStatus={}",
                updated.id().value(),
                updated.code().value(),
                application.status(),
                updated.status());

        return toResult(updated);
    }

    private Result toResult(Application application) {
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
            ApplicationStatus newStatus
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

    public static class ApplicationNotFoundException extends RuntimeException {
        public ApplicationNotFoundException(String message) {
            super(message);
        }
    }
}
