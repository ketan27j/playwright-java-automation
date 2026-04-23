# Prompt: Phase 1 — Page Exploration & Selector Discovery

## When to use
Run this FIRST for each page/feature you want to test.
It instructs Qwen3 to navigate the real site and extract accurate selectors before any code is written.

---

## Prompt Template

```
You are a test automation engineer working with a Java 21 + Playwright + Cucumber framework.

## Task
Explore the following page and extract everything needed to write automated tests.

## Target
URL: {{PAGE_URL}}
Flow to explore: {{DESCRIBE_THE_FLOW}}
Example: "the user registration flow — from clicking Sign Up to successful account creation"

## Instructions
1. Use the `playwright_navigate` tool to open the URL
2. Use `playwright_screenshot` to capture the initial state
3. Use `playwright_get_snapshot` to get the full DOM/accessibility snapshot
4. Interact with the page to explore the flow: click buttons, fill forms, navigate between steps
5. After each significant interaction, use `playwright_get_snapshot` again
6. Take note of:
   - All interactive elements (inputs, buttons, dropdowns, checkboxes)
   - Their best selector (prefer data-testid > aria-label > role > text > css)
   - Page URLs at each step
   - Any validation messages or error states you can trigger
   - Success/confirmation states

## Output Format
Produce a structured selector inventory — NOT code yet:

### Page: {{Page Name}}
URL: <url>

#### Selectors
| Element | Selector | Type | Notes |
|---------|----------|------|-------|
| Username input | [data-testid='username'] | input | Required field |
| Submit button | role=button[name='Sign In'] | button | Disabled until form valid |
| Error message | .error-banner | div | Appears on failed login |

#### User Flows Discovered
1. Happy path: <describe steps>
2. Validation errors: <describe what triggers them>
3. Edge cases noticed: <anything unusual>

#### Page URLs
- Entry: /login
- On success: /dashboard
- On error: stays on /login

Do NOT generate Java code yet. Only produce the selector inventory and flow description.
```

---

## Usage Tips
- Run this once per page or feature area
- Save the output — you'll paste it into Phase 2
- If the page requires login first, add to the prompt:
  "First navigate to /login and authenticate with username='admin' password='pass123' before exploring the target page"
