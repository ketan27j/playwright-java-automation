package com.framework.pages;

import com.microsoft.playwright.options.LoadState;
import io.qameta.allure.Step;

public class AppLoginPage extends BasePage {

    static final String APP_BASE = "https://app.commenthook.com";

    private static final String EMAIL_INPUT    = "input[type='email']";
    private static final String PASSWORD_INPUT = "input[type='password']";
    private static final String SIGN_IN_BTN    = "button:has-text('Sign in')";
    private static final String ERROR_MSG      = "p:has-text('Invalid email or password')";
    private static final String GOOGLE_LINK    = "a:has-text('Continue with Google')";
    private static final String INSTAGRAM_LINK = "a:has-text('Continue with Instagram')";
    private static final String CREATE_ONE     = "a[href='/register']";
    private static final String LOGOUT_BTN     = "button:has-text('Logout')";

    @Step("Open app login page")
    public AppLoginPage open() {
        navigateTo(APP_BASE + "/login");
        return this;
    }

    @Step("Navigate to full URL: {url}")
    public void navigate(String url) {
        // Use NETWORKIDLE so the SPA router finishes its client-side redirect before we assert
        page().navigate(url);
        page().waitForLoadState(LoadState.NETWORKIDLE);
    }

    @Step("Login with email: {email}")
    public void login(String email, String password) {
        fill(EMAIL_INPUT, email);
        fill(PASSWORD_INPUT, password);
        click(SIGN_IN_BTN);
        page().waitForLoadState(LoadState.NETWORKIDLE);
    }

    @Step("Click Sign In without credentials")
    public void clickSignIn() {
        click(SIGN_IN_BTN);
    }

    @Step("Click Logout")
    public void clickLogout() {
        click(LOGOUT_BTN);
        page().waitForLoadState(LoadState.NETWORKIDLE);
    }

    @Step("Wait for login error to appear")
    public void waitForError() {
        assertVisible(ERROR_MSG);
    }

    public String heading() {
        return getText("h1");
    }

    public boolean isEmailFieldVisible() {
        return isVisible(EMAIL_INPUT);
    }

    public boolean isPasswordFieldVisible() {
        return isVisible(PASSWORD_INPUT);
    }

    public boolean isButtonVisible(String label) {
        return isVisible("button:has-text('" + label + "')");
    }

    public String getLinkHref(String text) {
        return getAttribute("a:has-text('" + text + "')", "href");
    }

    public boolean isErrorVisible() {
        return isVisible(ERROR_MSG);
    }

    public String errorMessage() {
        return getText(ERROR_MSG);
    }

    public String googleLinkHref() {
        return getAttribute(GOOGLE_LINK, "href");
    }

    public String instagramLinkHref() {
        return getAttribute(INSTAGRAM_LINK, "href");
    }

    @Step("Wait for URL to contain: {substring}")
    public void waitForUrlContaining(String substring) {
        page().waitForURL(url -> url.contains(substring));
    }

    public String currentUrl() {
        return getCurrentUrl();
    }
}
