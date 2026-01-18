Feature: Resend verification email

  Background:
    Given the system is running

  Scenario: Resend sends a new email for a pending user
    Given I am a registered user "resend@example.com"
    And I wait "1100" milliseconds
    When I request a verification email resend for "resend@example.com"
    Then the resend response status should be 202
    And the email outbox size should be 2

  Scenario: Resend is throttled by minimum interval
    Given I am a registered user "throttle@example.com"
    And I wait "1100" milliseconds
    When I request a verification email resend for "throttle@example.com"
    And I request a verification email resend for "throttle@example.com"
    Then the resend response status should be 202
    And the email outbox size should be 2

  Scenario: Resend does not send for active users
    Given I am a registered user "active@example.com"
    And I have extracted the verification token from the last email
    And I verify the email using the token
    And I wait "1100" milliseconds
    When I request a verification email resend for "active@example.com"
    Then the resend response status should be 202
    And the email outbox size should be 1

  Scenario: Resend is limited by the hourly cap
    Given I am a registered user "limit@example.com"
    And I wait "1100" milliseconds
    When I request a verification email resend for "limit@example.com"
    And I wait "1100" milliseconds
    And I request a verification email resend for "limit@example.com"
    And I wait "1100" milliseconds
    And I request a verification email resend for "limit@example.com"
    Then the resend response status should be 202
    And the email outbox size should be 3
