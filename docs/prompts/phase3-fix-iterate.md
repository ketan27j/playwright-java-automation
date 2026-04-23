# Prompt: Phase 3 — Fix & Iterate

## When to use
After running the generated tests and getting failures.
Paste the Allure/Gradle failure output into {{ERROR_OUTPUT}}.

---

## Prompt Template — Fix Selector Failures

```
You are a test automation engineer debugging a failing Playwright test.

## Framework Rules
[PASTE framework-context.md HERE]

## Failing Test Output
{{PASTE GRADLE/ALLURE ERROR OUTPUT HERE}}

## Current Page Object
{{PASTE THE FAILING PAGE OBJECT JAVA FILE HERE}}

## Task
1. Use `playwright_navigate` to open: {{PAGE_URL}}
2. Use `playwright_get_snapshot` to inspect the current DOM
3. Find the correct selectors for the elements that are failing
4. Output ONLY the corrected Page Object file with fixed selectors
5. Explain what changed and why each selector was wrong

Do not change any logic — only fix selectors.
```

---

## Prompt Template — Add New Scenario

```
You are a test automation engineer extending an existing test suite.

## Framework Rules
[PASTE framework-context.md HERE]

## Existing Feature File
{{PASTE EXISTING .feature FILE HERE}}

## Existing Page Object
{{PASTE EXISTING PAGE OBJECT HERE}}

## Existing Steps
{{PASTE EXISTING STEPS FILE HERE}}

## New scenario to add
{{DESCRIBE THE NEW SCENARIO IN PLAIN ENGLISH}}
Example: "A logged-in user tries to change their email to one already in use — should show an error"

## Task
1. Use playwright_navigate to explore any new page states needed
2. Add the new Scenario to the feature file
3. Add any new methods to the Page Object (keep existing methods unchanged)
4. Add new step definitions (keep existing steps unchanged)

Output only the CHANGED sections, clearly marked with file path.
```

---

## Prompt Template — Increase Coverage

```
You are a test automation engineer improving test coverage.

## Framework Rules
[PASTE framework-context.md HERE]

## Current Feature File
{{PASTE FEATURE FILE HERE}}

## Task
1. Use playwright_navigate to open: {{PAGE_URL}}
2. Explore the page looking for:
   - Untested user interactions
   - Edge cases (empty states, max length inputs, special characters)
   - Accessibility interactions (keyboard navigation, tab order)
   - Responsive/state changes (loading states, disabled states)
3. Suggest 3-5 additional scenarios worth automating
4. For each suggestion explain: what it tests, why it has value, rough priority (P1/P2/P3)
5. Generate the Gherkin for each suggested scenario

Do not generate Java code yet — scenarios only.
```
