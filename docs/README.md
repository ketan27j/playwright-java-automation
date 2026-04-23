# Qwen3-Coder + Playwright MCP → Test Framework Workflow

Generate accurate, drop-in test code for your Java 21 + Playwright + Cucumber framework
by combining Qwen3-Coder (local Ollama) with Playwright MCP in OpenCode.

---

## How it works

```
OpenCode
  └── Qwen3-Coder (Ollama)
        └── Playwright MCP tools
              └── Real browser (Chromium)
                    └── Your website
                          ↓
              Accurate selectors + real DOM
                          ↓
              Java code matching your framework
```

Qwen3 doesn't guess at selectors — it navigates your actual site and reads the DOM
before writing a single line of code.

---

## Prerequisites

```bash
# 1. Ollama running with Qwen3-Coder
ollama serve
ollama run qwen3-coder-next   # confirm it loads

# 2. Playwright MCP available
npm install -g @playwright/mcp

# 3. OpenCode installed
# https://opencode.ai

# 4. Copy opencode.json to your project root (or ~/.opencode/config.json globally)
```

---

## Setup

```bash
# Copy opencode.json to your framework project root
cp opencode.json /path/to/playwright-cucumber-framework/

# Start OpenCode from your framework project directory
cd /path/to/playwright-cucumber-framework
opencode
```

OpenCode will automatically start the Playwright MCP server as a subprocess
when you begin a session.

---

## The 3-Phase Workflow

### Phase 1 — Explore (2-5 min per page)

Open `prompts/phase1-exploration.md`, fill in:
- `{{PAGE_URL}}` — the page to explore
- `{{DESCRIBE_THE_FLOW}}` — what journey to trace

Paste the filled prompt into OpenCode. Qwen3 will:
- Navigate to the page
- Take screenshots
- Inspect the DOM
- Produce a selector inventory

**Save the output** — you need it for Phase 2.

---

### Phase 2 — Generate (1-2 min)

Open `prompts/phase2-codegen.md`, fill in:
- The framework context (paste `context/framework-context.md`)
- The Phase 1 selector inventory output
- File name placeholders (`{{feature_name}}`, `{{FeatureName}}`)

Paste into OpenCode. Qwen3 will produce:
- `*.feature` file (Gherkin)
- `*Page.java` (Page Object)
- `*Steps.java` (Step definitions)

Copy each file into your framework at the stated paths.

---

### Phase 3 — Fix & Iterate

Run your tests:
```bash
./gradlew test -Dcucumber.filter.tags="@yourfeature"
```

If tests fail, use `prompts/phase3-fix-iterate.md` to:
- Fix selector mismatches (most common issue)
- Add new scenarios
- Improve coverage

---

## Tips for Qwen3-Coder (local model)

**Context window is limited** — keep prompts focused:
- One page per session, not the whole app
- Paste framework-context.md once at the start of a session, then reference it
- If output gets cut off: `"Continue the output from the Steps file"`

**Better selector results:**
- Tell it which selectors to prefer: `"prefer data-testid over CSS classes"`
- If the site uses React/Angular, tell it: `"this is a React app, aria attributes are reliable"`
- For SPAs: `"wait for network idle before taking snapshots"`

**Multi-page flows (e.g. checkout):**
- Run Phase 1 on each page separately
- Generate one Page Object per page
- Generate one combined Steps file for the whole flow

**Authenticated pages:**
- Add to Phase 1 prompt: `"First login at /login with username='x' password='y'"`
- Or ask Qwen3 to generate a `LoginHelper.java` utility class for reuse

---

## Example Session

```
You → OpenCode:
  [Phase 1 prompt with URL: https://myapp.com/checkout]

Qwen3 → calls playwright_navigate("https://myapp.com/checkout")
Qwen3 → calls playwright_screenshot()
Qwen3 → calls playwright_get_snapshot()
Qwen3 → calls playwright_click("[data-testid='add-to-cart']")
Qwen3 → calls playwright_get_snapshot()
Qwen3 → produces selector inventory

You → OpenCode:
  [Phase 2 prompt with framework context + Phase 1 output]

Qwen3 → produces checkout.feature + CheckoutPage.java + CheckoutSteps.java

You → copy files into framework
You → ./gradlew test -Dcucumber.filter.tags="@checkout"
You → 2 tests fail (selector changed)

You → OpenCode:
  [Phase 3 fix prompt with error output]

Qwen3 → navigates site, finds correct selectors, outputs fixed CheckoutPage.java
You → copy fixed file, re-run → all pass ✅
```

---

## File Reference

```
opencode-playwright-setup/
├── opencode.json                      # OpenCode config — copy to project root
├── context/
│   └── framework-context.md           # Framework rules — paste into every Phase 2/3 prompt
└── prompts/
    ├── phase1-exploration.md          # Selector discovery prompt
    ├── phase2-codegen.md              # Code generation prompt
    └── phase3-fix-iterate.md          # Fix + extend prompts
```
