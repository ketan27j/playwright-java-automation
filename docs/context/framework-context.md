# Framework Context — Read this before generating any code

## Project: Playwright + Cucumber + Java 21 Test Framework

---

## Package Structure

```
src/test/java/com/framework/
├── config/ConfigManager.java     # Environment config — use ConfigManager.baseUrl()
├── hooks/Hooks.java              # Before/After lifecycle — DO NOT modify
├── pages/BasePage.java           # Base class for all page objects — always extend this
├── pages/<FeatureName>Page.java  # Page objects go here
├── steps/<FeatureName>Steps.java # Step definitions go here
└── utils/BrowserManager.java     # ThreadLocal browser — never instantiate Page directly

src/test/resources/
├── features/<feature>.feature    # Gherkin feature files go here
```

---

## Rules — follow these exactly

### Page Objects
- ALWAYS extend `BasePage`
- ALWAYS declare selectors as `private static final String` constants at the top
- NEVER reference `page` directly — use inherited helpers: `click()`, `fill()`, `getText()`, `navigateTo()`, `assertVisible()`, `assertContainsText()`
- ALWAYS annotate public methods with `@Step("description")` from `io.qameta.allure.Step`
- Return `this` for fluent chaining where it makes sense
- One page object per page/component

### Step Definitions
- ALWAYS use constructor injection for page objects (never `new` inside a method)
- Map step text EXACTLY to the Gherkin in the feature file
- Use `Allure.step("description")` for sub-steps if needed
- Import: `io.cucumber.java.en.*`

### Feature Files
- Use Background for shared preconditions
- Tag every scenario with at least `@parallel` and one feature tag e.g. `@login`
- Use Scenario Outline + Examples for data-driven cases
- Write from the USER's perspective — not implementation detail

### Selectors — priority order
1. `[data-testid='x']` — preferred
2. `[aria-label='x']` or role-based: `role=button[name='Submit']`
3. `text='exact text'`
4. CSS: `.class-name` — only if no better option
5. NEVER use XPath unless absolutely unavoidable
6. NEVER use positional selectors like `nth-child` unless nothing else works

---

## BasePage helpers available

```java
protected void navigateTo(String url)
protected void click(String selector)
protected void fill(String selector, String value)
protected String getText(String selector)
protected void assertVisible(String selector)
protected void assertContainsText(String selector, String expected)
protected Page page()   // raw page access — use sparingly
```

---

## ConfigManager helpers

```java
ConfigManager.baseUrl()    // → String base URL from config
ConfigManager.browser()    // → "chromium" / "firefox" / "webkit"
ConfigManager.headless()   // → boolean
ConfigManager.timeout()    // → int milliseconds
```

---

## Example Page Object

```java
package com.framework.pages;

import io.qameta.allure.Step;

public class LoginPage extends BasePage {

    private static final String USERNAME_INPUT = "[data-testid='username']";
    private static final String PASSWORD_INPUT = "[data-testid='password']";
    private static final String LOGIN_BUTTON   = "[data-testid='login-btn']";
    private static final String ERROR_MESSAGE  = "[data-testid='error-msg']";

    @Step("Open login page")
    public LoginPage open() {
        navigateTo(ConfigManager.baseUrl() + "/login");
        return this;
    }

    @Step("Login as {username}")
    public LoginPage login(String username, String password) {
        fill(USERNAME_INPUT, username);
        fill(PASSWORD_INPUT, password);
        click(LOGIN_BUTTON);
        return this;
    }

    @Step("Assert error: {expected}")
    public void assertError(String expected) {
        assertContainsText(ERROR_MESSAGE, expected);
    }
}
```

## Example Step Definitions

```java
package com.framework.steps;

import com.framework.pages.LoginPage;
import io.cucumber.java.en.*;

public class LoginSteps {

    private final LoginPage loginPage = new LoginPage();

    @Given("the user is on the login page")
    public void onLoginPage() {
        loginPage.open();
    }

    @When("the user logs in with {string} and {string}")
    public void login(String user, String pass) {
        loginPage.login(user, pass);
    }
}
```

## Example Feature File

```gherkin
@login @parallel
Feature: User Login

  Background:
    Given the user is on the login page

  @smoke
  Scenario: Valid login
    When the user logs in with "admin" and "password123"
    Then the user should see the dashboard

  @regression
  Scenario Outline: Invalid login attempts
    When the user logs in with "<user>" and "<pass>"
    Then an error "<error>" should be shown

    Examples:
      | user  | pass  | error                  |
      |       | pass  | Username is required   |
      | user  |       | Password is required   |
```
