Feature: Create Application (Onboard Application)

  Background:
    Given the system is running

  Scenario: Admin creates a new application with default status
    Given I am authenticated as admin
    When I create an application with code "my_app" and name "My Application"
    Then the response status should be 201
    And the application code should be "MY_APP"
    And the application name should be "My Application"
    And the application status should be "ACTIVE"
    And the application should have a valid id
    And the application should have timestamps

  Scenario: Admin creates an application with optional description
    Given I am authenticated as admin
    When I create an application with code "app_with_desc" name "App With Desc" and description "This is a test application"
    Then the response status should be 201
    And the application description should be "This is a test application"

  Scenario: Admin creates an application with DISABLED status
    Given I am authenticated as admin
    When I create an application with code "disabled_app" name "Disabled App" and status "DISABLED"
    Then the response status should be 201
    And the application status should be "DISABLED"

  Scenario: Application code is normalized to uppercase
    Given I am authenticated as admin
    When I create an application with code "lower_case_code" and name "Test App"
    Then the response status should be 201
    And the application code should be "LOWER_CASE_CODE"

  Scenario: Duplicate application code is rejected
    Given I am authenticated as admin
    When I create an application with code "duplicate_app" and name "First App"
    Then the response status should be 201
    When I create an application with code "duplicate_app" and name "Second App"
    Then the response status should be 409

  Scenario: Duplicate application code is rejected case-insensitively
    Given I am authenticated as admin
    When I create an application with code "CASE_TEST" and name "First App"
    Then the response status should be 201
    When I create an application with code "case_test" and name "Second App"
    Then the response status should be 409

  Scenario Outline: Invalid application code is rejected
    Given I am authenticated as admin
    When I create an application with code "<code>" and name "Test App"
    Then the response status should be 400

    Examples:
      | code                                                  |
      | <empty>                                               |
      | A                                                     |
      | 1INVALID                                              |
      | INVALID-CODE                                          |
      | INVALID CODE                                          |

  Scenario Outline: Invalid application input is rejected
    Given I am authenticated as admin
    When I create an application with code "<code>" and name "<name>"
    Then the response status should be 400

    Examples:
      | code      | name    |
      | VALID_APP | <empty> |

  Scenario: Unauthenticated request is rejected
    When I create an application without authentication with code "NO_AUTH" and name "No Auth App"
    Then the response status should be 401

  Scenario: Invalid API key is rejected
    Given I am authenticated with invalid admin key
    When I create an application with code "BAD_KEY" and name "Bad Key App"
    Then the response status should be 401
