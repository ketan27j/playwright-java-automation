# Data Driven Testing Guide
## For running same test steps with multiple datasets (5+ rows)

This framework natively supports Cucumber **Scenario Outline** which is the official and recommended way to run identical test steps with different data sets.

---

## ✅ How it works for your requirement:
> 1 test with 10 steps ✕ 5 different data sets = 5 separate test executions, each runs all 10 steps with their own data

---

## 📝 Complete Working Example

### Step 1: Create Feature File with Scenario Outline
```gherkin
@yourFeature @dataDriven @parallel
Feature: Your Test Feature

  # Runs before EVERY dataset execution (once per test row)
  Background:
    Given the user is logged in
    And navigates to target page

  # This will run ONCE FOR EACH ROW in Examples table
  Scenario Outline: Test XYZ functionality with different inputs
    # YOUR 10 TEST STEPS GO HERE - use placeholders <paramName>
    When user enters value "<field1>"
    And user selects option "<field2>"
    And user clicks submit
    Then system should show status "<expectedResult>"
    And validation message should contain "<expectedMessage>"
    # Add all your remaining 10 steps here...

    # DEFINE YOUR 5 DATA SETS HERE
    Examples:
      | field1  | field2  | expectedResult | expectedMessage |
      |---------|---------|----------------|-----------------|
      | value1  | optionA | SUCCESS        | Done correctly  |
      | value2  | optionB | ERROR          | Invalid input   |
      | value3  | optionC | WARNING        | Partial match   |
      | value4  | optionD | SUCCESS        | All good        |
      | value5  | optionA | ERROR          | Missing field   |
```

---

### Step 2: Step Definitions (100% same as normal tests)
Your Step class does NOT need any changes for data driven tests. Just write normal step methods with parameters:

```java
@When("user enters value {string}")
public void userEntersValue(String value) {
    yourPage.enterValue(value);
}

@When("user selects option {string}")
public void userSelectsOption(String option) {
    yourPage.selectOption(option);
}

@Then("system should show status {string}")
public void verifyStatus(String expectedStatus) {
    yourPage.assertStatus(expectedStatus);
}
```

✅ Cucumber automatically injects the correct value from the Examples table for each run.

---

## 🚀 Running your data driven test
```bash
# Run normally - all 5 datasets will execute
./gradlew test -Dcucumber.filter.tags="@dataDriven"

# Run in parallel (default already enabled in this framework)
./gradlew test -Dcucumber.filter.tags="@dataDriven" -Dparallelism=4

# View results in Allure report - you will see 5 separate test cases
./gradlew runTests -Dcucumber.filter.tags="@dataDriven"
```

---

## ✅ Framework Specific Best Practices
1. **Always use `@parallel` tag** - all 5 datasets will run in parallel threads completely isolated
2. **Never use static variables** - every dataset run gets fresh Browser + Page automatically
3. **Each row is fully independent** - browser is closed and reopened for every data row
4. **Traces are generated per dataset** - you will get 5 separate trace files if tracing is enabled
5. **Allure reports show each row separately** with their own parameters, screenshots and results

---

## 📌 Important Details
- 5 data rows = 5 separate scenarios executed
- Background runs before every single row
- Hooks (@Before / @After) run before/after every row
- If one dataset fails, others will still continue running
- You can have unlimited rows in Examples table
- You can add as many columns as you need for parameters

---

## ❌ Do NOT do these
- ❌ Do not loop inside step definitions
- ❌ Do not use static variables to pass data between runs
- ❌ Do not share page objects across runs
- ❌ Do not try to implement your own data provider - Cucumber Scenario Outline is already optimized for this framework

---

## 📊 What happens when you run:
```
✅ Browser 1 opened → Run dataset 1 → Close browser
✅ Browser 2 opened → Run dataset 2 → Close browser
✅ Browser 3 opened → Run dataset 3 → Close browser
✅ Browser 4 opened → Run dataset 4 → Close browser
✅ Browser 5 opened → Run dataset 5 → Close browser
```

All run completely isolated, no cross contamination, no thread safety issues. This works perfectly with this framework's ThreadLocal implementation.