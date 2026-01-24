package com.vishnu.authplatform.appregistry.application;

import com.vishnu.authplatform.appregistry.application.command.UpdateApplicationStatusCommand;
import com.vishnu.authplatform.appregistry.application.exception.ApplicationNotFoundException;
import com.vishnu.authplatform.appregistry.application.port.ApplicationRepository;
import com.vishnu.authplatform.appregistry.application.result.ApplicationResult;
import com.vishnu.authplatform.appregistry.domain.Application;
import com.vishnu.authplatform.appregistry.domain.ApplicationCode;
import com.vishnu.authplatform.appregistry.domain.ApplicationStatus;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.Instant;

@RequiredArgsConstructor
public final class UpdateApplicationStatusUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateApplicationStatusUseCase.class);

    private final ApplicationRepository applicationRepository;
    private final Clock clock;

    public ApplicationResult execute(UpdateApplicationStatusCommand cmd) {
        ApplicationCode code = new ApplicationCode(cmd.applicationCode());

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

    private ApplicationResult toResult(Application application) {
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
