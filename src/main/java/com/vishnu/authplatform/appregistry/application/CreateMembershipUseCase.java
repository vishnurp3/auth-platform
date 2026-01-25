package com.vishnu.authplatform.appregistry.application;

import com.vishnu.authplatform.appregistry.application.command.CreateMembershipCommand;
import com.vishnu.authplatform.appregistry.application.exception.ApplicationNotFoundException;
import com.vishnu.authplatform.appregistry.application.exception.MembershipAlreadyExistsException;
import com.vishnu.authplatform.appregistry.application.exception.RoleNotFoundException;
import com.vishnu.authplatform.appregistry.application.exception.UserNotFoundException;
import com.vishnu.authplatform.appregistry.application.port.ApplicationRepository;
import com.vishnu.authplatform.appregistry.application.port.MembershipRepository;
import com.vishnu.authplatform.appregistry.application.port.RoleRepository;
import com.vishnu.authplatform.appregistry.application.result.MembershipResult;
import com.vishnu.authplatform.appregistry.domain.Application;
import com.vishnu.authplatform.appregistry.domain.ApplicationCode;
import com.vishnu.authplatform.appregistry.domain.Membership;
import com.vishnu.authplatform.appregistry.domain.MembershipId;
import com.vishnu.authplatform.appregistry.domain.MembershipStatus;
import com.vishnu.authplatform.appregistry.domain.Role;
import com.vishnu.authplatform.appregistry.domain.RoleCode;
import com.vishnu.authplatform.appregistry.domain.RoleId;
import com.vishnu.authplatform.identity.application.port.UserRepository;
import com.vishnu.authplatform.identity.domain.User;
import com.vishnu.authplatform.identity.domain.UserId;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public final class CreateMembershipUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateMembershipUseCase.class);

    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final RoleRepository roleRepository;
    private final MembershipRepository membershipRepository;
    private final Clock clock;

    public MembershipResult execute(CreateMembershipCommand cmd, String adminIdentifier) {
        UserId userId = new UserId(cmd.userId());
        ApplicationCode applicationCode = new ApplicationCode(cmd.applicationCode());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        "user with id '" + userId.value() + "' not found"));

        Application application = applicationRepository.findByCode(applicationCode)
                .orElseThrow(() -> new ApplicationNotFoundException(
                        "application with code '" + applicationCode.value() + "' not found"));

        if (!application.isActive()) {
            throw new IllegalStateException(
                    "application '" + applicationCode.value() + "' is not active");
        }

        if (membershipRepository.existsByUserIdAndApplicationId(userId, application.id())) {
            throw new MembershipAlreadyExistsException(
                    "membership already exists for user '" + userId.value() + "' in application '" + applicationCode.value() + "'");
        }

        Set<RoleId> roleIds = new HashSet<>();
        List<Role> assignedRoles = new ArrayList<>();

        if (cmd.roleCodes() != null && !cmd.roleCodes().isEmpty()) {
            for (String roleCodeStr : cmd.roleCodes()) {
                RoleCode roleCode = new RoleCode(roleCodeStr);
                Role role = roleRepository.findByApplicationIdAndCode(application.id(), roleCode)
                        .orElseThrow(() -> new RoleNotFoundException(
                                "role with code '" + roleCode.value() + "' not found in application '" + applicationCode.value() + "'"));

                if (!role.isAssignable()) {
                    throw new IllegalStateException(
                            "role '" + roleCode.value() + "' is not assignable (status: " + role.status() + ")");
                }

                roleIds.add(role.id());
                assignedRoles.add(role);
            }
        }

        MembershipStatus status = cmd.status() != null ? cmd.status() : MembershipStatus.ACTIVE;

        Instant now = Instant.now(clock);
        Membership membership = Membership.create(
                MembershipId.newId(),
                userId,
                application.id(),
                roleIds,
                status,
                adminIdentifier,
                now
        );

        membership = membershipRepository.save(membership);

        log.info("Membership created: id={}, userId={}, applicationCode={}, rolesCount={}, status={}, createdBy={}",
                membership.id().value(),
                userId.value(),
                applicationCode.value(),
                roleIds.size(),
                membership.status(),
                membership.createdBy());

        List<MembershipResult.AssignedRole> assignedRoleResults = assignedRoles.stream()
                .map(role -> new MembershipResult.AssignedRole(
                        role.id().value(),
                        role.code().value(),
                        role.displayName()))
                .toList();

        return new MembershipResult(
                membership.id().value(),
                userId.value(),
                application.id().value(),
                applicationCode.value(),
                assignedRoleResults,
                membership.status().name(),
                membership.createdAt(),
                membership.updatedAt()
        );
    }
}
