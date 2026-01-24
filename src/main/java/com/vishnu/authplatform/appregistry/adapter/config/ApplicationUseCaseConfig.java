package com.vishnu.authplatform.appregistry.adapter.config;

import com.vishnu.authplatform.appregistry.application.CreateApplicationUseCase;
import com.vishnu.authplatform.appregistry.application.UpdateApplicationStatusUseCase;
import com.vishnu.authplatform.appregistry.application.port.ApplicationRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class ApplicationUseCaseConfig {

    @Bean
    public CreateApplicationUseCase createApplicationUseCase(
            ApplicationRepository applicationRepository,
            Clock clock
    ) {
        return new CreateApplicationUseCase(applicationRepository, clock);
    }

    @Bean
    public UpdateApplicationStatusUseCase updateApplicationStatusUseCase(
            ApplicationRepository applicationRepository,
            Clock clock
    ) {
        return new UpdateApplicationStatusUseCase(applicationRepository, clock);
    }
}
