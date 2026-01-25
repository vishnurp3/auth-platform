package com.vishnu.authplatform.appregistry.adapter.persistence;

import com.vishnu.authplatform.appregistry.application.port.MembershipRepository;
import com.vishnu.authplatform.appregistry.domain.ApplicationId;
import com.vishnu.authplatform.appregistry.domain.Membership;
import com.vishnu.authplatform.appregistry.domain.MembershipId;
import com.vishnu.authplatform.appregistry.domain.MembershipStatus;
import com.vishnu.authplatform.appregistry.domain.RoleId;
import com.vishnu.authplatform.identity.domain.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@Transactional
@RequiredArgsConstructor
public class JpaMembershipRepositoryAdapter implements MembershipRepository {

    private final SpringDataMembershipJpaRepository membershipJpa;

    @Override
    public Membership save(Membership membership) {
        Set<UUID> roleIdValues = membership.roleIds().stream()
                .map(RoleId::value)
                .collect(Collectors.toSet());

        MembershipEntity entity = new MembershipEntity(
                membership.id().value(),
                membership.userId().value(),
                membership.applicationId().value(),
                roleIdValues,
                membership.status().name(),
                membership.createdBy(),
                membership.createdAt(),
                membership.updatedAt()
        );

        MembershipEntity saved = membershipJpa.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Membership> findById(MembershipId id) {
        return membershipJpa.findById(id.value()).map(this::toDomain);
    }

    @Override
    public Optional<Membership> findByUserIdAndApplicationId(UserId userId, ApplicationId applicationId) {
        return membershipJpa.findByUserIdAndApplicationId(userId.value(), applicationId.value())
                .map(this::toDomain);
    }

    @Override
    public boolean existsByUserIdAndApplicationId(UserId userId, ApplicationId applicationId) {
        return membershipJpa.existsByUserIdAndApplicationId(userId.value(), applicationId.value());
    }

    private Membership toDomain(MembershipEntity entity) {
        Set<RoleId> roleIds = entity.getRoleIds().stream()
                .map(RoleId::new)
                .collect(Collectors.toSet());

        return Membership.reconstitute(
                new MembershipId(entity.getId()),
                new UserId(entity.getUserId()),
                new ApplicationId(entity.getApplicationId()),
                roleIds,
                MembershipStatus.valueOf(entity.getStatus()),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
