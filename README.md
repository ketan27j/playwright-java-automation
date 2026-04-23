# Playwright + Cucumber + Java 21 Test Framework

A production-ready test automation framework using **Playwright**, **Cucumber-JVM**, **JUnit 5**, and **Allure Reports** with full parallel execution support.

---

## Tech Stack

| Tool | Version | Purpose |
|------|---------|---------|
| Java | 21 | Language |
| Gradle | 8+ | Build tool |
| Playwright | 1.44 | Browser automation |
| Cucumber-JVM | 7.18 | BDD / Gherkin |
| JUnit Platform | 5.10 | Test runner |
| Allure | 2.27 | Reporting |
| Logback | 1.5 | Logging |
| Typesafe Config | 1.4 | Environment config |

---

## Project Structure

```
src/test/
├── java/com/framework/
│   ├── TestRunner.java          # JUnit Platform suite entry point
│   ├── config/
│   │   └── ConfigManager.java   # Loads env-specific config + system props
│   ├── hooks/
│   │   └── Hooks.java           # Before/After — browser lifecycle, screenshots
│   ├── pages/
│   │   ├── BasePage.java        # Common Playwright helpers, Allure @Step wrappers
│   │   └── LoginPage.java       # Example Page Object
│   ├── steps/
│   │   └── LoginSteps.java      # Example Cucumber step definitions
│   └── utils/
│       ├── BrowserManager.java  # ThreadLocal browser — parallel-safe
│       └── ScreenshotUtil.java  # Screenshot → Allure attachment
└── resources/
    ├── features/
    │   └── login.feature        # Example feature file
    ├── config/
    │   ├── application.conf     # Default config
    │   ├── staging.conf         # Staging overrides
    │   └── prod.conf            # Prod overrides
    ├── allure.properties        # Allure output dir + link patterns
    ├── cucumber.properties      # Parallel execution settings
    └── logback-test.xml         # Logging configuration
```

---

## Quick Start

### 1. Prerequisites

- Java 21+
- Gradle 8+ (wrapper included)
- Node.js 18+ (for Playwright CLI)

### 2. Platform Setup

#### Linux / macOS

```bash
# Clone and navigate to project
git clone <repo-url>
cd playwright-java-automation

# Make gradlew executable
chmod +x gradlew

# Download dependencies and install browsers
./gradlew dependencies
npx playwright install chromium
```

#### Windows

```powershell
# Clone and navigate to project
git clone <repo-url>
cd playwright-java-automation

# Use gradlew.bat (included)
.\gradlew.bat dependencies
npx playwright install chromium
```

### 3. Run tests

```bash
# Default (staging, headless)
./gradlew test

# With custom URL
./gradlew test -Dbase.url=https://www.saucedemo.com

# See browser in action
./gradlew test -Dheadless=false

# Run only @smoke tagged scenarios
./gradlew test -Dcucumber.filter.tags="@smoke"

# Full pipeline: clean → test → allure report
./gradlew runTests
```

### 4. View Allure report

```bash
# Generate and open report
./gradlew allureReport
npx allure open build/reports/allure-report

# Or serve live
npx allure serve build/allure-results
```

### 5. View Playwright traces

Traces are saved to `build/traces/` as `.zip` files when `playwright.tracing=true`.

Open them in the Playwright Trace Viewer:

```bash
# Via npx (no install needed)
npx playwright show-trace build/traces/your-scenario-name.zip

# Or open https://trace.playwright.dev and drag-drop the zip
```

---

## Parallel Execution

Parallelism is configured at two levels:

**1. `cucumber.properties`** — controls scenario-level parallelism:
```properties
cucumber.execution.parallel.enabled=true
cucumber.execution.parallel.config.strategy=fixed
cucumber.execution.parallel.config.fixed.parallelism=4
```

**2. `build.gradle`** — passes runtime parallelism override:
```bash
./gradlew test -Dparallelism=8
```

**Thread safety** is guaranteed by `BrowserManager` which uses `ThreadLocal<Playwright>`, `ThreadLocal<Browser>`, `ThreadLocal<BrowserContext>`, and `ThreadLocal<Page>`. Each scenario thread gets completely isolated browser state — no shared objects.

> ⚠️ Never use static `Page` or `BrowserContext` fields in page objects. Always call `BrowserManager.getPage()`.

---

## Adding a New Page Object

```java
public class CheckoutPage extends BasePage {

    private static final String CONTINUE_BTN = "[data-testid='continue']";
    private static final String TOTAL_LABEL  = ".summary_total_label";

    @Step("Click continue to payment")
    public void clickContinue() {
        click(CONTINUE_BTN);
    }

    @Step("Assert total is {expected}")
    public void assertTotal(String expected) {
        assertContainsText(TOTAL_LABEL, expected);
    }
}
```

---

## Adding a New Feature

1. Create `src/test/resources/features/checkout.feature`
2. Create `src/test/java/com/framework/steps/CheckoutSteps.java`
3. Add `@parallel` tag to run in parallel
4. Run: `./gradlew test -Dcucumber.filter.tags="@checkout"`

---

## Configuration Reference

| System Property | Default | Description |
|----------------|---------|-------------|
| `env` | `staging` | Config file to load (`staging.conf`, `prod.conf`) |
| `browser` | `chromium` | `chromium` / `firefox` / `webkit` |
| `headless` | `true` | `true` / `false` |
| `parallelism` | CPU count | Number of parallel scenario threads |
| `base.url` | from conf | Override base URL directly |

---

## Reporting

### Allure Report features
- Gherkin steps with pass/fail per step
- Screenshot on failure attached inline
- Playwright trace zip linked per scenario
- Thread / browser / environment labels
- History trending across runs (when `allure-results` is preserved in CI)

### Log files
- Console: colored output during run
- File: `build/logs/test.log` (rolling, 7-day retention)

---

## CI/CD (GitHub Actions example)

```yaml
name: E2E Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'

      - name: Install Playwright browsers
        run: ./gradlew dependencies && npx playwright install chromium

      - name: Run tests
        run: ./gradlew test -Dparallelism=4

      - name: Upload Allure results
        uses: actions/upload-artifact@v4
        if: always()
        with:
          name: allure-results
          path: build/allure-results

      - name: Upload traces (on failure)
        uses: actions/upload-artifact@v4
        if: failure()
        with:
          name: playwright-traces
          path: build/traces/
```

---

## Troubleshooting

**Browser not found**
```bash
npx playwright install chromium
```

**Parallel scenarios interfering**
- Ensure no `static` Page/Context fields in page objects or step definitions
- Check that `BrowserManager.getPage()` is always called (never cache the return value in a field)

**Allure report empty**
```bash
ls build/allure-results/
./gradlew allureReport
```
