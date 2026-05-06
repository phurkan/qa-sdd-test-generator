package com.sdd.core;

import com.sdd.utils.ConfigReader;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * SwaggerSpecLoader
 * Parses a Swagger/OpenAPI URL and generates API test cases automatically.
 * Usage: SwaggerSpecLoader.generateFromSwagger("PROJ-101")
 */
public class SwaggerSpecLoader {

    public static OpenAPI load(String swaggerUrl) {
        SwaggerParseResult result = new OpenAPIParser().readLocation(swaggerUrl, null, null);
        if (result.getMessages() != null)
            result.getMessages().forEach(m -> System.out.println("⚠ Swagger parse: " + m));
        return result.getOpenAPI();
    }

    public static List<TestCaseModel> generateFromSwagger(String ticketId) {
        String swaggerUrl = ConfigReader.getSwaggerUrl();
        OpenAPI openAPI = load(swaggerUrl);
        List<TestCaseModel> testCases = new ArrayList<>();

        if (openAPI == null || openAPI.getPaths() == null) {
            System.err.println("Could not load OpenAPI spec from: " + swaggerUrl);
            return testCases;
        }

        int count = 1;
        for (Map.Entry<String, PathItem> pathEntry : openAPI.getPaths().entrySet()) {
            String path = pathEntry.getKey();
            PathItem pathItem = pathEntry.getValue();

            for (Map.Entry<PathItem.HttpMethod, Operation> opEntry :
                    pathItem.readOperationsMap().entrySet()) {

                String method = opEntry.getKey().name();
                Operation op = opEntry.getValue();
                String summary = op.getSummary() != null ? op.getSummary() : method + " " + path;

                testCases.add(new TestCaseModel.Builder()
                    .tcId(String.format("API-TC-%03d", count++))
                    .ticketId(ticketId)
                    .requirementId(ticketId + "-API-SWAGGER-" + String.format("%03d", count))
                    .title("Validate API: " + method + " " + path + " — " + summary)
                    .type("API")
                    .priority(detectPriority(method))
                    .module("API — " + path.split("/")[1])
                    .preconditions("1. API server running\n2. Auth token available\n3. Base URL set in config")
                    .steps("Step 1: Open Postman\nStep 2: Set method: " + method +
                           "\nStep 3: Set endpoint: " + path +
                           "\nStep 4: Set headers & body\nStep 5: Send request\nStep 6: Verify status & body")
                    .testData("Method: " + method + " | Endpoint: " + path)
                    .expectedResult("Expected: Valid response per OpenAPI spec for " + method + " " + path)
                    .status("Pending")
                    .originalAC(method + " " + path + ": " + summary)
                    .build());
            }
        }

        System.out.println("✅ Generated " + testCases.size() + " API test cases from Swagger spec");
        return testCases;
    }

    private static String detectPriority(String method) {
        return switch (method.toUpperCase()) {
            case "POST", "DELETE" -> "Critical";
            case "GET"            -> "High";
            case "PUT", "PATCH"   -> "Medium";
            default               -> "Medium";
        };
    }
}
