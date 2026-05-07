package com.framework.pages;

import com.microsoft.playwright.options.LoadState;
import io.qameta.allure.Step;

public class AppRegisterPage extends BasePage {

    private static final String EMAIL_INPUT    = "input[type='email']";
    private static final String PASSWORD_INPUT = "input[type='password']";
    private static final String REGISTER_BTN   = "button:has-text('Get started free')";
    private static final String ERROR_MSG      = "p:has-text('Email already registered')";
    private static final String SIGN_IN_LINK   = "a[href='/login']";

    @Step("Open app register page")
    public AppRegisterPage open() {
        navigateTo(AppLoginPage.APP_BASE + "/register");
        return this;
    }

    @Step("Register with name: {name}, email: {email}")
    public void register(String name, String email, String password) {
        // Name field is the first input (no type attribute, comes before email/password)
        page().locator("input").first().fill(name);
        fill(EMAIL_INPUT, email);
        fill(PASSWORD_INPUT, password);
        click(REGISTER_BTN);
        page().waitForLoadState(LoadState.NETWORKIDLE);
    }

    @Step("Wait for register error to appear")
    public void waitForError() {
        assertVisible(ERROR_MSG);
    }

    public String heading() {
        return getText("h1");
    }

    public boolean isNameFieldVisible() {
        return page().locator("input").first().isVisible();
    }

    public boolean isEmailFieldVisible() {
        return isVisible(EMAIL_INPUT);
    }

    public boolean isPasswordFieldVisible() {
        return isVisible(PASSWORD_INPUT);
    }

    public boolean isRegisterButtonVisible() {
        return isVisible(REGISTER_BTN);
    }

    public String signInLinkHref() {
        return getAttribute(SIGN_IN_LINK, "href");
    }

    public boolean isErrorVisible() {
        return isVisible(ERROR_MSG);
    }

    public String errorMessage() {
        return getText(ERROR_MSG);
    }
}
