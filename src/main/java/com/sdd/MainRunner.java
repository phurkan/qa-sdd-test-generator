package com.sdd;

import com.sdd.core.ACParser;
import com.sdd.core.TestCaseModel;
import com.sdd.exporters.ExcelExporter;
import com.sdd.exporters.GherkinExporter;
import com.sdd.exporters.HTMLExporter;

import java.io.File;
import java.util.List;

/**
 * MainRunner - Entry Point for the SDD Framework
 *
 * HOW TO USE:
 * 1. Set your ticketId (e.g., "PROJ-101")
 * 2. Paste your Jira acceptance criteria in the acceptanceCriteria block
 * 3. Run this class as a Java main application
 * 4. Check output/ folder for:
 *    - test-cases-PROJ-101.xlsx   (Manual test cases - 4 sheets)
 *    - traceability-PROJ-101.html (HTML traceability matrix)
 *    - src/test/resources/features/ (.feature files for Cucumber)
 *
 * Then run: mvn clean test       (to execute automation)
 *           mvn serenity:aggregate (to generate living docs)
 */
public class MainRunner {

    public static void main(String[] args) throws Exception {
        new File("output").mkdirs();
        new File("src/test/resources/features").mkdirs();

        // ─────────────────────────────────────────────────
        // STEP 1: Set your Jira Ticket ID
        // ─────────────────────────────────────────────────
        String ticketId = "PROJ-101";

        // ─────────────────────────────────────────────────
        // STEP 2: Paste your Acceptance Criteria below
        //         (Copy from Jira ticket description)
        //         Framework auto-detects UI / API / Accessibility
        // ─────────────────────────────────────────────────
        String acceptanceCriteria = """
            1. User can log in with valid email and password
            2. On successful login, user must be redirected to /dashboard
            3. Invalid credentials should show error message "Invalid credentials"
            4. Login button must be visible and enabled on page load
            5. POST /api/login must return 200 with JWT token for valid credentials
            6. POST /api/login must return 401 for invalid credentials
            7. GET /api/users must return list of users with status 200
            8. Login form fields must have ARIA labels per WCAG AA standard
            9. Login button must be keyboard accessible via Tab and Enter keys
            10. Color contrast on login page must meet WCAG AA minimum ratio of 4.5:1
            """;

        // ─────────────────────────────────────────────────
        // STEP 3: Framework runs automatically
        // ─────────────────────────────────────────────────
        System.out.println("============================================");
        System.out.println("  SDD FRAMEWORK - Spec-Driven Test Generator");
        System.out.println("============================================");
        System.out.println("Ticket: " + ticketId);
        System.out.println("Parsing Acceptance Criteria...\n");

        List<TestCaseModel> testCases = ACParser.parse(ticketId, acceptanceCriteria);

        System.out.println("Generated " + testCases.size() + " Test Cases:");
        System.out.println("--------------------------------------------");
        testCases.forEach(tc -> System.out.println("  " + tc));
        System.out.println("--------------------------------------------\n");

        // Export to Excel
        String excelPath = "output/test-cases-" + ticketId + ".xlsx";
        ExcelExporter.export(testCases, excelPath);

        // Export to Gherkin .feature files
        GherkinExporter.export(testCases, "src/test/resources/features", ticketId);

        // Export to HTML Traceability Matrix
        String htmlPath = "output/traceability-" + ticketId + ".html";
        HTMLExporter.export(testCases, htmlPath, ticketId);

        System.out.println("\n============================================");
        System.out.println("  DONE! Outputs:");
        System.out.println("  Excel  -> " + excelPath);
        System.out.println("  HTML   -> " + htmlPath);
        System.out.println("  Features -> src/test/resources/features/");
        System.out.println("============================================");
        System.out.println("\nNext Steps:");
        System.out.println("  mvn clean test           -> Run automation");
        System.out.println("  mvn serenity:aggregate   -> Generate reports");
    }
}
