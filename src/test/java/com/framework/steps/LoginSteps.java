package com.framework.steps;

import com.framework.config.ConfigManager;
import com.framework.pages.LoginPage;
import io.cucumber.java.en.*;
import io.qameta.allure.Allure;

public class LoginSteps {

    // Page objects are instantiated per-step-definition class (thread-safe via ThreadLocal in BrowserManager)
    private final LoginPage loginPage = new LoginPage();

    @Given("the user is on the login page")
    public void theUserIsOnTheLoginPage() {
        Allure.step("Opening login page");
        loginPage.open(ConfigManager.baseUrl());
    }

    @When("the user logs in with username {string} and password {string}")
    public void theUserLogsIn(String username, String password) {
        loginPage.login(username, password);
    }

    @Then("the user should be logged in successfully")
    public void theUserShouldBeLoggedIn() {
        loginPage.assertLoginSuccess();
    }

    @Then("an error message should be displayed")
    public void anErrorMessageShouldBeDisplayed() {
        loginPage.assertErrorMessageAny();
    }
}
