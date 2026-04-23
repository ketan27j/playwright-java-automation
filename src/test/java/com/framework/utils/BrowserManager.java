package com.framework.utils;

import com.microsoft.playwright.*;
import com.framework.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

/**
 * Thread-safe browser lifecycle manager using ThreadLocal.
 *
 * Each parallel Cucumber scenario thread gets its own:
 *   Playwright instance → Browser → BrowserContext → Page
 *
 * Call init() in @Before hook, quit() in @After hook.
 */
public class BrowserManager {

    private static final Logger log = LoggerFactory.getLogger(BrowserManager.class);

    private static final ThreadLocal<Playwright>      playwright = new ThreadLocal<>();
    private static final ThreadLocal<Browser>         browser    = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext>  context    = new ThreadLocal<>();
    private static final ThreadLocal<Page>            page       = new ThreadLocal<>();

    private BrowserManager() {}

    public static void init() {
        log.info("[{}] Initialising browser: {} headless={}",
                Thread.currentThread().getName(),
                ConfigManager.browser(),
                ConfigManager.headless());

        Playwright pw = Playwright.create();
        playwright.set(pw);

        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(ConfigManager.headless());

        Browser b = switch (ConfigManager.browser().toLowerCase()) {
            case "firefox"  -> pw.firefox().launch(launchOptions);
            case "webkit"   -> pw.webkit().launch(launchOptions);
            default         -> pw.chromium().launch(launchOptions);
        };
        browser.set(b);

        // Context-level settings (viewport, locale, etc.)
        Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
                .setViewportSize(1440, 900)
                .setLocale("en-US");

        BrowserContext ctx = b.newContext(contextOptions);

        // Start tracing if enabled in config
        if (ConfigManager.tracing()) {
            ctx.tracing().start(new Tracing.StartOptions()
                    .setScreenshots(true)
                    .setSnapshots(true)
                    .setSources(true));
            log.debug("[{}] Playwright tracing started", Thread.currentThread().getName());
        }

        context.set(ctx);
        page.set(ctx.newPage());

        // Default navigation timeout
        page.get().setDefaultNavigationTimeout(ConfigManager.timeout());
        page.get().setDefaultTimeout(ConfigManager.timeout());
    }

    public static Page getPage()            { return page.get(); }
    public static BrowserContext getContext(){ return context.get(); }

    /**
     * Save tracing zip and clean up all resources for this thread.
     * @param scenarioName used as the trace file name
     * @param failed       if true the trace is always saved; if false only when tracing=true
     */
    public static void quit(String scenarioName, boolean failed) {
        try {
            BrowserContext ctx = context.get();
            if (ctx != null && ConfigManager.tracing()) {
                String safeName = scenarioName.replaceAll("[^a-zA-Z0-9_\\-]", "_");
                String tracePath = "build/traces/" + safeName + "-" + Thread.currentThread().getId() + ".zip";
                ctx.tracing().stop(new Tracing.StopOptions().setPath(Paths.get(tracePath)));
                log.info("[{}] Trace saved → {}", Thread.currentThread().getName(), tracePath);
            }
        } catch (Exception e) {
            log.warn("Failed to stop tracing: {}", e.getMessage());
        } finally {
            closeSilently(page.get());
            closeSilently(context.get());
            closeSilently(browser.get());
            closeSilently(playwright.get());
            page.remove();
            context.remove();
            browser.remove();
            playwright.remove();
            log.info("[{}] Browser closed", Thread.currentThread().getName());
        }
    }

    private static void closeSilently(AutoCloseable resource) {
        if (resource != null) {
            try { resource.close(); } catch (Exception ignored) {}
        }
    }
}
