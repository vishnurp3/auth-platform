package com.vishnu.authplatform.appregistry.adapter.config;

import com.vishnu.authplatform.appregistry.application.CreateApplicationUseCase;
import com.vishnu.authplatform.appregistry.application.CreateMembershipUseCase;
import com.vishnu.authplatform.appregistry.application.CreateRoleUseCase;
import com.vishnu.authplatform.appregistry.application.ModifyMembershipRolesUseCase;
import com.vishnu.authplatform.appregistry.application.UpdateApplicationStatusUseCase;
import com.vishnu.authplatform.appregistry.application.port.ApplicationRepository;
import com.vishnu.authplatform.appregistry.application.port.MembershipRepository;
import com.vishnu.authplatform.appregistry.application.port.RoleRepository;
import com.vishnu.authplatform.identity.application.port.UserRepository;
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

    @Bean
    public CreateRoleUseCase createRoleUseCase(
            ApplicationRepository applicationRepository,
            RoleRepository roleRepository,
            Clock clock
    ) {
        return new CreateRoleUseCase(applicationRepository, roleRepository, clock);
    }

    @Bean
    public CreateMembershipUseCase createMembershipUseCase(
            UserRepository userRepository,
            ApplicationRepository applicationRepository,
            RoleRepository roleRepository,
            MembershipRepository membershipRepository,
            Clock clock
    ) {
        return new CreateMembershipUseCase(userRepository, applicationRepository, roleRepository, membershipRepository, clock);
    }

    @Bean
    public ModifyMembershipRolesUseCase modifyMembershipRolesUseCase(
            ApplicationRepository applicationRepository,
            RoleRepository roleRepository,
            MembershipRepository membershipRepository,
            Clock clock
    ) {
        return new ModifyMembershipRolesUseCase(applicationRepository, roleRepository, membershipRepository, clock);
    }
}
