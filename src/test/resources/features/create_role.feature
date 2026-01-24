Feature: Create Role (Role Onboarding to Application)

  Background:
    Given the system is running
    And I am authenticated as admin

  Scenario: Admin creates a new role with default status
    Given an application with code "ROLE_TEST_APP" exists
    When I create a role with code "USER" and display name "User" in application "ROLE_TEST_APP"
    Then the response status should be 201
    And the role code should be "USER"
    And the role display name should be "User"
    And the role status should be "ACTIVE"
    And the role should have a valid id
    And the role should belong to application "ROLE_TEST_APP"
    And the role should have timestamps

  Scenario: Admin creates a role with optional description
    Given an application with code "ROLE_DESC_APP" exists
    When I create a role with code "ADMIN" display name "Administrator" and description "Full administrative access" in application "ROLE_DESC_APP"
    Then the response status should be 201
    And the role description should be "Full administrative access"

  Scenario: Admin creates a role with DISABLED status
    Given an application with code "DISABLED_ROLE_APP" exists
    When I create a role with code "SUSPENDED" display name "Suspended User" and status "DISABLED" in application "DISABLED_ROLE_APP"
    Then the response status should be 201
    And the role status should be "DISABLED"

  Scenario: Role code is normalized to uppercase
    Given an application with code "NORMALIZE_ROLE_APP" exists
    When I create a role with code "lower_case_role" and display name "Lower Case Role" in application "NORMALIZE_ROLE_APP"
    Then the response status should be 201
    And the role code should be "LOWER_CASE_ROLE"

  Scenario: Duplicate role code within same application is rejected
    Given an application with code "DUP_ROLE_APP" exists
    When I create a role with code "DUPLICATE" and display name "First Role" in application "DUP_ROLE_APP"
    Then the response status should be 201
    When I create a role with code "DUPLICATE" and display name "Second Role" in application "DUP_ROLE_APP"
    Then the response status should be 409

  Scenario: Same role code in different applications is allowed
    Given an application with code "APP_ONE" exists
    And an application with code "APP_TWO" exists
    When I create a role with code "ADMIN" and display name "Admin One" in application "APP_ONE"
    Then the response status should be 201
    When I create a role with code "ADMIN" and display name "Admin Two" in application "APP_TWO"
    Then the response status should be 201

  Scenario: Creating role in non-existent application is rejected
    When I create a role with code "ORPHAN" and display name "Orphan Role" in application "NON_EXISTENT_APP"
    Then the response status should be 404

  Scenario Outline: Invalid role code is rejected
    Given an application with code "INVALID_CODE_APP" exists
    When I create a role with code "<code>" and display name "Test Role" in application "INVALID_CODE_APP"
    Then the response status should be 400

    Examples:
      | code                                                  |
      | <empty>                                               |
      | A                                                     |
      | 1INVALID                                              |
      | INVALID-CODE                                          |
      | INVALID CODE                                          |

  Scenario: Invalid role display name is rejected
    Given an application with code "INVALID_NAME_APP" exists
    When I create a role with code "VALID_CODE" and display name "<empty>" in application "INVALID_NAME_APP"
    Then the response status should be 400

  Scenario: Unauthenticated request is rejected
    Given an application with code "NO_AUTH_ROLE_APP" exists with system credentials
    When I create a role without authentication with code "NO_AUTH" and display name "No Auth Role" in application "NO_AUTH_ROLE_APP"
    Then the response status should be 401

  Scenario: Invalid API key is rejected
    Given an application with code "BAD_KEY_ROLE_APP" exists with system credentials
    When I create a role with invalid API key with code "BAD_KEY" and display name "Bad Key Role" in application "BAD_KEY_ROLE_APP"
    Then the response status should be 401
