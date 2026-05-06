package com.sdd.pages;

import com.sdd.utils.ConfigReader;
import org.openqa.selenium.By;

public class LoginPage extends BasePage {
    // TODO: Update these locators to match your application
    private final By emailField    = By.id("email");
    private final By passwordField = By.id("password");
    private final By loginButton   = By.id("login-btn");
    private final By errorMessage  = By.cssSelector(".error-message");

    public void open()                             { navigate(ConfigReader.getAppBaseUrl() + "/login"); }
    public void login(String email, String pass)   { type(emailField, email); type(passwordField, pass); click(loginButton); }
    public boolean isLoginButtonVisible()          { return isDisplayed(loginButton); }
    public String  getErrorMessage()               { return getText(errorMessage); }
    public boolean isOnDashboard()                 { return currentUrl().contains("/dashboard"); }
}
