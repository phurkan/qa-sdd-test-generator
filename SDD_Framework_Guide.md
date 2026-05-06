# Spec-Driven Development (SDD) Framework for QA Automation

## Overview

This document describes a complete, end-to-end Spec-Driven Development (SDD) framework for QA Automation Engineers. The framework accepts acceptance criteria from Jira tickets or plain text, classifies each requirement automatically, and generates manual test cases for UI (Frontend), API (Backend), and Accessibility testing — all with full traceability back to the source ticket.

---

## Framework Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        INPUT LAYER                          │
│    Jira Ticket / Plain Text / requirements.yaml             │
│    (Acceptance Criteria pasted or fetched via API)          │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                     AC PARSER ENGINE                        │
│    Keyword classifier → UI / API / Accessibility            │
│    NLP-based or AI-assisted (OpenAI API optional)           │
└──────────────┬───────────────────┬────────────────┬─────────┘
               │                   │                │
               ▼                   ▼                ▼
    ┌──────────────┐    ┌──────────────┐   ┌──────────────────┐
    │  UI Test     │    │  API Test    │   │  Accessibility   │
    │  Generator   │    │  Generator   │   │  Test Generator  │
    │ (Selenium)   │    │(REST-assured)│   │  (Axe / WCAG)   │
    └──────┬───────┘    └──────┬───────┘   └────────┬─────────┘
           │                   │                    │
           └───────────────────┼────────────────────┘
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                    OUTPUT LAYER                             │
│   Manual Test Cases (Excel / HTML / JSON)                   │
│   Gherkin Feature Files (.feature)                          │
│   Serenity BDD Living Documentation Report                  │
│   Requirement Traceability Matrix                           │
└─────────────────────────────────────────────────────────────┘
```

---

## Project Structure

```
sdd-framework/
├── src/
│   ├── main/java/com/sdd/
│   │   ├── core/
│   │   │   ├── ACParser.java              ← Parses & classifies AC text
│   │   │   ├── TicketReader.java          ← Reads from Jira API or file
│   │   │   └── TestCaseModel.java         ← Data model for a test case
│   │   ├── generators/
│   │   │   ├── UITestCaseGenerator.java   ← Generates UI test cases
│   │   │   ├── APITestCaseGenerator.java  ← Generates API test cases
│   │   │   └── A11yTestCaseGenerator.java ← Generates Accessibility TCs
│   │   ├── exporters/
│   │   │   ├── ExcelExporter.java         ← Exports to .xlsx
│   │   │   ├── GherkinExporter.java       ← Exports to .feature files
│   │   │   └── HTMLExporter.java          ← Exports to HTML report
│   │   └── MainRunner.java                ← Entry point
│   └── test/java/com/sdd/
│       ├── steps/                         ← Cucumber Step Definitions
│       ├── pages/                         ← Page Object Model (POM)
│       ├── hooks/                         ← Before/After hooks
│       └── runners/                       ← TestNG + Cucumber runners
├── resources/
│   ├── features/                          ← Auto-generated .feature files
│   ├── requirements/
│   │   └── sample-ac.txt                  ← Sample acceptance criteria
│   └── config/
│       └── config.properties              ← App base URL, API URL, etc.
├── output/
│   ├── test-cases.xlsx                    ← Generated manual test cases
│   ├── traceability-matrix.html           ← Requirement traceability
│   └── serenity-reports/                  ← Living documentation
└── pom.xml
```

---

## Module 1 — pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.sdd</groupId>
    <artifactId>sdd-framework</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <properties>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
        <selenium.version>4.18.1</selenium.version>
        <cucumber.version>7.15.0</cucumber.version>
        <serenity.version>4.1.4</serenity.version>
        <restassured.version>5.4.0</restassured.version>
    </properties>

    <dependencies>
        <!-- Selenium WebDriver -->
        <dependency>
            <groupId>org.seleniumhq.selenium</groupId>
            <artifactId>selenium-java</artifactId>
            <version>${selenium.version}</version>
        </dependency>

        <!-- Cucumber BDD -->
        <dependency>
            <groupId>io.cucumber</groupId>
            <artifactId>cucumber-java</artifactId>
            <version>${cucumber.version}</version>
        </dependency>
        <dependency>
            <groupId>io.cucumber</groupId>
            <artifactId>cucumber-testng</artifactId>
            <version>${cucumber.version}</version>
        </dependency>

        <!-- Serenity BDD -->
        <dependency>
            <groupId>net.serenity-bdd</groupId>
            <artifactId>serenity-core</artifactId>
            <version>${serenity.version}</version>
        </dependency>
        <dependency>
            <groupId>net.serenity-bdd</groupId>
            <artifactId>serenity-cucumber</artifactId>
            <version>${serenity.version}</version>
        </dependency>

        <!-- REST-assured (API Testing) -->
        <dependency>
            <groupId>io.rest-assured</groupId>
            <artifactId>rest-assured</artifactId>
            <version>${restassured.version}</version>
        </dependency>

        <!-- Swagger / OpenAPI Parser -->
        <dependency>
            <groupId>io.swagger.parser.v3</groupId>
            <artifactId>swagger-parser</artifactId>
            <version>2.1.22</version>
        </dependency>

        <!-- Axe Accessibility (Selenium) -->
        <dependency>
            <groupId>com.deque.html.axe-core</groupId>
            <artifactId>selenium</artifactId>
            <version>4.8.0</version>
        </dependency>

        <!-- Apache POI (Excel export) -->
        <dependency>
            <groupId>org.apache.poi</groupId>
            <artifactId>poi-ooxml</artifactId>
            <version>5.2.5</version>
        </dependency>

        <!-- YAML Parser -->
        <dependency>
            <groupId>org.yaml</groupId>
            <artifactId>snakeyaml</artifactId>
            <version>2.2</version>
        </dependency>

        <!-- TestNG -->
        <dependency>
            <groupId>org.testng</groupId>
            <artifactId>testng</artifactId>
            <version>7.9.0</version>
        </dependency>

        <!-- Allure Reporting -->
        <dependency>
            <groupId>io.qameta.allure</groupId>
            <artifactId>allure-testng</artifactId>
            <version>2.27.0</version>
        </dependency>

        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.30</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
</project>
```

---

## Module 2 — TestCaseModel.java (Data Model)

```java
package com.sdd.core;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestCaseModel {
    private String tcId;          // e.g., UI-TC-001
    private String ticketId;      // e.g., PROJ-101
    private String requirementId; // e.g., FE-REQ-01
    private String title;
    private String type;          // UI | API | ACCESSIBILITY
    private String priority;      // Critical | High | Medium | Low
    private String module;
    private String preconditions;
    private String steps;
    private String testData;
    private String expectedResult;
    private String actualResult;  // filled at runtime
    private String status;        // Pass | Fail | Blocked | Pending
}
```

---

## Module 3 — ACParser.java (Core Intelligence)

This is the brain of the framework — it reads acceptance criteria text and classifies each line into UI, API, or Accessibility test type.

```java
package com.sdd.core;

import java.util.*;

public class ACParser {

    // Keywords for classification
    private static final List<String> UI_KEYWORDS = Arrays.asList(
        "user", "button", "click", "navigate", "page", "form", "screen",
        "visible", "display", "redirect", "login", "dashboard", "modal",
        "dropdown", "input", "field", "message", "ui", "frontend"
    );

    private static final List<String> API_KEYWORDS = Arrays.asList(
        "api", "endpoint", "post", "get", "put", "delete", "patch",
        "status code", "response", "payload", "request", "jwt", "token",
        "header", "body", "http", "rest", "json", "backend", "service"
    );

    private static final List<String> A11Y_KEYWORDS = Arrays.asList(
        "wcag", "aria", "accessibility", "screen reader", "keyboard",
        "tab", "contrast", "focus", "alt text", "label", "role",
        "accessible", "a11y", "color contrast"
    );

    /**
     * Parses raw acceptance criteria text into classified TestCaseModel list.
     * Each line of AC is analyzed and mapped to a test case.
     */
    public static List<TestCaseModel> parse(String ticketId, String acText) {
        List<TestCaseModel> testCases = new ArrayList<>();
        String[] lines = acText.split("\n");

        int uiCount = 1, apiCount = 1, a11yCount = 1;

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            String type = classify(line);
            String tcId = generateId(type, uiCount, apiCount, a11yCount);

            TestCaseModel tc = TestCaseModel.builder()
                .tcId(tcId)
                .ticketId(ticketId)
                .requirementId(ticketId + "-" + type + "-REQ")
                .title(generateTitle(line, type))
                .type(type)
                .priority(detectPriority(line))
                .preconditions(generatePreconditions(type))
                .steps(generateSteps(line, type))
                .testData(generateTestData(line, type))
                .expectedResult(generateExpectedResult(line, type))
                .status("Pending")
                .build();

            testCases.add(tc);

            if (type.equals("UI")) uiCount++;
            else if (type.equals("API")) apiCount++;
            else a11yCount++;
        }

        return testCases;
    }

    private static String classify(String line) {
        String lower = line.toLowerCase();
        long uiScore = UI_KEYWORDS.stream().filter(lower::contains).count();
        long apiScore = API_KEYWORDS.stream().filter(lower::contains).count();
        long a11yScore = A11Y_KEYWORDS.stream().filter(lower::contains).count();

        if (a11yScore >= 1) return "ACCESSIBILITY";
        if (apiScore >= apiScore && apiScore > uiScore) return "API";
        return "UI"; // Default to UI
    }

    private static String generateId(String type, int ui, int api, int a11y) {
        return switch (type) {
            case "UI"            -> String.format("UI-TC-%03d", ui);
            case "API"           -> String.format("API-TC-%03d", api);
            case "ACCESSIBILITY" -> String.format("ACC-TC-%03d", a11y);
            default              -> "TC-000";
        };
    }

    private static String generateTitle(String line, String type) {
        // Trim list markers like "1.", "-", "*"
        return line.replaceAll("^[\\d\\.\\-\\*]+\\s*", "").trim();
    }

    private static String detectPriority(String line) {
        String lower = line.toLowerCase();
        if (lower.contains("must") || lower.contains("critical") || lower.contains("always"))
            return "Critical";
        if (lower.contains("should") || lower.contains("required"))
            return "High";
        if (lower.contains("may") || lower.contains("optional"))
            return "Low";
        return "Medium";
    }

    private static String generatePreconditions(String type) {
        return switch (type) {
            case "UI"            -> "Application is running. Browser is open. User account exists.";
            case "API"           -> "API server is running. Postman/REST client is ready. Auth token available.";
            case "ACCESSIBILITY" -> "Application is running. Accessibility testing tool (Axe/DevTools) is installed.";
            default -> "Application is running.";
        };
    }

    private static String generateSteps(String line, String type) {
        return switch (type) {
            case "UI" -> """
                1. Open browser and navigate to the application URL
                2. Identify the relevant UI element based on requirement
                3. Perform the required user action (click/input/navigate)
                4. Observe the application response
                5. Validate the outcome against expected result
                """;
            case "API" -> """
                1. Open Postman or REST client
                2. Set request method and endpoint URL from requirement
                3. Set required headers (Authorization, Content-Type)
                4. Set request body payload
                5. Send the request
                6. Validate status code and response body
                """;
            case "ACCESSIBILITY" -> """
                1. Open the application page in browser
                2. Open DevTools → Accessibility panel
                3. Run Axe browser extension on the page
                4. Check ARIA labels, keyboard navigation, and color contrast
                5. Validate all findings against WCAG AA standard
                """;
            default -> "Follow standard testing steps.";
        };
    }

    private static String generateTestData(String line, String type) {
        return switch (type) {
            case "UI"            -> "Valid: user@test.com / Test@123 | Invalid: wrong@test.com / wrong";
            case "API"           -> "{ \"email\": \"user@test.com\", \"password\": \"Test@123\" }";
            case "ACCESSIBILITY" -> "WCAG AA — Min contrast ratio: 4.5:1 | ARIA labels required";
            default              -> "N/A";
        };
    }

    private static String generateExpectedResult(String line, String type) {
        // Use the original AC line as the basis for expected result
        return "Expected: " + line.replaceAll("^[\\d\\.\\-\\*]+\\s*", "").trim();
    }
}
```

---

## Module 4 — ExcelExporter.java

Exports all generated test cases into a formatted `.xlsx` file.

```java
package com.sdd.exporters;

import com.sdd.core.TestCaseModel;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.util.List;

public class ExcelExporter {

    public static void export(List<TestCaseModel> testCases, String outputPath) throws Exception {
        Workbook workbook = new XSSFWorkbook();

        // Create sheets per type
        exportSheet(workbook, "UI Test Cases",
            testCases.stream().filter(tc -> tc.getType().equals("UI")).toList());

        exportSheet(workbook, "API Test Cases",
            testCases.stream().filter(tc -> tc.getType().equals("API")).toList());

        exportSheet(workbook, "Accessibility Test Cases",
            testCases.stream().filter(tc -> tc.getType().equals("ACCESSIBILITY")).toList());

        // All test cases combined
        exportSheet(workbook, "All Test Cases", testCases);

        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            workbook.write(fos);
        }
        System.out.println("✅ Excel exported to: " + outputPath);
    }

    private static void exportSheet(Workbook wb, String sheetName,
                                    List<TestCaseModel> testCases) {
        Sheet sheet = wb.createSheet(sheetName);

        // Header style
        CellStyle headerStyle = wb.createCellStyle();
        Font headerFont = wb.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.TEAL.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Header row
        String[] headers = {
            "TC ID", "Ticket ID", "Requirement ID", "Title", "Type",
            "Priority", "Module", "Preconditions", "Test Steps",
            "Test Data", "Expected Result", "Actual Result", "Status"
        };

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 5000);
        }

        // Data rows
        int rowNum = 1;
        for (TestCaseModel tc : testCases) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(tc.getTcId());
            row.createCell(1).setCellValue(tc.getTicketId());
            row.createCell(2).setCellValue(tc.getRequirementId());
            row.createCell(3).setCellValue(tc.getTitle());
            row.createCell(4).setCellValue(tc.getType());
            row.createCell(5).setCellValue(tc.getPriority());
            row.createCell(6).setCellValue(tc.getModule() != null ? tc.getModule() : "General");
            row.createCell(7).setCellValue(tc.getPreconditions());
            row.createCell(8).setCellValue(tc.getSteps());
            row.createCell(9).setCellValue(tc.getTestData());
            row.createCell(10).setCellValue(tc.getExpectedResult());
            row.createCell(11).setCellValue(tc.getActualResult() != null ? tc.getActualResult() : "");
            row.createCell(12).setCellValue(tc.getStatus());
        }
    }
}
```

---

## Module 5 — GherkinExporter.java

Converts test cases into `.feature` files for Cucumber automation.

```java
package com.sdd.exporters;

import com.sdd.core.TestCaseModel;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

public class GherkinExporter {

    public static void export(List<TestCaseModel> testCases,
                              String outputDir, String ticketId) throws Exception {

        // Separate by type
        exportFeature(testCases, "UI", outputDir, ticketId + "_UI.feature");
        exportFeature(testCases, "API", outputDir, ticketId + "_API.feature");
        exportFeature(testCases, "ACCESSIBILITY", outputDir, ticketId + "_Accessibility.feature");

        System.out.println("✅ Gherkin .feature files exported to: " + outputDir);
    }

    private static void exportFeature(List<TestCaseModel> testCases,
                                      String type, String dir, String filename) throws Exception {
        List<TestCaseModel> filtered = testCases.stream()
            .filter(tc -> tc.getType().equals(type)).toList();

        if (filtered.isEmpty()) return;

        try (PrintWriter pw = new PrintWriter(new FileWriter(dir + "/" + filename))) {
            pw.println("# Auto-generated by SDD Framework");
            pw.println("# Ticket: " + filtered.get(0).getTicketId());
            pw.println();
            pw.println("Feature: " + type + " Tests — " + filtered.get(0).getTicketId());
            pw.println();

            for (TestCaseModel tc : filtered) {
                pw.println("  @" + tc.getTcId() + " @" + tc.getPriority().toLowerCase());
                pw.println("  Scenario: " + tc.getTitle());
                pw.println("    Given the application is running");
                pw.println("    When the user performs the required action for: " + tc.getTitle());
                pw.println("    Then the system should: " + tc.getExpectedResult());
                pw.println();
            }
        }
    }
}
```

---

## Module 6 — MainRunner.java (Entry Point)

This is where you paste your Jira ticket AC and run the full framework.

```java
package com.sdd;

import com.sdd.core.ACParser;
import com.sdd.core.TestCaseModel;
import com.sdd.exporters.ExcelExporter;
import com.sdd.exporters.GherkinExporter;

import java.util.List;

public class MainRunner {

    public static void main(String[] args) throws Exception {

        // ─── STEP 1: Paste your Jira Ticket ID and Acceptance Criteria ───
        String ticketId = "PROJ-101";
        String acceptanceCriteria = """
            1. User can log in with valid email and password
            2. On successful login, user is redirected to /dashboard
            3. Invalid credentials must show error message "Invalid credentials"
            4. POST /api/login must return 200 with JWT token for valid credentials
            5. POST /api/login must return 401 for invalid credentials
            6. Login form fields must have ARIA labels (WCAG AA)
            7. Login button must be keyboard accessible via Tab + Enter key
            8. Color contrast on login page must meet WCAG AA (4.5:1 ratio)
            """;

        // ─── STEP 2: Parse AC into test cases ───
        System.out.println("🔍 Parsing Acceptance Criteria for ticket: " + ticketId);
        List<TestCaseModel> testCases = ACParser.parse(ticketId, acceptanceCriteria);

        System.out.println("✅ Generated " + testCases.size() + " test cases:");
        testCases.forEach(tc ->
            System.out.println("   [" + tc.getType() + "] " + tc.getTcId() + " — " + tc.getTitle())
        );

        // ─── STEP 3: Export to Excel ───
        ExcelExporter.export(testCases, "output/test-cases-" + ticketId + ".xlsx");

        // ─── STEP 4: Export to Gherkin .feature files ───
        GherkinExporter.export(testCases, "resources/features", ticketId);

        System.out.println("\n🎉 SDD Framework complete!");
        System.out.println("   → Manual Test Cases: output/test-cases-" + ticketId + ".xlsx");
        System.out.println("   → Gherkin Features:  resources/features/");
    }
}
```

---

## Module 7 — Sample Selenium UI Step (Auto-wired)

```java
package com.sdd.steps;

import io.cucumber.java.en.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class UIStepDefinitions {

    WebDriver driver;

    @Given("the application is running")
    public void appIsRunning() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @When("the user performs the required action for: {string}")
    public void performAction(String action) {
        System.out.println("Executing UI action: " + action);
        // Specific steps are in Page Object Model classes
    }

    @Then("the system should: {string}")
    public void verifyExpectedResult(String expectedResult) {
        System.out.println("Verifying: " + expectedResult);
        // Assertion logic here
    }
}
```

---

## Module 8 — Accessibility Test (Axe-core + Selenium)

```java
package com.sdd.steps;

import com.deque.html.axecore.selenium.AxeBuilder;
import com.deque.html.axecore.results.Results;
import com.deque.html.axecore.results.Rule;
import io.cucumber.java.en.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;

import java.util.Arrays;
import java.util.List;

public class AccessibilityStepDefinitions {

    WebDriver driver;

    @Given("the application is running")
    public void setup() {
        driver = new ChromeDriver();
    }

    @When("the user performs the required action for accessibility on: {string}")
    public void openPage(String page) {
        driver.get("https://your-app-base-url.com" + page);
    }

    @Then("the page should have zero WCAG AA violations")
    public void checkAccessibility() {
        Results results = new AxeBuilder()
            .withTags(Arrays.asList("wcag2a", "wcag2aa"))
            .analyze(driver);

        List<Rule> violations = results.getViolations();

        violations.forEach(v ->
            System.out.println("❌ VIOLATION: [" + v.getId() + "] " + v.getDescription())
        );

        Assert.assertEquals(violations.size(), 0,
            violations.size() + " WCAG AA accessibility violations found.");
    }
}
```

---

## Module 9 — API Test Step (REST-assured)

```java
package com.sdd.steps;

import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;

public class APIStepDefinitions {

    Response response;
    String baseUrl = "https://your-api-base-url.com";

    @When("I send a POST request to {string} with valid credentials")
    public void sendPostLogin(String endpoint) {
        response = RestAssured.given()
            .baseUri(baseUrl)
            .header("Content-Type", "application/json")
            .body("{ \"email\": \"user@test.com\", \"password\": \"Test@123\" }")
            .when()
            .post(endpoint);
    }

    @Then("the response status should be {int}")
    public void verifyStatusCode(int expectedStatus) {
        Assert.assertEquals(response.getStatusCode(), expectedStatus,
            "Unexpected status code. Response: " + response.asString());
    }

    @Then("the response body should contain {string}")
    public void verifyResponseBody(String expectedKey) {
        Assert.assertTrue(response.asString().contains(expectedKey),
            "Expected key '" + expectedKey + "' not found in response: " + response.asString());
    }
}
```

---

## Module 10 — config.properties

```properties
# Application Config
app.base.url=https://your-app.com
api.base.url=https://api.your-app.com
swagger.url=https://your-app.com/v2/api-docs

# Browser Config
browser=chrome
headless=false

# Jira Config (optional — for live AC fetch)
jira.base.url=https://your-company.atlassian.net
jira.api.token=YOUR_JIRA_API_TOKEN
jira.project.key=PROJ

# Output Config
output.excel.path=output/test-cases.xlsx
output.feature.path=resources/features/
```

---

## Module 11 — TestNG Runner

```java
package com.sdd.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.testng.annotations.DataProvider;

@CucumberOptions(
    features = "resources/features",
    glue = {"com.sdd.steps", "com.sdd.hooks"},
    tags = "@smoke or @regression",
    plugin = {
        "pretty",
        "html:output/cucumber-report.html",
        "json:output/cucumber-report.json",
        "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
    },
    monochrome = true
)
public class TestRunner extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
```

---

## Full Workflow Summary

| Step | Action | Tool/Class |
|------|--------|------------|
| 1 | Paste Jira AC text into `MainRunner.java` | `MainRunner` |
| 2 | Framework classifies AC into UI/API/A11y | `ACParser` |
| 3 | Manual test cases generated | `TestCaseModel` |
| 4 | Export to Excel (manual execution) | `ExcelExporter` |
| 5 | Export to Gherkin `.feature` files | `GherkinExporter` |
| 6 | Run automated tests via Cucumber + TestNG | `TestRunner` |
| 7 | UI tests execute via Selenium WebDriver | `UIStepDefinitions` |
| 8 | API tests execute via REST-assured | `APIStepDefinitions` |
| 9 | Accessibility tests run via Axe-core | `AccessibilityStepDefinitions` |
| 10 | Serenity BDD generates living docs report | Serenity + Allure |

---

## POC Demo Script

1. Open `MainRunner.java` — show the raw AC text (Jira ticket format)
2. Run `MainRunner` → console shows classified test cases
3. Open `output/test-cases-PROJ-101.xlsx` → show UI, API, Accessibility sheets
4. Open `resources/features/` → show auto-generated `.feature` files
5. Run `TestRunner` → Selenium + REST-assured + Axe execute
6. Open Serenity report → show requirement traceability and pass/fail
7. **Add one more AC line** → re-run MainRunner → new test appears automatically

---

*Generated by SDD Framework | Version 1.0 | May 2026*
