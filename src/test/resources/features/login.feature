@login
Feature: User Authentication

  Background:
    Given the user is on the login page

  @smoke @parallel
  Scenario: Successful login with valid credentials
    When the user logs in with username "standard_user" and password "secret_sauce"
    Then the user should be logged in successfully

  @smoke @parallel
  Scenario: Login fails with invalid credentials
    When the user logs in with username "invalid_user" and password "wrong_pass"
    Then an error message "Username and password do not match" should be displayed

  @regression @parallel
  Scenario Outline: Login with multiple credential sets
    When the user logs in with username "<username>" and password "<password>"
    Then an error message "<error>" should be displayed

    Examples:
      | username      | password    | error                                    |
      | locked_user   | secret_sauce | Sorry, this user has been locked out.   |
      |               | secret_sauce | Username is required                     |
      | standard_user |              | Password is required                     |
