package com.framework.utils;

import com.microsoft.playwright.Page;
import io.qameta.allure.Allure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;

/**
 * Takes a screenshot via Playwright and attaches it to the Allure report.
 * Called from hooks on scenario failure (always) or on demand.
 */
public class ScreenshotUtil {

    private static final Logger log = LoggerFactory.getLogger(ScreenshotUtil.class);

    private ScreenshotUtil() {}

    /**
     * Capture a full-page screenshot and attach to Allure.
     * @param name label shown in the Allure report
     */
    public static void attachToAllure(String name) {
        Page page = BrowserManager.getPage();
        if (page == null) {
            log.warn("No active page — skipping screenshot '{}'", name);
            return;
        }
        try {
            byte[] bytes = page.screenshot(
                    new Page.ScreenshotOptions().setFullPage(true));
            Allure.addAttachment(name, "image/png", new ByteArrayInputStream(bytes), "png");
            log.debug("Screenshot attached: {}", name);
        } catch (Exception e) {
            log.warn("Screenshot failed for '{}': {}", name, e.getMessage());
        }
    }
}
