t as # Complete Guide: Writing New Test Cases (For Java Beginners)

This is a simple step-by-step guide for writing tests in this Playwright Java + Cucumber framework. No advanced Java knowledge required.

---

## 🎯 How this framework works (5 second overview)

This project uses 3 layers **exactly in this order for EVERY test**:

| File Type | What it does | Where it lives |
|---|---|---|
| 1️⃣ **Feature File** | Human readable test scenarios (Gherkin) | `src/test/resources/features/` |
| 2️⃣ **Steps Class** | Maps feature file sentences to actual code | `src/test/java/com/framework/steps/` |
| 3️⃣ **Page Object** | All element selectors + page actions live here | `src/test/java/com/framework/pages/` |

✅ **RULE**: Never put selectors directly in steps or features. Always use Page Objects.

---

## 🚀 Step 1: Create your new test

Let's create a completely new test for the Products page as an example. You can follow this exact pattern for any page.

---

### ✅ Step 1.1: Create Feature File

Create new file: `src/test/resources/features/products.feature`

```gherkin
@products
Feature: Products Page Tests

  # Runs BEFORE every scenario in this file
  Background:
    Given the user is logged in

  @smoke @parallel
  Scenario: Products list is displayed after login
    Then all 6 products should be visible
    And product prices are shown correctly

  @regression
  Scenario: User can add product to cart
    When user adds first product to cart
    Then cart badge should show count 1
```

✅ Notes for beginners:
- Tags like `@smoke` let you run groups of tests
- `@parallel` tag means this test can run at the same time as other tests
- Each sentence line must match EXACTLY what we write later in Steps class

---

### ✅ Step 1.2: Create Page Object Class

Create new file: `src/test/java/com/framework/pages/ProductsPage.java`

```java
package com.framework.pages;

import io.qameta.allure.Step;

// ✅ ALWAYS extend BasePage - it gives you all pre-made methods
public class ProductsPage extends BasePage {

    // 🔍 First: list ALL selectors here at the TOP
    // Never hardcode selectors anywhere else
    private static final String PRODUCT_CARD  = ".inventory_item";
    private static final String ADD_TO_CART   = ".btn_inventory";
    private static final String CART_BADGE    = ".shopping_cart_badge";
    private static final String PRODUCT_PRICE = ".inventory_item_price";


    // ✅ Next: write methods for actions you do on this page
    @Step("Add first product to cart")
    public ProductsPage addFirstProduct() {
        click(ADD_TO_CART);
        return this;
    }


    // ✅ Next: write assert methods
    @Step("Verify all products are displayed")
    public void assertProductsLoaded() {
        // BasePage has pre-made asserts you can use directly
        assertVisible(PRODUCT_CARD);
    }

    @Step("Verify cart count: {count}")
    public void assertCartCount(int count) {
        assertContainsText(CART_BADGE, String.valueOf(count));
    }

    @Step("Verify product prices are present")
    public void assertPricesDisplayed() {
        assertVisible(PRODUCT_PRICE);
    }
}
```

✅ What you get for FREE from `BasePage` (you never have to write these):
- `click()`
- `fill()` 
- `navigateTo()`
- `assertVisible()`
- `assertContainsText()`
- `getText()`

All of these are automatically thread safe, logged, and show up in Allure reports.

---

### ✅ Step 1.3: Create Steps Class

Create new file: `src/test/java/com/framework/steps/ProductsSteps.java`

```java
package com.framework.steps;

import com.framework.pages.ProductsPage;
import io.cucumber.java.en.*;

public class ProductsSteps {

    // ✅ 1. Create your page object once here
    // This is 100% thread safe - you never need to worry about this
    private final ProductsPage productsPage = new ProductsPage();


    // ✅ 2. Map each sentence from feature file here
    // Copy paste the sentence EXACTLY
    @Then("all 6 products should be visible")
    public void allProductsVisible() {
        productsPage.assertProductsLoaded();
    }

    @Then("product prices are shown correctly")
    public void pricesShown() {
        productsPage.assertPricesDisplayed();
    }

    @When("user adds first product to cart")
    public void addFirstProduct() {
        productsPage.addFirstProduct();
    }

    @Then("cart badge should show count {int}")
    public void cartCountIs(int count) {
        productsPage.assertCartCount(count);
    }
}
```

---

### ✅ Step 1.4: Add the login step (reuse existing code)

You don't need to write login again! It already exists. Just add this line to the existing `LoginSteps.java` if it's not there:

```java
@Given("the user is logged in")
public void userIsLoggedIn() {
    loginPage.open(ConfigManager.baseUrl());
    loginPage.login("standard_user", "secret_sauce");
}
```

---

## 🏃 Run your new test

```bash
# Run only your new products tests
./gradlew test -Dcucumber.filter.tags="@products"

# Run in visible browser mode
./gradlew test -Dcucumber.filter.tags="@products" -Dheadless=false

# Run with report
./gradlew runTests -Dcucumber.filter.tags="@products"
```

---

## ❌ Most common mistakes for beginners

| ❌ Don't do this | ✅ Do this instead |
|---|---|
| Use `static Page page` field | Always use `page()` method from BasePage |
| Put selectors in Steps class | All selectors go in Page Object at top |
| Write Playwright code directly in Steps | Always go through Page Object methods |
| Forget `@Step` annotation | Add `@Step` to every public method in page objects |
| Use hardcoded urls | Always use `ConfigManager.baseUrl()` |
| Make page object fields static | Always make them `final` instance fields |

---

## 📝 Summary Checklist for every new test

✅ 1. Create Feature file with scenarios
✅ 2. Create Page Object extending BasePage
✅ 3. Add all selectors as private static final at top
✅ 4. Add action and assert methods with @Step
✅ 5. Create Steps class with exact matching sentences
✅ 6. Add appropriate tags
✅ 7. Test it runs correctly

---

## 🎯 Ready made methods you can use

From `BasePage` you already have:

```java
// Actions
navigateTo(url)
click(selector)
fill(selector, text)
getText(selector)

// Asserts
assertVisible(selector)
assertContainsText(selector, expectedText)
```

That's it! You don't need to learn 100 Playwright methods. This is all you will need for 95% of test cases.