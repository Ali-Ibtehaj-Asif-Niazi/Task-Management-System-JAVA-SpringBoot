Feature: User Management and Authentication

  As a user of the platform,
  I want to be able to register, log in, and retrieve user information
  to interact with the system.

  Scenario: Successful User Registration
    Given I want to create a new user with the following details:
      | name     | email              | password      |
      | John Doe | john.doe@example.com | securePassword123 |
    When I send a POST request to "/users" to register
    Then the response status code should be 201
    And the response body should contain the email "john.doe@example.com"
    And the user "john.doe@example.com" should exist in the database with a hashed password

  Scenario: Attempt to register with an existing email
    Given a user with email "jane.doe@example.com" and password "password456" is already registered
    And I want to create a new user with the following details:
      | name      | email              | password      |
      | Jane Smith| jane.doe@example.com | anotherPassword |
    When I send a POST request to "/users" to register
    Then the response status code should be 409
    And the response body should contain the message "Email already exists"

  Scenario: Successful User Validation (Login)
    Given a user with email "login.user@example.com" and password "aVeryStrongPass" is already registered
    When I send a POST request to "/auth/validate" with email "login.user@example.com" and password "aVeryStrongPass"
    Then the response status code should be 200
    And the validation response should have a status of "VALID"
    And the validation response should contain the email "login.user@example.com"

  Scenario: User Validation with incorrect password
    Given a user with email "login.user@example.com" and password "aVeryStrongPass" is already registered
    When I send a POST request to "/auth/validate" with email "login.user@example.com" and password "wrongPassword"
    Then the response status code should be 401
    And the validation response should have a status of "INVALID"

  Scenario: Get a specific user by ID
    Given a user with email "find.me@example.com" and password "findme123" is already registered
    When I send a GET request to retrieve the user with email "find.me@example.com"
    Then the response status code should be 200
    And the response body should contain the user name "find.me" and email "find.me@example.com"