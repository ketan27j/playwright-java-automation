package com.framework.hooks;

import com.framework.utils.BrowserManager;
import com.framework.utils.ScreenshotUtil;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Hooks {

    private static final Logger log = LoggerFactory.getLogger(Hooks.class);

    /**
     * Runs before every scenario — spins up a fresh browser context per thread.
     */
    @Before(order = 0)
    public void beforeScenario(Scenario scenario) {
        log.info("▶ START  [{}] | thread={}", scenario.getName(), Thread.currentThread().getName());
        Allure.label("thread", Thread.currentThread().getName());
        BrowserManager.init();
    }

    /**
     * Runs after every STEP.
     * Screenshot attached only on step failure — avoids report bloat.
     * For "screenshot every step" mode, remove the isFailed() guard.
     */
    @AfterStep
    public void afterStep(Scenario scenario) {
        if (scenario.isFailed()) {
            ScreenshotUtil.attachToAllure("Step failed — " + scenario.getName());
        }
    }

    /**
     * Runs after every scenario — attaches final screenshot on failure,
     * stops tracing, closes browser.
     */
    @After(order = 0)
    public void afterScenario(Scenario scenario) {
        boolean failed = scenario.isFailed();

        if (failed) {
            log.error("✖ FAIL   [{}]", scenario.getName());
            ScreenshotUtil.attachToAllure("Final state on failure");
        } else {
            log.info("✔ PASS   [{}]", scenario.getName());
        }

        BrowserManager.quit(scenario.getName(), failed);
    }
}
