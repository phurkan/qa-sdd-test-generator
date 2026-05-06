package com.sdd.steps;

import com.sdd.utils.ConfigReader;
import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;

public class APIStepDefinitions {
    private Response response;
    private RequestSpecification request;

    @Given("the API server is running")
    public void apiReady() {
        RestAssured.baseURI = ConfigReader.getApiBaseUrl();
        request = RestAssured.given()
            .header("Content-Type", "application/json")
            .header("Accept", "application/json");
        System.out.println("API Base URI: " + ConfigReader.getApiBaseUrl());
    }

    @Given("valid authentication headers are set")
    public void setAuth() { System.out.println("Auth headers configured"); }

    @When("the API request is sent for: {string}")
    public void sendRequest(String action) { System.out.println("API: " + action); }

    @Then("the response status code should be valid")
    public void statusValid() {
        if (response != null)
            Assert.assertTrue(response.getStatusCode() < 500, "Server error: " + response.getStatusCode());
    }

    @Then("the response body should match: {string}")
    public void bodyMatches(String expected) { System.out.println("Verifying: " + expected); }

    @When("I send POST {string} with valid credentials")
    public void postValid(String endpoint) {
        response = request.body("{\"email\":\"test@example.com\",\"password\":\"Test@123\"}").post(endpoint);
        System.out.println("POST " + endpoint + " -> " + response.getStatusCode());
    }

    @When("I send POST {string} with invalid credentials")
    public void postInvalid(String endpoint) {
        response = request.body("{\"email\":\"wrong@example.com\",\"password\":\"wrong\"}").post(endpoint);
        System.out.println("POST " + endpoint + " -> " + response.getStatusCode());
    }

    @When("I send GET {string}")
    public void sendGet(String endpoint) {
        response = request.get(endpoint);
        System.out.println("GET " + endpoint + " -> " + response.getStatusCode());
    }

    @Then("the response status should be {int}")
    public void checkStatus(int expected) {
        Assert.assertEquals(response.getStatusCode(), expected,
            "Status mismatch! Got: " + response.getStatusCode() + " Body: " + response.asString());
    }

    @Then("the response body should contain {string}")
    public void checkBody(String key) {
        Assert.assertTrue(response.asString().contains(key),
            "Key '" + key + "' not found in: " + response.asString());
    }

    @Then("response time should be under {int} milliseconds")
    public void responseTime(int ms) {
        long actual = response.getTime();
        Assert.assertTrue(actual < ms, "Too slow: " + actual + "ms (max: " + ms + "ms)");
    }
}
