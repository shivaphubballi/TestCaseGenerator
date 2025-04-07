package com.testgen.generator;

import com.testgen.exception.TestGenException;
import com.testgen.model.ApiEndpoint;
import com.testgen.model.TestCase;

import java.util.List;
import java.util.Map;

/**
 * Generates REST Assured test code from API endpoints.
 */
public class RestAssuredTestGenerator {
    
    /**
     * Generates a REST Assured test class from a list of API endpoints.
     *
     * @param endpoints List of API endpoints
     * @param className Name of the test class
     * @param packageName Package for the test class
     * @return Generated REST Assured test class source code
     * @throws TestGenException If code generation fails
     */
    public String generateTestClass(List<ApiEndpoint> endpoints, String className, String packageName) 
            throws TestGenException {
        if (endpoints == null || endpoints.isEmpty()) {
            throw new TestGenException("No API endpoints provided for REST Assured test generation");
        }
        
        StringBuilder sb = new StringBuilder();
        
        // Add package declaration
        if (packageName != null && !packageName.isEmpty()) {
            sb.append("package ").append(packageName).append(";\n\n");
        }
        
        // Add imports
        sb.append("import io.restassured.RestAssured;\n");
        sb.append("import io.restassured.http.ContentType;\n");
        sb.append("import io.restassured.response.Response;\n");
        sb.append("import io.restassured.specification.RequestSpecification;\n");
        sb.append("import org.junit.Before;\n");
        sb.append("import org.junit.Test;\n");
        sb.append("import org.junit.Assert;\n\n");
        sb.append("import java.util.HashMap;\n");
        sb.append("import java.util.Map;\n\n");
        
        // Extract base URL from the first endpoint
        String baseUrl = "";
        if (!endpoints.isEmpty()) {
            String url = endpoints.get(0).getUrl();
            if (url != null && !url.isEmpty()) {
                // Extract base URL (protocol + host)
                int pathStart = url.indexOf('/', url.indexOf("//") + 2);
                if (pathStart > 0) {
                    baseUrl = url.substring(0, pathStart);
                } else {
                    baseUrl = url;
                }
            }
        }
        
        // Class declaration
        sb.append("/**\n");
        sb.append(" * REST Assured tests generated for API endpoints\n");
        sb.append(" */\n");
        sb.append("public class ").append(className).append(" {\n\n");
        
        // Add class-level fields
        sb.append("    private String baseUrl = \"").append(baseUrl).append("\";\n\n");
        
        // Setup method
        sb.append("    @Before\n");
        sb.append("    public void setUp() {\n");
        sb.append("        // Configure REST Assured\n");
        sb.append("        RestAssured.baseURI = baseUrl;\n");
        sb.append("        // Uncomment and set if authentication is needed\n");
        sb.append("        // RestAssured.authentication = RestAssured.basic(\"username\", \"password\");\n");
        sb.append("    }\n\n");
        
        // Generate test methods for each endpoint
        for (ApiEndpoint endpoint : endpoints) {
            generateEndpointTest(sb, endpoint);
        }
        
        // Close class
        sb.append("}\n");
        
        return sb.toString();
    }
    
    /**
     * Generates a test method for a single API endpoint.
     *
     * @param sb StringBuilder to append to
     * @param endpoint The API endpoint to generate a test for
     */
    private void generateEndpointTest(StringBuilder sb, ApiEndpoint endpoint) {
        String methodName = "test" + capitalize(endpoint.getShortName());
        
        // Add method Javadoc
        sb.append("    /**\n");
        sb.append("     * Tests the ").append(endpoint.getName()).append(" endpoint\n");
        sb.append("     * Method: ").append(endpoint.getMethod()).append("\n");
        sb.append("     * URL: ").append(endpoint.getUrl()).append("\n");
        if (endpoint.getDescription() != null && !endpoint.getDescription().isEmpty()) {
            sb.append("     * Description: ").append(endpoint.getDescription()).append("\n");
        }
        sb.append("     */\n");
        
        // Add test annotation and method signature
        sb.append("    @Test\n");
        sb.append("    public void ").append(methodName).append("() {\n");
        
        // Get the request method
        String method = endpoint.getMethod().toUpperCase();
        
        // Prepare query parameters if any
        Map<String, String> queryParams = endpoint.getQueryParams();
        if (!queryParams.isEmpty()) {
            sb.append("        // Set up query parameters\n");
            sb.append("        Map<String, String> queryParams = new HashMap<>();\n");
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                sb.append("        queryParams.put(\"").append(entry.getKey()).append("\", \"")
                  .append(entry.getValue()).append("\");\n");
            }
            sb.append("\n");
        }
        
        // Prepare headers
        Map<String, String> headers = endpoint.getHeaders();
        if (!headers.isEmpty()) {
            sb.append("        // Set up headers\n");
            sb.append("        Map<String, String> headers = new HashMap<>();\n");
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                // Skip content-length as it's automatically set by REST Assured
                if (!entry.getKey().equalsIgnoreCase("content-length")) {
                    sb.append("        headers.put(\"").append(entry.getKey()).append("\", \"")
                      .append(entry.getValue()).append("\");\n");
                }
            }
            sb.append("\n");
        }
        
        // Request specification
        sb.append("        // Create request specification\n");
        sb.append("        RequestSpecification request = RestAssured.given()\n");
        sb.append("            .log().all()");
        
        // Add content type if available in headers
        boolean hasContentType = false;
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if (entry.getKey().equalsIgnoreCase("content-type")) {
                String contentType = entry.getValue().toLowerCase();
                sb.append("\n");
                if (contentType.contains("json")) {
                    sb.append("            .contentType(ContentType.JSON)");
                } else if (contentType.contains("xml")) {
                    sb.append("            .contentType(ContentType.XML)");
                } else if (contentType.contains("text")) {
                    sb.append("            .contentType(ContentType.TEXT)");
                } else if (contentType.contains("form")) {
                    sb.append("            .contentType(ContentType.URLENC)");
                } else {
                    sb.append("            .contentType(\"").append(entry.getValue()).append("\")");
                }
                hasContentType = true;
                break;
            }
        }
        
        // Add a default content type for POST, PUT, PATCH if none was specified and there's a body
        if (!hasContentType && (method.equals("POST") || method.equals("PUT") || method.equals("PATCH")) 
                && (endpoint.getRequestBody() != null && !endpoint.getRequestBody().isEmpty())) {
            sb.append("\n");
            
            // Determine content type from the body
            String requestBody = endpoint.getRequestBody();
            String requestBodyType = endpoint.getRequestBodyType();
            
            if ("json".equalsIgnoreCase(requestBodyType) || (requestBody.trim().startsWith("{") || requestBody.trim().startsWith("["))) {
                sb.append("            .contentType(ContentType.JSON)");
            } else if ("xml".equalsIgnoreCase(requestBodyType) || requestBody.trim().startsWith("<")) {
                sb.append("            .contentType(ContentType.XML)");
            } else {
                sb.append("            .contentType(ContentType.TEXT)");
            }
        }
        
        // Add headers
        if (!headers.isEmpty()) {
            sb.append("\n            .headers(headers)");
        }
        
        // Add query parameters
        if (!queryParams.isEmpty()) {
            sb.append("\n            .queryParams(queryParams)");
        }
        
        // Add request body for POST, PUT, PATCH
        if (method.equals("POST") || method.equals("PUT") || method.equals("PATCH")) {
            String requestBodyType = endpoint.getRequestBodyType();
            
            if ("formdata".equals(requestBodyType) || "urlencoded".equals(requestBodyType)) {
                // For form data, add each field
                Map<String, String> formData = endpoint.getFormData();
                if (!formData.isEmpty()) {
                    sb.append("\n\n        // Set form parameters\n");
                    for (Map.Entry<String, String> entry : formData.entrySet()) {
                        sb.append("        request = request.formParam(\"").append(entry.getKey())
                          .append("\", \"").append(entry.getValue()).append("\");\n");
                    }
                }
            } else {
                // For raw body (JSON, XML, text, etc.)
                String requestBody = endpoint.getRequestBody();
                if (requestBody != null && !requestBody.isEmpty()) {
                    sb.append("\n\n        // Set request body\n");
                    sb.append("        String requestBody = ");
                    
                    // For multi-line bodies, use Java text blocks (Java 15+) or concatenate strings
                    if (requestBody.contains("\n")) {
                        // Using regular string concatenation for better compatibility
                        String[] lines = requestBody.split("\n");
                        sb.append("\"").append(escapeStringForJava(lines[0])).append("\"");
                        for (int i = 1; i < lines.length; i++) {
                            sb.append("\n            + \"").append(escapeStringForJava(lines[i])).append("\"");
                        }
                    } else {
                        sb.append("\"").append(escapeStringForJava(requestBody)).append("\"");
                    }
                    sb.append(";\n");
                    sb.append("        request = request.body(requestBody);\n");
                }
            }
        }
        
        // Execute the request
        sb.append("\n        // Send the request and get response\n");
        sb.append("        Response response = request.");
        
        switch (method) {
            case "GET":
                sb.append("get(\"").append(endpoint.getPath()).append("\");\n");
                break;
            case "POST":
                sb.append("post(\"").append(endpoint.getPath()).append("\");\n");
                break;
            case "PUT":
                sb.append("put(\"").append(endpoint.getPath()).append("\");\n");
                break;
            case "DELETE":
                sb.append("delete(\"").append(endpoint.getPath()).append("\");\n");
                break;
            case "PATCH":
                sb.append("patch(\"").append(endpoint.getPath()).append("\");\n");
                break;
            case "HEAD":
                sb.append("head(\"").append(endpoint.getPath()).append("\");\n");
                break;
            case "OPTIONS":
                sb.append("options(\"").append(endpoint.getPath()).append("\");\n");
                break;
            default:
                sb.append("get(\"").append(endpoint.getPath()).append("\");\n");
                break;
        }
        
        // Log the response
        sb.append("        response.then().log().all();\n\n");
        
        // Add assertions
        sb.append("        // Assertions\n");
        
        // Status code assertion
        int expectedStatus = endpoint.getExpectedStatusCode();
        sb.append("        Assert.assertEquals(\"Status code should be ").append(expectedStatus).append("\", ")
          .append(expectedStatus).append(", response.getStatusCode());\n\n");
        
        // Content type assertion if we expect JSON
        boolean expectsJson = false;
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if (entry.getKey().equalsIgnoreCase("accept") && 
                entry.getValue().toLowerCase().contains("json")) {
                expectsJson = true;
                break;
            }
        }
        
        // If we have example responses, add more specific assertions
        List<Map<String, Object>> exampleResponses = endpoint.getExampleResponses();
        if (!exampleResponses.isEmpty()) {
            for (Map<String, Object> example : exampleResponses) {
                if (example.containsKey("code") && 
                    (int)example.get("code") == expectedStatus && 
                    example.containsKey("jsonBody")) {
                    
                    sb.append("        // Additional assertions based on example response\n");
                    sb.append("        // Uncomment and customize as needed\n");
                    sb.append("        // response.then().body(\"fieldName\", equalTo(\"expectedValue\"));\n");
                    break;
                }
            }
        } else if (expectsJson) {
            // Generic JSON validation
            sb.append("        // Validate response format\n");
            sb.append("        // Uncomment and customize as needed\n");
            sb.append("        // response.then().body(\"fieldName\", equalTo(\"expectedValue\"));\n");
        }
        
        // Close method
        sb.append("    }\n\n");
    }
    
    /**
     * Generates a REST Assured test class from a list of test cases.
     *
     * @param testCases List of test cases
     * @param className Name of the test class
     * @param packageName Package for the test class
     * @return Generated REST Assured test class source code
     * @throws TestGenException If code generation fails
     */
    public String generateTestClassFromTestCases(List<TestCase> testCases, String className, String packageName) 
            throws TestGenException {
        if (testCases == null || testCases.isEmpty()) {
            throw new TestGenException("No test cases provided for REST Assured test generation");
        }
        
        StringBuilder sb = new StringBuilder();
        
        // Add package declaration
        if (packageName != null && !packageName.isEmpty()) {
            sb.append("package ").append(packageName).append(";\n\n");
        }
        
        // Add imports
        sb.append("import io.restassured.RestAssured;\n");
        sb.append("import io.restassured.http.ContentType;\n");
        sb.append("import io.restassured.response.Response;\n");
        sb.append("import io.restassured.specification.RequestSpecification;\n");
        sb.append("import org.junit.Before;\n");
        sb.append("import org.junit.Test;\n");
        sb.append("import org.junit.Assert;\n\n");
        sb.append("import java.util.HashMap;\n");
        sb.append("import java.util.Map;\n\n");
        
        // Extract base URL if available in metadata
        String baseUrl = "";
        for (TestCase testCase : testCases) {
            if (testCase.getMetadata() != null && testCase.getMetadata().containsKey("baseUrl")) {
                baseUrl = testCase.getMetadata().get("baseUrl");
                break;
            }
        }
        
        // Class declaration
        sb.append("/**\n");
        sb.append(" * REST Assured tests generated from test cases\n");
        sb.append(" */\n");
        sb.append("public class ").append(className).append(" {\n\n");
        
        // Add class-level fields
        sb.append("    private String baseUrl = \"").append(baseUrl).append("\";\n\n");
        
        // Setup method
        sb.append("    @Before\n");
        sb.append("    public void setUp() {\n");
        sb.append("        // Configure REST Assured\n");
        sb.append("        RestAssured.baseURI = baseUrl;\n");
        sb.append("        // Uncomment and set if authentication is needed\n");
        sb.append("        // RestAssured.authentication = RestAssured.basic(\"username\", \"password\");\n");
        sb.append("    }\n\n");
        
        // Generate test methods for each test case
        for (TestCase testCase : testCases) {
            if (testCase.getType() == TestCase.TestType.API) {
                generateTestCaseMethod(sb, testCase);
            }
        }
        
        // Close class
        sb.append("}\n");
        
        return sb.toString();
    }
    
    /**
     * Generates a test method for a single test case.
     *
     * @param sb StringBuilder to append to
     * @param testCase The test case to generate code for
     */
    private void generateTestCaseMethod(StringBuilder sb, TestCase testCase) {
        String methodName = testCase.toMethodName();
        
        // Add method Javadoc
        sb.append("    /**\n");
        sb.append("     * ").append(testCase.getName()).append("\n");
        if (testCase.getDescription() != null && !testCase.getDescription().isEmpty()) {
            sb.append("     * \n");
            sb.append("     * ").append(testCase.getDescription()).append("\n");
        }
        sb.append("     */\n");
        
        // Add test annotation and method signature
        sb.append("    @Test\n");
        sb.append("    public void ").append(methodName).append("() {\n");
        
        // Extract metadata
        Map<String, String> metadata = testCase.getMetadata();
        String url = metadata.getOrDefault("url", "");
        String method = metadata.getOrDefault("method", "GET");
        String contentType = metadata.getOrDefault("contentType", "");
        
        // Request specification
        sb.append("        // Create request specification\n");
        sb.append("        RequestSpecification request = RestAssured.given()\n");
        sb.append("            .log().all()");
        
        // Content type
        if (!contentType.isEmpty()) {
            sb.append("\n");
            if (contentType.toLowerCase().contains("json")) {
                sb.append("            .contentType(ContentType.JSON)");
            } else if (contentType.toLowerCase().contains("xml")) {
                sb.append("            .contentType(ContentType.XML)");
            } else if (contentType.toLowerCase().contains("text")) {
                sb.append("            .contentType(ContentType.TEXT)");
            } else if (contentType.toLowerCase().contains("form")) {
                sb.append("            .contentType(ContentType.URLENC)");
            } else {
                sb.append("            .contentType(\"").append(contentType).append("\")");
            }
        }
        
        // Process test steps
        boolean hasRequestBody = false;
        for (TestCase.TestStep step : testCase.getSteps()) {
            String action = step.getAction().toLowerCase();
            Map<String, String> testData = step.getTestData();
            
            // Process headers
            if (action.contains("set header") || action.contains("add header")) {
                String headerName = testData.getOrDefault("name", "");
                String headerValue = testData.getOrDefault("value", "");
                
                if (!headerName.isEmpty()) {
                    sb.append("\n            .header(\"").append(headerName).append("\", \"")
                      .append(headerValue).append("\")");
                }
            }
            
            // Process query parameters
            else if (action.contains("query param") || action.contains("parameter")) {
                String paramName = testData.getOrDefault("name", "");
                String paramValue = testData.getOrDefault("value", "");
                
                if (!paramName.isEmpty()) {
                    sb.append("\n            .queryParam(\"").append(paramName).append("\", \"")
                      .append(paramValue).append("\")");
                }
            }
            
            // Process request body
            else if (action.contains("body") || action.contains("payload")) {
                String body = testData.getOrDefault("body", "");
                
                if (!body.isEmpty()) {
                    hasRequestBody = true;
                    sb.append("\n\n        // Set request body\n");
                    sb.append("        String requestBody = ");
                    
                    // For multi-line bodies, use Java text blocks (Java 15+) or concatenate strings
                    if (body.contains("\n")) {
                        // Using regular string concatenation for better compatibility
                        String[] lines = body.split("\n");
                        sb.append("\"").append(escapeStringForJava(lines[0])).append("\"");
                        for (int i = 1; i < lines.length; i++) {
                            sb.append("\n            + \"").append(escapeStringForJava(lines[i])).append("\"");
                        }
                    } else {
                        sb.append("\"").append(escapeStringForJava(body)).append("\"");
                    }
                    sb.append(";\n");
                    sb.append("        request = request.body(requestBody);\n");
                }
            }
            
            // Process form parameters
            else if (action.contains("form") || action.contains("form param")) {
                for (Map.Entry<String, String> entry : testData.entrySet()) {
                    if (!entry.getKey().equals("action") && !entry.getKey().equals("description")) {
                        sb.append("\n            .formParam(\"").append(entry.getKey()).append("\", \"")
                          .append(entry.getValue()).append("\")");
                    }
                }
            }
        }
        
        // Execute the request
        sb.append("\n\n        // Send the request and get response\n");
        sb.append("        Response response = request.");
        
        switch (method.toUpperCase()) {
            case "GET":
                sb.append("get(\"").append(url).append("\");\n");
                break;
            case "POST":
                sb.append("post(\"").append(url).append("\");\n");
                break;
            case "PUT":
                sb.append("put(\"").append(url).append("\");\n");
                break;
            case "DELETE":
                sb.append("delete(\"").append(url).append("\");\n");
                break;
            case "PATCH":
                sb.append("patch(\"").append(url).append("\");\n");
                break;
            case "HEAD":
                sb.append("head(\"").append(url).append("\");\n");
                break;
            case "OPTIONS":
                sb.append("options(\"").append(url).append("\");\n");
                break;
            default:
                sb.append("get(\"").append(url).append("\");\n");
                break;
        }
        
        // Log the response
        sb.append("        response.then().log().all();\n\n");
        
        // Add assertions
        sb.append("        // Assertions\n");
        
        // Default expected status code based on HTTP method
        int expectedStatus = 200;
        if (method.toUpperCase().equals("POST")) {
            expectedStatus = 201;
        } else if (method.toUpperCase().equals("DELETE")) {
            expectedStatus = 204;
        }
        
        // Override with specific expected status if available
        if (metadata.containsKey("expectedStatus")) {
            try {
                expectedStatus = Integer.parseInt(metadata.get("expectedStatus"));
            } catch (NumberFormatException e) {
                // Keep default status
            }
        }
        
        sb.append("        Assert.assertEquals(\"Status code should be ").append(expectedStatus).append("\", ")
          .append(expectedStatus).append(", response.getStatusCode());\n\n");
        
        // Add custom assertions from test steps
        for (TestCase.TestStep step : testCase.getSteps()) {
            if (step.getAction().toLowerCase().contains("verify") || 
                step.getAction().toLowerCase().contains("assert") || 
                step.getAction().toLowerCase().contains("validate")) {
                
                Map<String, String> testData = step.getTestData();
                String jsonPath = testData.getOrDefault("jsonPath", "");
                String expectedValue = testData.getOrDefault("expectedValue", "");
                
                if (!jsonPath.isEmpty()) {
                    sb.append("        // Verify response field: ").append(jsonPath).append("\n");
                    
                    // Different assertion based on expected value type
                    if (expectedValue.equalsIgnoreCase("true") || expectedValue.equalsIgnoreCase("false")) {
                        sb.append("        Boolean expected").append(capitalize(jsonPath)).append(" = ")
                          .append(expectedValue.toLowerCase()).append(";\n");
                        sb.append("        Assert.assertEquals(expected").append(capitalize(jsonPath))
                          .append(", response.jsonPath().getBoolean(\"").append(jsonPath).append("\"));\n\n");
                    } else if (expectedValue.matches("\\d+") && !expectedValue.contains("\"")) {
                        sb.append("        int expected").append(capitalize(jsonPath)).append(" = ")
                          .append(expectedValue).append(";\n");
                        sb.append("        Assert.assertEquals(expected").append(capitalize(jsonPath))
                          .append(", response.jsonPath().getInt(\"").append(jsonPath).append("\"));\n\n");
                    } else {
                        sb.append("        String expected").append(capitalize(jsonPath)).append(" = \"")
                          .append(expectedValue).append("\";\n");
                        sb.append("        Assert.assertEquals(expected").append(capitalize(jsonPath))
                          .append(", response.jsonPath().getString(\"").append(jsonPath).append("\"));\n\n");
                    }
                }
            }
        }
        
        // Close method
        sb.append("    }\n\n");
    }
    
    /**
     * Escapes a string for use in Java string literals.
     *
     * @param input String to escape
     * @return Escaped string
     */
    private String escapeStringForJava(String input) {
        return input.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }
    
    /**
     * Capitalizes the first letter of a string.
     *
     * @param input Input string
     * @return String with first letter capitalized
     */
    private String capitalize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return Character.toUpperCase(input.charAt(0)) + input.substring(1);
    }
}
