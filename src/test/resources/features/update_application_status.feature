Feature: Update Application Status (Enable/Disable Application)

  Background:
    Given the system is running

  Scenario: Admin disables an active application
    Given I am authenticated as admin
    And I create an application with code "APP_TO_DISABLE" and name "App To Disable"
    Then the response status should be 201
    And the application status should be "ACTIVE"
    When I update the status of application "APP_TO_DISABLE" to "DISABLED"
    Then the response status should be 200
    And the application status should be "DISABLED"
    And the application code should be "APP_TO_DISABLE"
    And the application name should be "App To Disable"

  Scenario: Admin enables a disabled application
    Given I am authenticated as admin
    And I create an application with code "APP_TO_ENABLE" name "App To Enable" and status "DISABLED"
    Then the response status should be 201
    And the application status should be "DISABLED"
    When I update the status of application "APP_TO_ENABLE" to "ACTIVE"
    Then the response status should be 200
    And the application status should be "ACTIVE"

  Scenario: Application code is case-insensitive for status update
    Given I am authenticated as admin
    And I create an application with code "CASE_INSENSITIVE" and name "Case Test"
    Then the response status should be 201
    When I update the status of application "case_insensitive" to "DISABLED"
    Then the response status should be 200
    And the application code should be "CASE_INSENSITIVE"
    And the application status should be "DISABLED"

  Scenario: Status value is case-insensitive
    Given I am authenticated as admin
    And I create an application with code "STATUS_CASE" and name "Status Case Test"
    Then the response status should be 201
    When I update the status of application "STATUS_CASE" to "disabled"
    Then the response status should be 200
    And the application status should be "DISABLED"

  Scenario: Updating non-existent application returns 404
    Given I am authenticated as admin
    When I update the status of application "NON_EXISTENT_APP" to "DISABLED"
    Then the response status should be 404

  Scenario: Invalid status value is rejected
    Given I am authenticated as admin
    And I create an application with code "INVALID_STATUS_APP" and name "Invalid Status Test"
    Then the response status should be 201
    When I update the status of application "INVALID_STATUS_APP" to "INVALID"
    Then the response status should be 400

  Scenario: Unauthenticated request is rejected
    When I update the status of application without authentication "SOME_APP" to "DISABLED"
    Then the response status should be 401

  Scenario: Invalid API key is rejected
    Given I am authenticated with invalid admin key
    When I update the status of application "SOME_APP" to "DISABLED"
    Then the response status should be 401

  Scenario: Timestamps are updated when status changes
    Given I am authenticated as admin
    And I create an application with code "TIMESTAMP_APP" and name "Timestamp Test"
    Then the response status should be 201
    And the application should have timestamps
    When I update the status of application "TIMESTAMP_APP" to "DISABLED"
    Then the response status should be 200
    And the application should have timestamps
