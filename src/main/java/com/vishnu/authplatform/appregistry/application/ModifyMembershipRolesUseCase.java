package com.vishnu.authplatform.appregistry.application;

import com.vishnu.authplatform.appregistry.application.command.ModifyMembershipRolesCommand;
import com.vishnu.authplatform.appregistry.application.exception.ApplicationNotFoundException;
import com.vishnu.authplatform.appregistry.application.exception.MembershipNotFoundException;
import com.vishnu.authplatform.appregistry.application.exception.RoleNotFoundException;
import com.vishnu.authplatform.appregistry.application.port.ApplicationRepository;
import com.vishnu.authplatform.appregistry.application.port.MembershipRepository;
import com.vishnu.authplatform.appregistry.application.port.RoleRepository;
import com.vishnu.authplatform.appregistry.application.result.MembershipResult;
import com.vishnu.authplatform.appregistry.domain.Application;
import com.vishnu.authplatform.appregistry.domain.ApplicationCode;
import com.vishnu.authplatform.appregistry.domain.ApplicationId;
import com.vishnu.authplatform.appregistry.domain.Membership;
import com.vishnu.authplatform.appregistry.domain.MembershipId;
import com.vishnu.authplatform.appregistry.domain.Role;
import com.vishnu.authplatform.appregistry.domain.RoleCode;
import com.vishnu.authplatform.appregistry.domain.RoleId;
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
public final class ModifyMembershipRolesUseCase {

    private static final Logger log = LoggerFactory.getLogger(ModifyMembershipRolesUseCase.class);

    private final ApplicationRepository applicationRepository;
    private final RoleRepository roleRepository;
    private final MembershipRepository membershipRepository;
    private final Clock clock;

    public MembershipResult execute(ModifyMembershipRolesCommand cmd, String adminIdentifier) {
        Membership membership = findMembership(cmd);
        Application application = findApplication(membership.applicationId());

        Set<RoleId> newRoleIds = computeNewRoleIds(cmd, membership, application);

        Instant now = Instant.now(clock);
        Membership updatedMembership = membership.updateRoles(newRoleIds, now);
        updatedMembership = membershipRepository.save(updatedMembership);

        log.info("Membership roles modified: membershipId={}, applicationId={}, previousRolesCount={}, newRolesCount={}, modifiedBy={}",
                updatedMembership.id().value(),
                application.id().value(),
                membership.roleIds().size(),
                newRoleIds.size(),
                adminIdentifier);

        return buildResult(updatedMembership, application);
    }

    private Membership findMembership(ModifyMembershipRolesCommand cmd) {
        if (cmd.membershipId() != null) {
            MembershipId membershipId = new MembershipId(cmd.membershipId());
            return membershipRepository.findById(membershipId)
                    .orElseThrow(() -> new MembershipNotFoundException(
                            "membership with id '" + cmd.membershipId() + "' not found"));
        }

        if (cmd.userId() != null && cmd.applicationCode() != null) {
            UserId userId = new UserId(cmd.userId());
            ApplicationCode applicationCode = new ApplicationCode(cmd.applicationCode());

            Application application = applicationRepository.findByCode(applicationCode)
                    .orElseThrow(() -> new ApplicationNotFoundException(
                            "application with code '" + applicationCode.value() + "' not found"));

            return membershipRepository.findByUserIdAndApplicationId(userId, application.id())
                    .orElseThrow(() -> new MembershipNotFoundException(
                            "membership not found for user '" + cmd.userId() + "' in application '" + cmd.applicationCode() + "'"));
        }

        throw new IllegalArgumentException("either membershipId or (userId and applicationCode) must be provided");
    }

    private Application findApplication(ApplicationId applicationId) {
        return applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException(
                        "application with id '" + applicationId.value() + "' not found"));
    }

    private Set<RoleId> computeNewRoleIds(ModifyMembershipRolesCommand cmd, Membership membership, Application application) {
        if (cmd.isReplaceMode()) {
            return resolveRoleCodes(cmd.roleCodes(), application);
        }

        if (cmd.isPatchMode()) {
            Set<RoleId> currentRoles = new HashSet<>(membership.roleIds());

            if (cmd.removeRoleCodes() != null && !cmd.removeRoleCodes().isEmpty()) {
                Set<RoleId> rolesToRemove = resolveRoleCodesForRemoval(cmd.removeRoleCodes(), application);
                currentRoles.removeAll(rolesToRemove);
            }

            if (cmd.addRoleCodes() != null && !cmd.addRoleCodes().isEmpty()) {
                Set<RoleId> rolesToAdd = resolveRoleCodes(cmd.addRoleCodes(), application);
                currentRoles.addAll(rolesToAdd);
            }

            return currentRoles;
        }

        throw new IllegalArgumentException("either roleCodes (replace) or addRoleCodes/removeRoleCodes (patch) must be provided");
    }

    private Set<RoleId> resolveRoleCodes(List<String> roleCodes, Application application) {
        Set<RoleId> roleIds = new HashSet<>();

        if (roleCodes == null || roleCodes.isEmpty()) {
            return roleIds;
        }

        for (String roleCodeStr : roleCodes) {
            RoleCode roleCode = new RoleCode(roleCodeStr);
            Role role = roleRepository.findByApplicationIdAndCode(application.id(), roleCode)
                    .orElseThrow(() -> new RoleNotFoundException(
                            "role with code '" + roleCode.value() + "' not found in application '" + application.code().value() + "'"));

            if (!role.isAssignable()) {
                throw new IllegalStateException(
                        "role '" + roleCode.value() + "' is not assignable (status: " + role.status() + ")");
            }

            roleIds.add(role.id());
        }

        return roleIds;
    }

    private Set<RoleId> resolveRoleCodesForRemoval(List<String> roleCodes, Application application) {
        Set<RoleId> roleIds = new HashSet<>();

        for (String roleCodeStr : roleCodes) {
            RoleCode roleCode = new RoleCode(roleCodeStr);
            Role role = roleRepository.findByApplicationIdAndCode(application.id(), roleCode)
                    .orElseThrow(() -> new RoleNotFoundException(
                            "role with code '" + roleCode.value() + "' not found in application '" + application.code().value() + "'"));
            roleIds.add(role.id());
        }

        return roleIds;
    }

    private MembershipResult buildResult(Membership membership, Application application) {
        List<MembershipResult.AssignedRole> assignedRoles = new ArrayList<>();

        for (RoleId roleId : membership.roleIds()) {
            roleRepository.findById(roleId).ifPresent(role ->
                    assignedRoles.add(new MembershipResult.AssignedRole(
                            role.id().value(),
                            role.code().value(),
                            role.displayName())));
        }

        return new MembershipResult(
                membership.id().value(),
                membership.userId().value(),
                membership.applicationId().value(),
                application.code().value(),
                assignedRoles,
                membership.status().name(),
                membership.createdAt(),
                membership.updatedAt()
        );
    }
}
