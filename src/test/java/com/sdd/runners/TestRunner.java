package com.sdd.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

@CucumberOptions(
    features   = "src/test/resources/features",
    glue       = {"com.sdd.steps", "com.sdd.hooks"},
    plugin     = {
        "pretty",
        "html:output/cucumber-report.html",
        "json:output/cucumber-report.json",
        "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
    },
    monochrome = true
)
public class TestRunner extends AbstractTestNGCucumberTests {
    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() { return super.scenarios(); }
}
