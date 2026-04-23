package com.framework;

import org.junit.platform.suite.api.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = "cucumber.plugin", value =
        "pretty," +
        "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm," +
        "html:build/reports/cucumber/index.html," +
        "json:build/reports/cucumber/cucumber.json")
@ConfigurationParameter(key = "cucumber.publish.quiet", value = "true")
@ConfigurationParameter(key = "cucumber.execution.parallel.enabled", value = "true")
public class TestRunner {
}
