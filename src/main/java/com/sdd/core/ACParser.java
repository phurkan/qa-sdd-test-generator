package com.sdd.core;

import java.util.*;

/**
 * ACParser - Core Intelligence Engine
 * Reads acceptance criteria text, classifies each line into
 * UI / API / ACCESSIBILITY type, and builds TestCaseModel list.
 */
public class ACParser {

    private static final List<String> UI_KEYWORDS = Arrays.asList(
        "user", "button", "click", "navigate", "page", "form", "screen",
        "visible", "display", "redirect", "login", "dashboard", "modal",
        "dropdown", "input", "field", "message", "ui", "frontend", "text",
        "link", "menu", "header", "footer", "image", "icon", "popup",
        "notification", "toast", "alert", "table", "list", "search", "filter"
    );

    private static final List<String> API_KEYWORDS = Arrays.asList(
        "api", "endpoint", "post", "get", "put", "delete", "patch",
        "status code", "response", "payload", "request", "jwt", "token",
        "http", "rest", "json", "backend", "service", "return", "returns",
        "200", "201", "400", "401", "403", "404", "500", "authentication", "authorization"
    );

    private static final List<String> A11Y_KEYWORDS = Arrays.asList(
        "wcag", "aria", "accessibility", "screen reader", "keyboard",
        "tab", "contrast", "focus", "alt text", "label", "role",
        "accessible", "a11y", "color contrast", "keyboard navigation"
    );

    private static final Map<String, String> PRIORITY_MAP = new LinkedHashMap<>() {{
        put("critical", "Critical"); put("must", "Critical");
        put("shall", "Critical");    put("always", "Critical");
        put("required", "High");     put("should", "High");
        put("important", "High");    put("may", "Low");
        put("optional", "Low");
    }};

    public static List<TestCaseModel> parse(String ticketId, String acText) {
        List<TestCaseModel> testCases = new ArrayList<>();
        String[] lines = acText.split("\n");
        int uiCount = 1, apiCount = 1, a11yCount = 1;

        for (String rawLine : lines) {
            String line = rawLine.trim();
            if (line.isEmpty() || line.startsWith("#") || line.length() < 5) continue;

            String type = classify(line);
            String tcId = switch (type) {
                case "UI"            -> String.format("UI-TC-%03d", uiCount++);
                case "API"           -> String.format("API-TC-%03d", apiCount++);
                case "ACCESSIBILITY" -> String.format("ACC-TC-%03d", a11yCount++);
                default              -> "TC-000";
            };

            String cleanLine = line.replaceAll("^[\\d\\.\\-\\*\\>]+\\s*", "").trim();
            int seqNum = type.equals("UI") ? uiCount-1 : type.equals("API") ? apiCount-1 : a11yCount-1;

            testCases.add(new TestCaseModel.Builder()
                .tcId(tcId)
                .ticketId(ticketId)
                .requirementId(ticketId + "-" + type.substring(0,2) + "-REQ-" + String.format("%03d", seqNum))
                .title(generateTitle(cleanLine, type))
                .type(type)
                .priority(detectPriority(line))
                .module(detectModule(line))
                .preconditions(generatePreconditions(type))
                .steps(generateSteps(type))
                .testData(generateTestData(type))
                .expectedResult("Expected: " + cleanLine)
                .actualResult("")
                .status("Pending")
                .originalAC(line)
                .build());
        }
        return testCases;
    }

    private static String classify(String line) {
        String lower = line.toLowerCase();
        long a11y = A11Y_KEYWORDS.stream().filter(lower::contains).count();
        long api  = API_KEYWORDS.stream().filter(lower::contains).count();
        long ui   = UI_KEYWORDS.stream().filter(lower::contains).count();
        if (a11y >= 1) return "ACCESSIBILITY";
        if (api > ui)  return "API";
        return "UI";
    }

    private static String generateTitle(String line, String type) {
        String prefix = switch (type) {
            case "UI"            -> "Verify UI: ";
            case "API"           -> "Validate API: ";
            case "ACCESSIBILITY" -> "Check A11y: ";
            default              -> "Test: ";
        };
        String s = line.length() > 75 ? line.substring(0, 75) + "..." : line;
        return prefix + Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private static String detectPriority(String line) {
        String lower = line.toLowerCase();
        for (Map.Entry<String, String> e : PRIORITY_MAP.entrySet())
            if (lower.contains(e.getKey())) return e.getValue();
        return "Medium";
    }

    private static String detectModule(String line) {
        String lower = line.toLowerCase();
        if (lower.contains("login") || lower.contains("logout") || lower.contains("auth")) return "Authentication";
        if (lower.contains("dashboard") || lower.contains("home")) return "Dashboard";
        if (lower.contains("api") || lower.contains("endpoint")) return "API Layer";
        if (lower.contains("profile") || lower.contains("user")) return "User Management";
        if (lower.contains("wcag") || lower.contains("aria") || lower.contains("accessibility")) return "Accessibility";
        return "General";
    }

    private static String generatePreconditions(String type) {
        return switch (type) {
            case "UI" ->
                "1. Application is deployed and running\n" +
                "2. Browser (Chrome/Firefox) is open\n" +
                "3. Valid test user account exists\n" +
                "4. Test environment is accessible";
            case "API" ->
                "1. API server is running\n" +
                "2. Postman / REST client is ready\n" +
                "3. Valid authentication token is available\n" +
                "4. API base URL is set in config.properties";
            case "ACCESSIBILITY" ->
                "1. Application is running in browser\n" +
                "2. Axe DevTools extension is installed\n" +
                "3. WCAG 2.1 AA checklist is ready";
            default -> "Application is running.";
        };
    }

    private static String generateSteps(String type) {
        return switch (type) {
            case "UI" ->
                "Step 1: Open browser and navigate to app URL\n" +
                "Step 2: Log in with valid test credentials\n" +
                "Step 3: Navigate to the relevant page/module\n" +
                "Step 4: Identify the UI element from requirement\n" +
                "Step 5: Perform the required user action\n" +
                "Step 6: Observe application response\n" +
                "Step 7: Compare with expected result";
            case "API" ->
                "Step 1: Open Postman or REST client\n" +
                "Step 2: Set request method and endpoint URL\n" +
                "Step 3: Set headers (Content-Type, Authorization)\n" +
                "Step 4: Set request body payload if applicable\n" +
                "Step 5: Send request\n" +
                "Step 6: Verify status code\n" +
                "Step 7: Verify response body fields";
            case "ACCESSIBILITY" ->
                "Step 1: Open target page in Chrome\n" +
                "Step 2: Run Axe DevTools - click Analyze\n" +
                "Step 3: Review violations by severity\n" +
                "Step 4: Check ARIA labels via Inspect Element\n" +
                "Step 5: Test keyboard navigation (Tab/Enter only)\n" +
                "Step 6: Check color contrast via DevTools\n" +
                "Step 7: Record all WCAG AA violations";
            default -> "Follow standard test execution steps.";
        };
    }

    private static String generateTestData(String type) {
        return switch (type) {
            case "UI" ->
                "Valid: testuser@example.com / Test@123\n" +
                "Invalid: wrong@example.com / wrongpass\n" +
                "Edge: empty fields, special characters";
            case "API" ->
                "Valid: { \"email\": \"test@example.com\", \"password\": \"Test@123\" }\n" +
                "Invalid: { \"email\": \"wrong@example.com\", \"password\": \"wrong\" }\n" +
                "Headers: Content-Type: application/json | Authorization: Bearer <token>";
            case "ACCESSIBILITY" ->
                "WCAG Standard: 2.1 AA\n" +
                "Min contrast ratio: 4.5:1 (body text), 3:1 (large text)\n" +
                "Required: aria-label, aria-describedby, role, alt";
            default -> "N/A";
        };
    }
}
