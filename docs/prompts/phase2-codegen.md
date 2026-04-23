# Prompt: Phase 2 — Code Generation

## When to use
After Phase 1 has produced the selector inventory.
Paste the Phase 1 output into {{SELECTOR_INVENTORY}} below.

---

## Prompt Template

```
You are a test automation engineer. Generate production-ready test code for our framework.

## Framework Rules
Read and follow these EXACTLY — do not deviate:

[PASTE CONTENTS OF framework-context.md HERE]

---

## Selector Inventory (from Phase 1 exploration)
{{PASTE PHASE 1 OUTPUT HERE}}

---

## What to Generate

Generate ALL of the following files. Output each file as a separate code block
with the full file path as the title.

### 1. Feature File
Path: src/test/resources/features/{{feature_name}}.feature

Requirements:
- Cover the happy path scenario
- Cover all validation/error scenarios discovered
- Use Scenario Outline for any data-driven cases
- Tag with @{{feature_name}} @parallel
- Tag happy path with @smoke
- Tag all others with @regression
- Write from user perspective, not implementation

### 2. Page Object
Path: src/test/java/com/framework/pages/{{FeatureName}}Page.java

Requirements:
- Extend BasePage
- All selectors as private static final String at top of class
- All public methods annotated with @Step
- Fluent return (return this) where appropriate
- Include an open() method that navigates to the page URL
- Cover every interaction in the feature file

### 3. Step Definitions
Path: src/test/java/com/framework/steps/{{FeatureName}}Steps.java

Requirements:
- Page object instantiated in constructor (private final field)
- Step text matches feature file EXACTLY — copy-paste match
- No logic in steps — delegate everything to page object methods
- Include all steps referenced in the feature file

---

## Output format
For each file output:

**`src/test/resources/features/login.feature`**
```gherkin
<content>
```

**`src/test/java/com/framework/pages/LoginPage.java`**
```java
<content>
```

**`src/test/java/com/framework/steps/LoginSteps.java`**
```java
<content>
```

Generate all three files now.
```

---

## Tips
- If output is cut off due to context length, follow up with: "Continue from where you stopped — output the remaining files"
- If selectors look wrong after running tests, go back to Phase 1 and re-explore
- For complex multi-page flows (e.g. checkout), split into one Page Object per page
