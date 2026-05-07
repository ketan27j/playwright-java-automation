package com.framework.steps;

import com.framework.pages.AppLoginPage;
import com.framework.pages.AppRegisterPage;
import io.cucumber.java.en.*;

import static org.junit.jupiter.api.Assertions.*;

public class AppAuthSteps {

    private final AppLoginPage loginPage     = new AppLoginPage();
    private final AppRegisterPage registerPage = new AppRegisterPage();

    // ── Given ─────────────────────────────────────────────────────────────────

    @Given("the user is on the CommentHook login page")
    public void openLoginPage() {
        loginPage.open();
    }

    @Given("the user is on the CommentHook register page")
    public void openRegisterPage() {
        registerPage.open();
    }

    @Given("the user is logged in with email {string} and password {string}")
    public void userIsLoggedIn(String email, String password) {
        loginPage.open();
        loginPage.login(email, password);
    }

    @Given("the user is not logged in")
    public void userIsNotLoggedIn() {
        // Fresh browser context per scenario — no action needed
    }

    // ── When ──────────────────────────────────────────────────────────────────

    @When("the user logs in with email {string} and password {string}")
    public void loginWith(String email, String password) {
        loginPage.login(email, password);
    }

    @When("the user clicks Sign In without entering credentials")
    public void clickSignInEmpty() {
        loginPage.clickSignIn();
    }

    @When("the user clicks logout")
    public void clickLogout() {
        loginPage.clickLogout();
    }

    @When("the user registers with name {string} email {string} and password {string}")
    public void registerWith(String name, String email, String password) {
        registerPage.register(name, email, password);
    }

    @When("the user tries to open the app URL {string}")
    public void openAppUrl(String url) {
        loginPage.navigate(url);
    }

    // ── Then — login page ─────────────────────────────────────────────────────

    @Then("the login heading should be {string}")
    public void loginHeadingEquals(String expected) {
        assertEquals(expected, loginPage.heading(), "login page heading");
    }

    @Then("the email and password fields should be visible")
    public void emailAndPasswordVisible() {
        assertTrue(loginPage.isEmailFieldVisible(), "email field visible");
        assertTrue(loginPage.isPasswordFieldVisible(), "password field visible");
    }

    @Then("the {string} button should be visible")
    public void buttonVisible(String label) {
        assertTrue(loginPage.isButtonVisible(label),
                "Expected button '%s' to be visible".formatted(label));
    }

    @Then("the Google OAuth link should point to {string}")
    public void googleLinkHref(String expected) {
        assertEquals(expected, loginPage.googleLinkHref(), "Google OAuth href");
    }

    @Then("the Instagram OAuth link should point to {string}")
    public void instagramLinkHref(String expected) {
        assertEquals(expected, loginPage.instagramLinkHref(), "Instagram OAuth href");
    }

    @Then("the {string} link should point to {string}")
    public void linkPointsTo(String label, String expected) {
        String actual = loginPage.getLinkHref(label);
        assertEquals(expected, actual, "'%s' link href".formatted(label));
    }

    @Then("the login error {string} should be displayed")
    public void loginErrorDisplayed(String expected) {
        loginPage.waitForError();  // wait for async API response to update the DOM
        assertTrue(loginPage.errorMessage().contains(expected),
                "Expected login error to contain '%s'".formatted(expected));
    }

    @Then("the user should remain on the login page")
    public void userRemainsOnLoginPage() {
        assertTrue(loginPage.currentUrl().contains("/login"),
                "Expected user to remain on login page but URL was: " + loginPage.currentUrl());
    }

    @Then("the current URL should contain {string}")
    public void urlContains(String expected) {
        loginPage.waitForUrlContaining(expected);
    }

    // ── Then — register page ──────────────────────────────────────────────────

    @Then("the register heading should be {string}")
    public void registerHeadingEquals(String expected) {
        assertEquals(expected, registerPage.heading(), "register page heading");
    }

    @Then("the name, email, and password fields should be visible")
    public void registerFieldsVisible() {
        assertTrue(registerPage.isNameFieldVisible(), "name field visible");
        assertTrue(registerPage.isEmailFieldVisible(), "email field visible");
        assertTrue(registerPage.isPasswordFieldVisible(), "password field visible");
    }

    @Then("the register error {string} should be displayed")
    public void registerErrorDisplayed(String expected) {
        registerPage.waitForError();  // wait for async API response to update the DOM
        assertTrue(registerPage.errorMessage().contains(expected),
                "Expected register error to contain '%s'".formatted(expected));
    }
}
