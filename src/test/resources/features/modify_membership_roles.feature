Feature: Modify User Roles for Application (Update Membership Roles)

  Background:
    Given the system is running
    And I am authenticated as admin

  Scenario: Admin replaces all roles using membership ID
    Given a user exists with id stored as "replaceRolesUserId"
    And an application with code "MOD_REPLACE_ROLES_APP" exists
    And a role with code "ADMIN" exists in application "MOD_REPLACE_ROLES_APP"
    And a role with code "USER" exists in application "MOD_REPLACE_ROLES_APP"
    And a role with code "EDITOR" exists in application "MOD_REPLACE_ROLES_APP"
    And a membership exists for user "replaceRolesUserId" in application "MOD_REPLACE_ROLES_APP" with roles "ADMIN,USER" stored as "membershipId"
    When I modify membership "membershipId" roles to replace with "EDITOR"
    Then the response status should be 200
    And the membership should have 1 assigned roles
    And the membership should include role "EDITOR"

  Scenario: Admin replaces all roles using user ID
    Given a user exists with id stored as "replaceByUserIdUserId"
    And an application with code "MOD_REPLACE_BY_USER_APP" exists
    And a role with code "VIEWER" exists in application "MOD_REPLACE_BY_USER_APP"
    And a role with code "EDITOR" exists in application "MOD_REPLACE_BY_USER_APP"
    And a membership exists for user "replaceByUserIdUserId" in application "MOD_REPLACE_BY_USER_APP" with roles "VIEWER"
    When I modify membership for user "replaceByUserIdUserId" in application "MOD_REPLACE_BY_USER_APP" to replace with roles "EDITOR,VIEWER"
    Then the response status should be 200
    And the membership should have 2 assigned roles
    And the membership should include role "EDITOR"
    And the membership should include role "VIEWER"

  Scenario: Admin clears all roles by replacing with empty list
    Given a user exists with id stored as "clearRolesUserId"
    And an application with code "MOD_CLEAR_ROLES_APP" exists
    And a role with code "ADMIN" exists in application "MOD_CLEAR_ROLES_APP"
    And a membership exists for user "clearRolesUserId" in application "MOD_CLEAR_ROLES_APP" with roles "ADMIN" stored as "clearMembershipId"
    When I modify membership "clearMembershipId" roles to replace with empty roles
    Then the response status should be 200
    And the membership should have 0 assigned roles

  Scenario: Admin adds roles using membership ID
    Given a user exists with id stored as "addRolesUserId"
    And an application with code "MOD_ADD_ROLES_APP" exists
    And a role with code "USER" exists in application "MOD_ADD_ROLES_APP"
    And a role with code "EDITOR" exists in application "MOD_ADD_ROLES_APP"
    And a membership exists for user "addRolesUserId" in application "MOD_ADD_ROLES_APP" with roles "USER" stored as "addMembershipId"
    When I modify membership "addMembershipId" to add roles "EDITOR"
    Then the response status should be 200
    And the membership should have 2 assigned roles
    And the membership should include role "USER"
    And the membership should include role "EDITOR"

  Scenario: Admin adds roles using user ID
    Given a user exists with id stored as "addByUserIdUserId"
    And an application with code "MOD_ADD_BY_USER_APP" exists
    And a role with code "VIEWER" exists in application "MOD_ADD_BY_USER_APP"
    And a role with code "ADMIN" exists in application "MOD_ADD_BY_USER_APP"
    And a membership exists for user "addByUserIdUserId" in application "MOD_ADD_BY_USER_APP" with roles "VIEWER"
    When I modify membership for user "addByUserIdUserId" in application "MOD_ADD_BY_USER_APP" to add roles "ADMIN"
    Then the response status should be 200
    And the membership should have 2 assigned roles
    And the membership should include role "VIEWER"
    And the membership should include role "ADMIN"

  Scenario: Admin removes roles using membership ID
    Given a user exists with id stored as "removeRolesUserId"
    And an application with code "MOD_REMOVE_ROLES_APP" exists
    And a role with code "ADMIN" exists in application "MOD_REMOVE_ROLES_APP"
    And a role with code "USER" exists in application "MOD_REMOVE_ROLES_APP"
    And a membership exists for user "removeRolesUserId" in application "MOD_REMOVE_ROLES_APP" with roles "ADMIN,USER" stored as "removeMembershipId"
    When I modify membership "removeMembershipId" to remove roles "ADMIN"
    Then the response status should be 200
    And the membership should have 1 assigned roles
    And the membership should include role "USER"

  Scenario: Admin removes roles using user ID
    Given a user exists with id stored as "removeByUserIdUserId"
    And an application with code "MOD_REMOVE_BY_USER_APP" exists
    And a role with code "EDITOR" exists in application "MOD_REMOVE_BY_USER_APP"
    And a role with code "VIEWER" exists in application "MOD_REMOVE_BY_USER_APP"
    And a membership exists for user "removeByUserIdUserId" in application "MOD_REMOVE_BY_USER_APP" with roles "EDITOR,VIEWER"
    When I modify membership for user "removeByUserIdUserId" in application "MOD_REMOVE_BY_USER_APP" to remove roles "VIEWER"
    Then the response status should be 200
    And the membership should have 1 assigned roles
    And the membership should include role "EDITOR"

  Scenario: Admin adds and removes roles atomically
    Given a user exists with id stored as "atomicUserId"
    And an application with code "MOD_ATOMIC_ROLES_APP" exists
    And a role with code "ADMIN" exists in application "MOD_ATOMIC_ROLES_APP"
    And a role with code "USER" exists in application "MOD_ATOMIC_ROLES_APP"
    And a role with code "EDITOR" exists in application "MOD_ATOMIC_ROLES_APP"
    And a membership exists for user "atomicUserId" in application "MOD_ATOMIC_ROLES_APP" with roles "ADMIN,USER" stored as "atomicMembershipId"
    When I modify membership "atomicMembershipId" to add roles "EDITOR" and remove roles "ADMIN"
    Then the response status should be 200
    And the membership should have 2 assigned roles
    And the membership should include role "USER"
    And the membership should include role "EDITOR"

  Scenario: Modifying non-existent membership is rejected
    Given an application with code "MOD_NO_MEMBERSHIP_APP" exists
    When I modify non-existent membership roles in application "MOD_NO_MEMBERSHIP_APP"
    Then the response status should be 404

  Scenario: Modifying membership with non-existent role is rejected
    Given a user exists with id stored as "badRoleModifyUserId"
    And an application with code "MOD_BAD_ROLE_APP" exists
    And a role with code "USER" exists in application "MOD_BAD_ROLE_APP"
    And a membership exists for user "badRoleModifyUserId" in application "MOD_BAD_ROLE_APP" with roles "USER" stored as "badRoleMembershipId"
    When I modify membership "badRoleMembershipId" roles to replace with "NON_EXISTENT_ROLE"
    Then the response status should be 404

  Scenario: Adding inactive role is rejected
    Given a user exists with id stored as "inactiveAddRoleUserId"
    And an application with code "MOD_INACTIVE_ROLE_APP" exists
    And a role with code "ACTIVE_ROLE" exists in application "MOD_INACTIVE_ROLE_APP"
    And a role with code "DISABLED_ROLE" and status "DISABLED" exists in application "MOD_INACTIVE_ROLE_APP"
    And a membership exists for user "inactiveAddRoleUserId" in application "MOD_INACTIVE_ROLE_APP" with roles "ACTIVE_ROLE" stored as "inactiveRoleMembershipId"
    When I modify membership "inactiveRoleMembershipId" to add roles "DISABLED_ROLE"
    Then the response status should be 409

  Scenario: Using both replace and patch mode is rejected
    Given a user exists with id stored as "bothModesUserId"
    And an application with code "MOD_BOTH_MODES_APP" exists
    And a role with code "USER" exists in application "MOD_BOTH_MODES_APP"
    And a membership exists for user "bothModesUserId" in application "MOD_BOTH_MODES_APP" with roles "USER" stored as "bothModesMembershipId"
    When I modify membership "bothModesMembershipId" with both replace and patch mode
    Then the response status should be 400

  Scenario: Empty request (no replace or patch) is rejected
    Given a user exists with id stored as "emptyRequestUserId"
    And an application with code "MOD_EMPTY_REQUEST_APP" exists
    And a role with code "USER" exists in application "MOD_EMPTY_REQUEST_APP"
    And a membership exists for user "emptyRequestUserId" in application "MOD_EMPTY_REQUEST_APP" with roles "USER" stored as "emptyRequestMembershipId"
    When I modify membership "emptyRequestMembershipId" with empty request
    Then the response status should be 400

  Scenario: Unauthenticated request is rejected
    Given a user exists with id stored as "noAuthModifyUserId" with system credentials
    And an application with code "MOD_NO_AUTH_APP" exists with system credentials
    And a membership exists for user "noAuthModifyUserId" in application "MOD_NO_AUTH_APP" with no roles stored as "noAuthMembershipId" with system credentials
    When I modify membership "noAuthMembershipId" roles without authentication
    Then the response status should be 401

  Scenario: Invalid API key is rejected
    Given a user exists with id stored as "badKeyModifyUserId" with system credentials
    And an application with code "MOD_BAD_KEY_APP" exists with system credentials
    And a membership exists for user "badKeyModifyUserId" in application "MOD_BAD_KEY_APP" with no roles stored as "badKeyMembershipId" with system credentials
    When I modify membership "badKeyMembershipId" roles with invalid API key
    Then the response status should be 401

  Scenario: Roles can be modified on inactive membership (for admin staging)
    Given a user exists with id stored as "inactiveMembershipUserId"
    And an application with code "MOD_INACTIVE_MEMBER_APP" exists
    And a role with code "ADMIN" exists in application "MOD_INACTIVE_MEMBER_APP"
    And a role with code "USER" exists in application "MOD_INACTIVE_MEMBER_APP"
    And an inactive membership exists for user "inactiveMembershipUserId" in application "MOD_INACTIVE_MEMBER_APP" with roles "USER" stored as "inactiveMembershipId"
    When I modify membership "inactiveMembershipId" to add roles "ADMIN"
    Then the response status should be 200
    And the membership status should be "INACTIVE"
    And the membership should have 2 assigned roles
