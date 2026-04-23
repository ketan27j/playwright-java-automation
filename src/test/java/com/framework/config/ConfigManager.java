package com.framework.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Loads environment-specific config from resources/config/<env>.conf,
 * falling back to application.conf defaults.
 *
 * Usage:  ConfigManager.get().getString("base.url")
 *
 * Override at runtime:  -Denv=prod -Dbrowser=firefox -Dheadless=false
 */
public class ConfigManager {

    private static final Logger log = LoggerFactory.getLogger(ConfigManager.class);
    private static final Config CONFIG;

    static {
        String env      = System.getProperty("env", "staging");
        String browser  = System.getProperty("browser", "chromium");
        String headless = System.getProperty("headless", "true");

        log.info("Loading config  env={} browser={} headless={}", env, browser, headless);

        Config base = ConfigFactory.load("config/application");

        // Try to load env-specific override file
        File envFile = new File("src/test/resources/config/" + env + ".conf");
        Config envOverride = envFile.exists()
                ? ConfigFactory.parseFile(envFile).withFallback(base)
                : base;

        // System property overrides win over everything
        CONFIG = ConfigFactory.systemProperties()
                .withFallback(envOverride)
                .resolve();
    }

    private ConfigManager() {}

    public static Config get() {
        return CONFIG;
    }

    public static String baseUrl()  { return CONFIG.getString("base.url"); }
    public static String browser()  { return CONFIG.getString("browser"); }
    public static boolean headless(){ return CONFIG.getBoolean("headless"); }
    public static int timeout()     { return CONFIG.getInt("timeout.default"); }
    public static boolean tracing() { return CONFIG.getBoolean("playwright.tracing"); }
}
