package com.sdd.hooks;

import com.sdd.utils.DriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class Hooks {

    @Before
    public void setUp(Scenario scenario) {
        System.out.println("\n>>> STARTING: " + scenario.getName());
        System.out.println("    Tags: " + scenario.getSourceTagNames());
    }

    @After
    public void tearDown(Scenario scenario) {
        try {
            WebDriver driver = DriverFactory.getDriver();
            if (scenario.isFailed()) {
                byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                scenario.attach(screenshot, "image/png", "FAILURE_SCREENSHOT");
                System.out.println("Screenshot captured for: " + scenario.getName());
            }
        } catch (Exception ignored) {}
        finally {
            DriverFactory.quitDriver();
        }
        System.out.println("<<< DONE: " + scenario.getName() +
            " [" + (scenario.isFailed() ? "FAILED" : "PASSED") + "]\n");
    }
}
