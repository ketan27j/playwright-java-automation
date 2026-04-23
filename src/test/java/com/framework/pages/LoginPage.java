package com.framework.pages;

import io.qameta.allure.Step;

/**
 * Example Page Object for a login page.
 * All selectors live here — steps and features never reference raw selectors.
 */
public class LoginPage extends BasePage {

    // Selectors
    private static final String USERNAME_INPUT = "[data-testid='username']";
    private static final String PASSWORD_INPUT = "[data-testid='password']";
    private static final String LOGIN_BUTTON   = "[data-testid='login-btn']";
    private static final String ERROR_MESSAGE  = "[data-testid='error-msg']";
    private static final String WELCOME_HEADER = "[data-testid='welcome-header']";

    @Step("Open login page")
    public LoginPage open(String baseUrl) {
        navigateTo(baseUrl + "/login");
        return this;
    }

    @Step("Login with username: {username}")
    public LoginPage login(String username, String password) {
        fill(USERNAME_INPUT, username);
        fill(PASSWORD_INPUT, password);
        click(LOGIN_BUTTON);
        return this;
    }

    @Step("Assert login succeeded")
    public void assertLoginSuccess() {
        assertVisible(WELCOME_HEADER);
    }

    @Step("Assert error message: {expected}")
    public void assertErrorMessage(String expected) {
        assertContainsText(ERROR_MESSAGE, expected);
    }
}
