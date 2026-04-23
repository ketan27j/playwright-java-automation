# AGENTS.md

## Must-Know Commands

```bash
# Run all tests (uses default staging env, chromium, headless)
./gradlew test

# Run with specific env/browser/parallelism
./gradlew test -Denv=prod -Dbrowser=firefox -Dparallelism=4

# Run only @smoke tagged scenarios
./gradlew test -Dcucumber.filter.tags="@smoke"

# Run in headed mode (see browser)
./gradlew test -Dheadless=false

# Full pipeline: clean → test → allure report
./gradlew runTests

# Generate Allure report
./gradlew allureReport
allure open build/reports/allure-report

# View Playwright trace
npx playwright show-trace build/traces/<scenario-name>.zip
```

## Browser Installation (Required Before First Run)

```bash
./gradlew dependencies
npx playwright install chromium
```

## CI Parallel Strategy

Split by tags across jobs:

```bash
# Job 1: Smoke tests
./gradlew test -Dcucumber.filter.tags="@smoke"

# Job 2: Regression tests
./gradlew test -Dcucumber.filter.tags="@regression"
```

## Thread Safety Rule

**Never use static `Page` or `BrowserContext` fields in page objects.** Always use:

```java
Page page = BrowserManager.getPage();  // ThreadLocal - per-scenario isolate
```

Static fields cause test interference in parallel execution.

## Environment Config

| Property | Default | Config File |
|----------|---------|-------------|
| `-Denv` | `staging` | `src/test/resources/config/{env}.conf` |

Valid envs: `staging`, `prod`. Base URL loaded from config file.

## Key Files

- **Entry point:** `src/test/java/com/framework/TestRunner.java`
- **Config:** `src/test/resources/config/application.conf` (+ env overrides)
- **Parallelism:** `cucumber.properties` (scenario-level), `-Dparallelism=N` (runtime override)
- **Allure results:** `build/allure-results/`
- **Traces:** `build/traces/` (zip files, enable with `playwright.tracing=true` in config)

## CI Reference

GitHub Actions workflow in README uses:
1. `actions/setup-java` with Java 21
2. Playwright CLI install with `--with-deps`
3. `./gradlew test -Dparallelism=4`