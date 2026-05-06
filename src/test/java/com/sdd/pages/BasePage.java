package com.sdd.pages;

import com.sdd.utils.DriverFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage() {
        this.driver = DriverFactory.getDriver();
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    protected WebElement find(By by)       { return wait.until(ExpectedConditions.visibilityOfElementLocated(by)); }
    protected void click(By by)            { wait.until(ExpectedConditions.elementToBeClickable(by)).click(); }
    protected void type(By by, String txt) { WebElement e = find(by); e.clear(); e.sendKeys(txt); }
    protected String getText(By by)        { return find(by).getText(); }
    protected void navigate(String url)    { driver.get(url); }
    protected String currentUrl()         { return driver.getCurrentUrl(); }
    protected boolean isDisplayed(By by)  {
        try { return find(by).isDisplayed(); } catch (Exception e) { return false; }
    }
}
