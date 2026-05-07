@auth
Feature: CommentHook App Authentication

  @login @smoke @parallel
  Scenario: Login page displays all expected elements
    Given the user is on the CommentHook login page
    Then the login heading should be "Sign in to CommentHook"
    And the email and password fields should be visible
    And the "Sign in" button should be visible
    And the Google OAuth link should point to "/api/auth/google"
    And the Instagram OAuth link should point to "/api/auth/instagram"
    And the "Create one" link should point to "/register"

  @login @smoke @parallel
  Scenario: Successful login redirects to the automations page
    Given the user is on the CommentHook login page
    When the user logs in with email "testuser.ch@mailinator.com" and password "TestPass@123"
    Then the current URL should contain "/automations"

  @login @smoke @parallel
  Scenario: Login fails with wrong credentials
    Given the user is on the CommentHook login page
    When the user logs in with email "wrong@example.com" and password "wrongpass"
    Then the login error "Invalid email or password" should be displayed

  @login @regression @parallel
  Scenario: Login form prevents submission with empty fields
    Given the user is on the CommentHook login page
    When the user clicks Sign In without entering credentials
    Then the user should remain on the login page

  @login @regression @parallel
  Scenario: Logout redirects to the login page
    Given the user is logged in with email "testuser.ch@mailinator.com" and password "TestPass@123"
    When the user clicks logout
    Then the current URL should contain "/login"

  @register @regression @parallel
  Scenario: Register page displays all expected elements
    Given the user is on the CommentHook register page
    Then the register heading should be "Create your account"
    And the name, email, and password fields should be visible
    And the "Get started free" button should be visible
    And the "Sign in" link should point to "/login"

  @register @regression @parallel
  Scenario: Registration fails with an already-registered email
    Given the user is on the CommentHook register page
    When the user registers with name "Test" email "testuser.ch@mailinator.com" and password "TestPass@123"
    Then the register error "Email already registered" should be displayed

  @security @regression @parallel
  Scenario: Accessing a protected route without login redirects to the login page
    Given the user is not logged in
    When the user tries to open the app URL "https://app.commenthook.com/automations"
    Then the current URL should contain "/login"
