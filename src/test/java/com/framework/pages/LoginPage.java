package com.framework.pages;

import io.qameta.allure.Step;

/**
 * Example Page Object for a login page.
 * All selectors live here — steps and features never reference raw selectors.
 */
public class LoginPage extends BasePage {

    // Selectors - SauceDemo uses standard HTML ids
    private static final String USERNAME_INPUT = "#user-name";
    private static final String PASSWORD_INPUT = "#password";
    private static final String LOGIN_BUTTON   = "#login-button";
    private static final String ERROR_MESSAGE  = "h3";
    private static final String APP_HEADER     = ".app_logo";

    @Step("Open login page")
    public LoginPage open(String baseUrl) {
        navigateTo(baseUrl);
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
        assertVisible(APP_HEADER);
    }

    @Step("Assert error message: {expected}")
    public void assertErrorMessage(String expected) {
        assertContainsText(ERROR_MESSAGE, expected);
    }

    @Step("Assert error message is displayed")
    public void assertErrorMessageAny() {
        assertVisible(ERROR_MESSAGE);
    }
}
