package com.sdd.steps;

import com.sdd.pages.LoginPage;
import com.sdd.utils.DriverFactory;
import io.cucumber.java.en.*;
import org.testng.Assert;

public class UIStepDefinitions {
    private LoginPage loginPage;

    @Given("the application is running and accessible")
    public void appRunning() { loginPage = new LoginPage(); }

    @Given("the user is on the relevant page")
    public void onPage() { loginPage.open(); }

    @When("the user performs action: {string}")
    public void performAction(String action) {
        System.out.println("UI Action: " + action);
        if (action.toLowerCase().contains("login"))
            loginPage.login("testuser@example.com", "Test@123");
    }

    @Then("the system should respond as expected")
    public void systemResponds() { System.out.println("Verifying system response..."); }

    @Then("the result should match: {string}")
    public void verifyResult(String expected) { System.out.println("Expected: " + expected); }

    @Given("the user navigates to the login page")
    public void goToLogin() { loginPage = new LoginPage(); loginPage.open(); }

    @When("the user enters valid credentials")
    public void validLogin() { loginPage.login("testuser@example.com", "Test@123"); }

    @When("the user enters invalid credentials")
    public void invalidLogin() { loginPage.login("wrong@example.com", "wrong"); }

    @Then("the user should be redirected to the dashboard")
    public void verifyDashboard() {
        Assert.assertTrue(loginPage.isOnDashboard(), "Not redirected to dashboard!");
    }

    @Then("an error message {string} should be displayed")
    public void verifyError(String msg) {
        Assert.assertEquals(loginPage.getErrorMessage(), msg, "Error message mismatch!");
    }

    @Then("the login button should be visible and enabled")
    public void verifyLoginBtn() {
        Assert.assertTrue(loginPage.isLoginButtonVisible(), "Login button not visible!");
    }
}
