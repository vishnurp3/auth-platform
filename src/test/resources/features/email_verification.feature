Feature: Email verification

  Background:
    Given the system is running

  Scenario: User verifies email and becomes active
    Given I am a registered user "bob@example.com"
    And I have extracted the verification token from the last email
    When I verify the email using the token
    Then the response status should be 200
    And the verified user status should be "ACTIVE"

  Scenario: Verification token cannot be reused
    Given I am a registered user "reuse@example.com"
    And I have extracted the verification token from the last email
    When I verify the email using the token
    And I verify the email using the token
    Then the response status should be 409

  Scenario: Unknown verification token is rejected
    When I verify the email using a valid but unknown token
    Then the response status should be 409

  Scenario Outline: Invalid verification tokens are rejected
    When I verify the email using token "<token>"
    Then the response status should be 400

    Examples:
      | token       |
      | <empty>     |
      | not-a-token |
      | abc.def     |
