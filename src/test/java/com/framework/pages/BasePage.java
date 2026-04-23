package com.framework.pages;

import com.framework.utils.BrowserManager;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for all Page Objects.
 * Provides thread-safe page access and common action wrappers
 * that auto-log and appear as Allure steps.
 */
public abstract class BasePage {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected Page page() {
        return BrowserManager.getPage();
    }

    @Step("Navigate to {url}")
    protected void navigateTo(String url) {
        log.info("Navigate → {}", url);
        page().navigate(url);
        page().waitForLoadState();
    }

    @Step("Click element: {selector}")
    protected void click(String selector) {
        log.debug("Click ← {}", selector);
        page().locator(selector).click();
    }

    @Step("Fill '{selector}' with value")
    protected void fill(String selector, String value) {
        log.debug("Fill ← {} = {}", selector, value);
        page().locator(selector).fill(value);
    }

    @Step("Get text of '{selector}'")
    protected String getText(String selector) {
        return page().locator(selector).innerText();
    }

    @Step("Assert element visible: {selector}")
    protected void assertVisible(String selector) {
        Locator locator = page().locator(selector);
        locator.waitFor(new Locator.WaitForOptions()
                .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE));
    }

    @Step("Assert element contains text: {expected}")
    protected void assertContainsText(String selector, String expected) {
        String actual = getText(selector);
        if (!actual.contains(expected)) {
            throw new AssertionError(
                    "Expected element '%s' to contain '%s' but got '%s'".formatted(selector, expected, actual));
        }
    }
}
