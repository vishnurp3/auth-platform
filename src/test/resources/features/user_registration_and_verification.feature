Feature: Registration and email verification

  Scenario: Register user creates pending user and sends verification email
    Given the system is running
    When I register with email "alice@example.com" and password "Str0ngPassw0rd!"
    Then the response status should be 201
    And the user status should be "PENDING_VERIFICATION"
    And a verification email should be sent to "alice@example.com"

  Scenario: User verifies email and becomes active
    Given the system is running
    When I register with email "bob@example.com" and password "Str0ngPassw0rd!"
    And I extract the verification token from the sent email
    And I verify the email using that token
    Then the response status should be 200
    And the verified user status should be "ACTIVE"
