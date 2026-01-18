Feature: User registration

  Background:
    Given the system is running

  Scenario: Registering a new user sends a verification email
    When I register with email "alice@example.com" and password "Str0ngPassw0rd!"
    Then the response status should be 201
    And the user status should be "PENDING_VERIFICATION"
    And the email outbox size should be 1
    And the last verification email should be sent to "alice@example.com"

  Scenario: Duplicate registration is rejected
    When I register with email "dup@example.com" and password "Str0ngPassw0rd!"
    And the email outbox size should be 1
    And I register with email "dup@example.com" and password "Str0ngPassw0rd!"
    Then the response status should be 409
    And the email outbox size should be 1

  Scenario Outline: Invalid registration input is rejected
    When I register with email "<email>" and password "<password>"
    Then the response status should be 400

    Examples:
      | email             | password          |
      | <empty>           | Str0ngPassw0rd!   |
      | aliceexample.com  | Str0ngPassw0rd!   |
      | alice@example.com | short             |
