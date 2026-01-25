package com.vishnu.authplatform.appregistry.application.port;

import com.vishnu.authplatform.appregistry.domain.ApplicationId;
import com.vishnu.authplatform.appregistry.domain.Membership;
import com.vishnu.authplatform.appregistry.domain.MembershipId;
import com.vishnu.authplatform.identity.domain.UserId;

import java.util.Optional;

public interface MembershipRepository {
    Membership save(Membership membership);

    Optional<Membership> findById(MembershipId id);

    Optional<Membership> findByUserIdAndApplicationId(UserId userId, ApplicationId applicationId);

    boolean existsByUserIdAndApplicationId(UserId userId, ApplicationId applicationId);
}
