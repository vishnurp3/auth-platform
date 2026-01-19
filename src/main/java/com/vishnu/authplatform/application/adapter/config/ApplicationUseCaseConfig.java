package com.vishnu.authplatform.application.adapter.config;

import com.vishnu.authplatform.application.application.CreateApplicationUseCase;
import com.vishnu.authplatform.application.application.port.ApplicationRepository;
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
}
