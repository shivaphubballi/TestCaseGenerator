package com.testgen.generator;

import com.testgen.model.ApiEndpoint;
import com.testgen.model.TestCase;
import com.testgen.model.TestCase.TestStep;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Generates REST Assured tests from API endpoints.
 */
public class RestAssuredTestGenerator {
    /**
     * Generates REST Assured tests for a list of API endpoints.
     *
     * @param endpoints   The API endpoints
     * @param outputDir   The directory to save the generated tests
     * @param packageName The package name for the generated tests
     * @throws IOException If an error occurs during generation
     */
    public void generate(List<ApiEndpoint> endpoints, String outputDir, String packageName) throws IOException {
        // Create default test cases based on endpoints
        List<TestCase> defaultTestCases = createDefaultTestCases(endpoints);
        generate(endpoints, defaultTestCases, outputDir, packageName);
    }
    
    /**
     * Generates REST Assured tests for a list of API endpoints and test cases.
     *
     * @param endpoints   The API endpoints
     * @param testCases   The test cases to generate
     * @param outputDir   The directory to save the generated tests
     * @param packageName The package name for the generated tests
     * @throws IOException If an error occurs during generation
     */
    public void generate(List<ApiEndpoint> endpoints, List<TestCase> testCases, String outputDir, String packageName) throws IOException {
        // Create output directory if it doesn't exist
        File outputDirFile = new File(outputDir);
        if (!outputDirFile.exists()) {
            outputDirFile.mkdirs();
        }
        
        // Create subdirectory for REST Assured tests
        File restAssuredDir = new File(outputDirFile, "restassured");
        if (!restAssuredDir.exists()) {
            restAssuredDir.mkdirs();
        }
        
        // Generate base test class
        generateBaseTestClass(restAssuredDir, packageName);
        
        // Generate a test class for each test case
        for (TestCase testCase : testCases) {
            if (testCase.getType() == TestCase.TestType.API) {
                generateTestClass(restAssuredDir, packageName, testCase, endpoints);
            }
        }
    }
    
    /**
     * Generates the base test class.
     *
     * @param outputDir   The directory to save the generated class
     * @param packageName The package name for the generated class
     * @throws IOException If an error occurs during generation
     */
    private void generateBaseTestClass(File outputDir, String packageName) throws IOException {
        String className = "BaseRestAssuredTest";
        File outputFile = new File(outputDir, className + ".java");
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            // Package declaration
            writer.println("package " + packageName + ";");
            writer.println();
            
            // Import statements
            writer.println("import io.restassured.RestAssured;");
            writer.println("import io.restassured.response.Response;");
            writer.println("import io.restassured.specification.RequestSpecification;");
            writer.println("import org.junit.Before;");
            writer.println();
            writer.println("import static io.restassured.RestAssured.given;");
            writer.println();
            
            // Class declaration
            writer.println("/**");
            writer.println(" * Base class for generated REST Assured tests.");
            writer.println(" */");
            writer.println("public abstract class " + className + " {");
            writer.println("    protected RequestSpecification request;");
            writer.println();
            
            // Before method
            writer.println("    @Before");
            writer.println("    public void setUp() {");
            writer.println("        // Set up RestAssured");
            writer.println("        RestAssured.baseURI = \"https://api.example.com\";");
            writer.println("        ");
            writer.println("        // Create base request specification");
            writer.println("        request = given()");
            writer.println("            .header(\"Content-Type\", \"application/json\");");
            writer.println("    }");
            writer.println();
            
            writer.println("    /**");
            writer.println("     * Helper method to validate common response conditions.");
            writer.println("     *");
            writer.println("     * @param response The response to validate");
            writer.println("     * @param expectedStatusCode The expected status code");
            writer.println("     */");
            writer.println("    protected void validateResponse(Response response, int expectedStatusCode) {");
            writer.println("        response.then()");
            writer.println("            .statusCode(expectedStatusCode);");
            writer.println("    }");
            writer.println("}");
        }
    }
    
    /**
     * Generates a test class for a test case.
     *
     * @param outputDir   The directory to save the generated class
     * @param packageName The package name for the generated class
     * @param testCase    The test case
     * @param endpoints   The API endpoints
     * @throws IOException If an error occurs during generation
     */
    private void generateTestClass(File outputDir, String packageName, TestCase testCase, List<ApiEndpoint> endpoints) throws IOException {
        String className = formatClassName(testCase.getName()) + "Test";
        File outputFile = new File(outputDir, className + ".java");
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            // Package declaration
            writer.println("package " + packageName + ";");
            writer.println();
            
            // Import statements
            writer.println("import io.restassured.response.Response;");
            writer.println("import org.junit.Test;");
            writer.println("import static org.hamcrest.Matchers.*;");
            writer.println();
            
            // Class declaration
            writer.println("/**");
            writer.println(" * " + testCase.getDescription());
            writer.println(" */");
            writer.println("public class " + className + " extends BaseRestAssuredTest {");
            
            // Test method
            writer.println("    @Test");
            writer.println("    public void " + formatMethodName(testCase.getName()) + "() {");
            
            // Find the relevant endpoint for this test case
            ApiEndpoint endpoint = findRelevantEndpoint(endpoints, testCase);
            
            if (endpoint != null) {
                // Generate test steps
                for (TestStep step : testCase.getSteps()) {
                    writer.println("        // " + step.getDescription());
                    writer.println("        // Expected: " + step.getExpectedResult());
                    
                    // Generate step implementation based on the description
                    String description = step.getDescription().toLowerCase();
                    
                    if (description.contains("prepare") || description.contains("create") || description.contains("request")) {
                        // Generate request preparation code
                        writer.println("        RequestSpecification apiRequest = request.copy();");
                        
                        // Add headers
                        for (Map.Entry<String, String> header : endpoint.getHeaders().entrySet()) {
                            writer.println("        apiRequest.header(\"" + header.getKey() + "\", \"" + header.getValue() + "\");");
                        }
                        
                        // Add request body if necessary
                        if (endpoint.getRequestBody() != null && !endpoint.getRequestBody().isEmpty()) {
                            writer.println("        String requestBody = \"" + endpoint.getRequestBody().replace("\"", "\\\"") + "\";");
                            writer.println("        apiRequest.body(requestBody);");
                        }
                    } else if (description.contains("send") || description.contains("execute") || description.contains("call")) {
                        // Generate request execution code
                        writer.println("        Response response = apiRequest");
                        
                        // Method call
                        writer.print("            ." + endpoint.getMethod().toLowerCase() + "(\"");
                        
                        // URL path
                        String path = endpoint.getUrl().replace("https://api.example.com", "");
                        if (path.isEmpty()) {
                            path = "/";
                        }
                        writer.println(path + "\");");
                    } else if (description.contains("validate") || description.contains("verify") || description.contains("check")) {
                        // Generate response validation code
                        writer.println("        validateResponse(response, " + endpoint.getExpectedStatusCode() + ");");
                        
                        // Add custom validations based on test case
                        if (description.contains("status")) {
                            writer.println("        response.then().statusCode(" + endpoint.getExpectedStatusCode() + ");");
                        }
                        
                        if (description.contains("body") || description.contains("response")) {
                            writer.println("        // Add additional body validations here");
                            writer.println("        // Example: response.then().body(\"field\", equalTo(\"value\"));");
                        }
                        
                        if (description.contains("header")) {
                            writer.println("        // Add header validations here");
                            writer.println("        // Example: response.then().header(\"Content-Type\", equalTo(\"application/json\"));");
                        }
                    }
                    
                    writer.println();
                }
            } else {
                // Generic test if no specific endpoint is found
                writer.println("        // This is a generic test as no specific endpoint was found for this test case");
                writer.println("        // Replace the URL and method with the actual API endpoint");
                writer.println("        Response response = request");
                writer.println("            .get(\"/generic\");");
                writer.println();
                writer.println("        // Validate the response");
                writer.println("        validateResponse(response, 200);");
            }
            
            writer.println("    }");
            writer.println("}");
        }
    }
    
    /**
     * Finds a relevant endpoint for a test case.
     *
     * @param endpoints The API endpoints
     * @param testCase  The test case
     * @return The relevant endpoint, or null if not found
     */
    private ApiEndpoint findRelevantEndpoint(List<ApiEndpoint> endpoints, TestCase testCase) {
        if (endpoints.isEmpty()) {
            return null;
        }
        
        String testCaseName = testCase.getName().toLowerCase();
        
        // Try to find by name
        for (ApiEndpoint endpoint : endpoints) {
            if (endpoint.getName() != null && testCaseName.contains(endpoint.getName().toLowerCase())) {
                return endpoint;
            }
        }
        
        // Try to find by URL
        for (ApiEndpoint endpoint : endpoints) {
            String urlPath = endpoint.getUrl().replaceAll("^https?://[^/]+", "");
            if (!urlPath.isEmpty() && testCaseName.contains(urlPath.toLowerCase())) {
                return endpoint;
            }
        }
        
        // Try to find by HTTP method
        for (ApiEndpoint endpoint : endpoints) {
            if (testCaseName.contains(endpoint.getMethod().toLowerCase())) {
                return endpoint;
            }
        }
        
        // Return the first endpoint if no match is found
        return endpoints.get(0);
    }
    
    /**
     * Creates default test cases based on API endpoints.
     *
     * @param endpoints The API endpoints
     * @return The default test cases
     */
    private List<TestCase> createDefaultTestCases(List<ApiEndpoint> endpoints) {
        List<TestCase> testCases = new ArrayList<>();
        
        for (ApiEndpoint endpoint : endpoints) {
            String testCaseName = endpoint.getName() != null ? endpoint.getName() : 
                               endpoint.getMethod() + " " + endpoint.getUrl();
            
            TestCase testCase = new TestCase(
                testCaseName,
                "Test for " + endpoint.getMethod() + " " + endpoint.getUrl(),
                TestCase.TestType.API
            );
            
            testCase.addStep(new TestStep(
                "Prepare " + endpoint.getMethod() + " request to " + endpoint.getUrl(),
                "Request should be prepared with appropriate headers and body"
            ));
            
            testCase.addStep(new TestStep(
                "Send the request",
                "Request should be sent successfully"
            ));
            
            testCase.addStep(new TestStep(
                "Validate the response status code",
                "Response should have status code " + endpoint.getExpectedStatusCode()
            ));
            
            testCase.addStep(new TestStep(
                "Validate the response body",
                "Response body should contain the expected data"
            ));
            
            testCases.add(testCase);
        }
        
        return testCases;
    }
    
    /**
     * Formats a name as a class name.
     *
     * @param name The name
     * @return The formatted class name
     */
    private String formatClassName(String name) {
        // Remove special characters and spaces
        String className = name.replaceAll("[^a-zA-Z0-9]", " ");
        
        // Split by spaces
        String[] parts = className.split("\\s+");
        
        // Capitalize each part
        StringBuilder formattedName = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                formattedName.append(Character.toUpperCase(part.charAt(0)));
                if (part.length() > 1) {
                    formattedName.append(part.substring(1).toLowerCase());
                }
            }
        }
        
        return formattedName.toString();
    }
    
    /**
     * Formats a name as a method name.
     *
     * @param name The name
     * @return The formatted method name
     */
    private String formatMethodName(String name) {
        // First format as class name
        String className = formatClassName(name);
        
        // Convert to camel case
        if (!className.isEmpty()) {
            return Character.toLowerCase(className.charAt(0)) + className.substring(1);
        } else {
            return "test";
        }
    }
}
