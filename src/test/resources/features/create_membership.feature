Feature: Create Membership (Assign Roles to User for Application)

  Background:
    Given the system is running
    And I am authenticated as admin

  Scenario: Admin creates a membership with roles
    Given a user exists with id stored as "testUserId"
    And an application with code "MEMBERSHIP_APP" exists
    And a role with code "ADMIN" exists in application "MEMBERSHIP_APP"
    And a role with code "USER" exists in application "MEMBERSHIP_APP"
    When I create a membership for user "testUserId" in application "MEMBERSHIP_APP" with roles "ADMIN,USER"
    Then the response status should be 201
    And the membership should have a valid id
    And the membership user id should match "testUserId"
    And the membership application code should be "MEMBERSHIP_APP"
    And the membership status should be "ACTIVE"
    And the membership should have 2 assigned roles
    And the membership should have timestamps

  Scenario: Admin creates a membership without roles (empty roles)
    Given a user exists with id stored as "noRoleUserId"
    And an application with code "NO_ROLE_APP" exists
    When I create a membership for user "noRoleUserId" in application "NO_ROLE_APP" with no roles
    Then the response status should be 201
    And the membership status should be "ACTIVE"
    And the membership should have 0 assigned roles

  Scenario: Admin creates a membership with INACTIVE status
    Given a user exists with id stored as "inactiveUserId"
    And an application with code "INACTIVE_MEMBERSHIP_APP" exists
    When I create a membership for user "inactiveUserId" in application "INACTIVE_MEMBERSHIP_APP" with status "INACTIVE"
    Then the response status should be 201
    And the membership status should be "INACTIVE"

  Scenario: Duplicate membership is rejected
    Given a user exists with id stored as "dupUserId"
    And an application with code "DUP_MEMBERSHIP_APP" exists
    When I create a membership for user "dupUserId" in application "DUP_MEMBERSHIP_APP" with no roles
    Then the response status should be 201
    When I create a membership for user "dupUserId" in application "DUP_MEMBERSHIP_APP" with no roles
    Then the response status should be 409

  Scenario: Same user can have memberships in different applications
    Given a user exists with id stored as "multiAppUserId"
    And an application with code "MULTI_APP_ONE" exists
    And an application with code "MULTI_APP_TWO" exists
    When I create a membership for user "multiAppUserId" in application "MULTI_APP_ONE" with no roles
    Then the response status should be 201
    When I create a membership for user "multiAppUserId" in application "MULTI_APP_TWO" with no roles
    Then the response status should be 201

  Scenario: Creating membership for non-existent user is rejected
    And an application with code "NO_USER_APP" exists
    When I create a membership for non-existent user in application "NO_USER_APP" with no roles
    Then the response status should be 404

  Scenario: Creating membership in non-existent application is rejected
    Given a user exists with id stored as "orphanUserId"
    When I create a membership for user "orphanUserId" in application "NON_EXISTENT_APP" with no roles
    Then the response status should be 404

  Scenario: Creating membership in inactive application is rejected
    Given a user exists with id stored as "inactiveAppUserId"
    And an application with code "DISABLED_APP_MEMBERSHIP" exists
    And the application "DISABLED_APP_MEMBERSHIP" is disabled
    When I create a membership for user "inactiveAppUserId" in application "DISABLED_APP_MEMBERSHIP" with no roles
    Then the response status should be 409

  Scenario: Creating membership with non-existent role is rejected
    Given a user exists with id stored as "badRoleUserId"
    And an application with code "BAD_ROLE_APP" exists
    When I create a membership for user "badRoleUserId" in application "BAD_ROLE_APP" with roles "NON_EXISTENT_ROLE"
    Then the response status should be 404

  Scenario: Creating membership with inactive role is rejected
    Given a user exists with id stored as "inactiveRoleUserId"
    And an application with code "INACTIVE_ROLE_APP" exists
    And a role with code "DISABLED_ROLE" and status "DISABLED" exists in application "INACTIVE_ROLE_APP"
    When I create a membership for user "inactiveRoleUserId" in application "INACTIVE_ROLE_APP" with roles "DISABLED_ROLE"
    Then the response status should be 409

  Scenario: Unauthenticated request is rejected
    Given a user exists with id stored as "noAuthUserId" with system credentials
    And an application with code "NO_AUTH_MEMBERSHIP_APP" exists with system credentials
    When I create a membership without authentication for user "noAuthUserId" in application "NO_AUTH_MEMBERSHIP_APP"
    Then the response status should be 401

  Scenario: Invalid API key is rejected
    Given a user exists with id stored as "badKeyUserId" with system credentials
    And an application with code "BAD_KEY_MEMBERSHIP_APP" exists with system credentials
    When I create a membership with invalid API key for user "badKeyUserId" in application "BAD_KEY_MEMBERSHIP_APP"
    Then the response status should be 401
