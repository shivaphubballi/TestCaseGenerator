package com.testgen.ai;

import com.testgen.model.ApiEndpoint;
import com.testgen.model.TestCase;
import com.testgen.model.TestCase.TestStep;
import com.testgen.model.TestCase.TestType;
import com.testgen.model.WebElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of the AIService interface.
 * Uses rule-based heuristics to enhance test cases.
 * Can be extended to use external AI services in the future.
 */
public class DefaultAIService implements AIService {

    @Override
    public List<TestCase> enhanceWebTestCases(List<WebElement> elements, List<TestCase> baseTestCases) {
        List<TestCase> enhancedTestCases = new ArrayList<>(baseTestCases);
        
        // Add test cases for form validation
        List<WebElement> formElements = findFormElements(elements);
        if (!formElements.isEmpty()) {
            enhancedTestCases.add(createFormValidationTestCase(formElements));
        }
        
        // Add test cases for navigation flows
        List<WebElement> navigationElements = findNavigationElements(elements);
        if (!navigationElements.isEmpty()) {
            enhancedTestCases.add(createNavigationFlowTestCase(navigationElements));
        }
        
        // Add accessibility tests
        enhancedTestCases.addAll(suggestAccessibilityTests(elements));
        
        // Add security tests
        enhancedTestCases.addAll(suggestWebSecurityTests(elements));
        
        // Add edge cases for each element
        for (WebElement element : elements) {
            enhancedTestCases.addAll(generateWebElementEdgeCases(element));
        }
        
        return enhancedTestCases;
    }

    @Override
    public List<TestCase> enhanceApiTestCases(List<ApiEndpoint> endpoints, List<TestCase> baseTestCases) {
        List<TestCase> enhancedTestCases = new ArrayList<>(baseTestCases);
        
        // Add test cases for common API scenarios
        enhancedTestCases.add(createApiAuthenticationTestCase(endpoints));
        enhancedTestCases.add(createApiPerformanceTestCase(endpoints));
        
        // Add security tests
        enhancedTestCases.addAll(suggestApiSecurityTests(endpoints));
        
        // Add edge cases for each endpoint
        for (ApiEndpoint endpoint : endpoints) {
            enhancedTestCases.addAll(generateApiEndpointEdgeCases(endpoint));
        }
        
        return enhancedTestCases;
    }

    @Override
    public List<TestCase> generateWebElementEdgeCases(WebElement element) {
        List<TestCase> edgeCases = new ArrayList<>();
        
        // Generate different edge cases based on element type
        if ("input".equals(element.getTagName())) {
            if ("text".equals(element.getType())) {
                // Text input edge cases
                edgeCases.add(createInputValidationTestCase(element, "Empty Input", ""));
                edgeCases.add(createInputValidationTestCase(element, "Very Long Input", "A".repeat(1000)));
                edgeCases.add(createInputValidationTestCase(element, "Special Characters", "!@#$%^&*()_+<>?:\"{}|"));
            } else if ("number".equals(element.getType())) {
                // Number input edge cases
                edgeCases.add(createInputValidationTestCase(element, "Negative Number", "-1"));
                edgeCases.add(createInputValidationTestCase(element, "Very Large Number", "9999999999"));
                edgeCases.add(createInputValidationTestCase(element, "Non-numeric Input", "abc"));
            } else if ("email".equals(element.getType())) {
                // Email input edge cases
                edgeCases.add(createInputValidationTestCase(element, "Invalid Email Format", "notanemail"));
                edgeCases.add(createInputValidationTestCase(element, "Valid Email", "test@example.com"));
                edgeCases.add(createInputValidationTestCase(element, "Very Long Email", "a".repeat(100) + "@example.com"));
            }
        } else if ("button".equals(element.getTagName())) {
            // Button edge cases
            edgeCases.add(createButtonTestCase(element, "Rapid Clicking", "Rapidly click the button multiple times"));
        } else if ("select".equals(element.getTagName())) {
            // Select element edge cases
            edgeCases.add(createSelectTestCase(element, "Select First Option", "Select the first option"));
            edgeCases.add(createSelectTestCase(element, "Select Last Option", "Select the last option"));
        }
        
        return edgeCases;
    }

    @Override
    public List<TestCase> generateApiEndpointEdgeCases(ApiEndpoint endpoint) {
        List<TestCase> edgeCases = new ArrayList<>();
        
        // Missing required parameters
        edgeCases.add(createApiTestCase(
            endpoint,
            "Missing Required Parameters",
            "Send request without required parameters",
            "API should return appropriate error response"
        ));
        
        // Invalid parameter values
        edgeCases.add(createApiTestCase(
            endpoint,
            "Invalid Parameter Values",
            "Send request with invalid parameter values",
            "API should return appropriate error response"
        ));
        
        // Rate limiting
        edgeCases.add(createApiTestCase(
            endpoint,
            "Rate Limiting",
            "Send multiple requests in quick succession",
            "API should handle rate limiting appropriately"
        ));
        
        // Large payload
        edgeCases.add(createApiTestCase(
            endpoint,
            "Large Payload",
            "Send request with a very large payload",
            "API should handle large payloads appropriately"
        ));
        
        return edgeCases;
    }

    @Override
    public List<TestCase> suggestWebSecurityTests(List<WebElement> elements) {
        List<TestCase> securityTests = new ArrayList<>();
        
        // XSS vulnerabilities
        securityTests.add(createSecurityTestCase(
            "XSS Vulnerability Test",
            "Test for Cross-Site Scripting vulnerabilities",
            "Enter script tags in input fields",
            "Application should sanitize input and prevent execution of scripts"
        ));
        
        // CSRF vulnerabilities
        securityTests.add(createSecurityTestCase(
            "CSRF Vulnerability Test",
            "Test for Cross-Site Request Forgery vulnerabilities",
            "Attempt to submit forms from external origins",
            "Application should validate request origins and prevent CSRF attacks"
        ));
        
        // SQL Injection
        securityTests.add(createSecurityTestCase(
            "SQL Injection Test",
            "Test for SQL Injection vulnerabilities",
            "Enter SQL commands in input fields",
            "Application should sanitize input and prevent SQL injection"
        ));
        
        // Authentication bypass
        securityTests.add(createSecurityTestCase(
            "Authentication Bypass Test",
            "Test for authentication bypass vulnerabilities",
            "Attempt to access protected resources without authentication",
            "Application should enforce proper authentication"
        ));
        
        return securityTests;
    }

    @Override
    public List<TestCase> suggestApiSecurityTests(List<ApiEndpoint> endpoints) {
        List<TestCase> securityTests = new ArrayList<>();
        
        // Authentication
        securityTests.add(createApiSecurityTestCase(
            "Missing Authentication Test",
            "Test API without authentication",
            "Send request without authentication credentials",
            "API should return appropriate authentication error"
        ));
        
        // Authorization
        securityTests.add(createApiSecurityTestCase(
            "Insufficient Authorization Test",
            "Test API with insufficient permissions",
            "Send request with authentication but insufficient permissions",
            "API should return appropriate authorization error"
        ));
        
        // Injection
        securityTests.add(createApiSecurityTestCase(
            "Injection Test",
            "Test for injection vulnerabilities",
            "Send request with potential injection payloads",
            "API should sanitize input and prevent injection attacks"
        ));
        
        // Parameter tampering
        securityTests.add(createApiSecurityTestCase(
            "Parameter Tampering Test",
            "Test for parameter tampering vulnerabilities",
            "Modify request parameters to attempt unauthorized access",
            "API should validate parameters and prevent tampering"
        ));
        
        return securityTests;
    }

    @Override
    public List<TestCase> suggestAccessibilityTests(List<WebElement> elements) {
        List<TestCase> accessibilityTests = new ArrayList<>();
        
        // Alt text for images
        accessibilityTests.add(createAccessibilityTestCase(
            "Image Alt Text Test",
            "Test alt text for images",
            "Check all images for alt text attributes",
            "All images should have descriptive alt text"
        ));
        
        // Form labels
        accessibilityTests.add(createAccessibilityTestCase(
            "Form Label Test",
            "Test form input labels",
            "Check all form inputs for associated labels",
            "All form inputs should have associated labels"
        ));
        
        // Keyboard navigation
        accessibilityTests.add(createAccessibilityTestCase(
            "Keyboard Navigation Test",
            "Test keyboard navigation",
            "Navigate through the page using only the keyboard",
            "All interactive elements should be accessible via keyboard"
        ));
        
        // Color contrast
        accessibilityTests.add(createAccessibilityTestCase(
            "Color Contrast Test",
            "Test color contrast for readability",
            "Check color contrast between text and background",
            "All text should have sufficient contrast with background"
        ));
        
        return accessibilityTests;
    }

    @Override
    public String analyzeCoverageGaps(List<TestCase> existingTests) {
        StringBuilder analysis = new StringBuilder();
        
        // Check for functional coverage
        Map<String, Integer> functionalCoverage = analyzeFunctionalCoverage(existingTests);
        analysis.append("Functional Coverage Analysis:\n");
        for (Map.Entry<String, Integer> entry : functionalCoverage.entrySet()) {
            analysis.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append(" test cases\n");
        }
        
        // Check for edge case coverage
        analysis.append("\nEdge Case Coverage Analysis:\n");
        int edgeCaseCount = countEdgeCases(existingTests);
        analysis.append("- Edge cases: ").append(edgeCaseCount).append(" test cases\n");
        
        // Check for security coverage
        analysis.append("\nSecurity Coverage Analysis:\n");
        int securityTestCount = countSecurityTests(existingTests);
        analysis.append("- Security tests: ").append(securityTestCount).append(" test cases\n");
        
        // Check for accessibility coverage
        analysis.append("\nAccessibility Coverage Analysis:\n");
        int accessibilityTestCount = countAccessibilityTests(existingTests);
        analysis.append("- Accessibility tests: ").append(accessibilityTestCount).append(" test cases\n");
        
        // Recommendations
        analysis.append("\nRecommendations:\n");
        if (edgeCaseCount < 5) {
            analysis.append("- Increase edge case coverage\n");
        }
        if (securityTestCount < 4) {
            analysis.append("- Increase security test coverage\n");
        }
        if (accessibilityTestCount < 4) {
            analysis.append("- Increase accessibility test coverage\n");
        }
        
        return analysis.toString();
    }
    
    // Helper methods
    
    private List<WebElement> findFormElements(List<WebElement> elements) {
        List<WebElement> formElements = new ArrayList<>();
        for (WebElement element : elements) {
            if ("input".equals(element.getTagName()) || 
                "textarea".equals(element.getTagName()) || 
                "select".equals(element.getTagName()) ||
                "button".equals(element.getTagName())) {
                formElements.add(element);
            }
        }
        return formElements;
    }
    
    private List<WebElement> findNavigationElements(List<WebElement> elements) {
        List<WebElement> navigationElements = new ArrayList<>();
        for (WebElement element : elements) {
            if ("a".equals(element.getTagName()) || 
                ("button".equals(element.getTagName()) && element.getText() != null && 
                 (element.getText().toLowerCase().contains("go") || 
                  element.getText().toLowerCase().contains("next") || 
                  element.getText().toLowerCase().contains("previous") || 
                  element.getText().toLowerCase().contains("submit")))) {
                navigationElements.add(element);
            }
        }
        return navigationElements;
    }
    
    private TestCase createFormValidationTestCase(List<WebElement> formElements) {
        List<TestStep> steps = new ArrayList<>();
        
        steps.add(new TestStep("Navigate to the form page", "Form should be displayed"));
        steps.add(new TestStep("Submit the form without filling any fields", "Form should display validation errors"));
        
        for (WebElement element : formElements) {
            if ("input".equals(element.getTagName()) || "textarea".equals(element.getTagName())) {
                steps.add(new TestStep(
                    "Enter invalid data in the " + (element.getName() != null ? element.getName() : "input") + " field",
                    "Field should display validation error"
                ));
            }
        }
        
        steps.add(new TestStep("Fill all fields with valid data", "All validation errors should be cleared"));
        steps.add(new TestStep("Submit the form", "Form should be submitted successfully"));
        
        return new TestCase(
            "Form Validation Test",
            "Test form validation for required fields and invalid inputs",
            steps,
            TestType.WEB_UI
        );
    }
    
    private TestCase createNavigationFlowTestCase(List<WebElement> navigationElements) {
        List<TestStep> steps = new ArrayList<>();
        
        steps.add(new TestStep("Navigate to the starting page", "Starting page should be displayed"));
        
        for (int i = 0; i < Math.min(navigationElements.size(), 5); i++) {
            WebElement element = navigationElements.get(i);
            steps.add(new TestStep(
                "Click on the " + (element.getText() != null ? element.getText() : "navigation element"),
                "Application should navigate to the appropriate page"
            ));
            steps.add(new TestStep(
                "Verify the new page content",
                "Page content should be appropriate for the navigation target"
            ));
        }
        
        return new TestCase(
            "Navigation Flow Test",
            "Test navigation flow through the application",
            steps,
            TestType.WEB_UI
        );
    }
    
    private TestCase createInputValidationTestCase(WebElement element, String scenario, String input) {
        List<TestStep> steps = new ArrayList<>();
        
        steps.add(new TestStep("Navigate to the page containing the input", "Page should be displayed"));
        steps.add(new TestStep(
            "Enter \"" + input + "\" in the " + (element.getName() != null ? element.getName() : "input") + " field",
            "Input should be entered in the field"
        ));
        steps.add(new TestStep(
            "Submit the form or trigger validation",
            "Application should handle the input appropriately"
        ));
        
        return new TestCase(
            (element.getName() != null ? element.getName() : "Input") + " Validation - " + scenario,
            "Test input validation with " + scenario.toLowerCase(),
            steps,
            TestType.WEB_UI
        );
    }
    
    private TestCase createButtonTestCase(WebElement element, String scenario, String action) {
        List<TestStep> steps = new ArrayList<>();
        
        steps.add(new TestStep("Navigate to the page containing the button", "Page should be displayed"));
        steps.add(new TestStep(
            action,
            "Application should handle the button interaction appropriately"
        ));
        
        return new TestCase(
            (element.getText() != null ? element.getText() : "Button") + " Test - " + scenario,
            "Test button interaction with " + scenario.toLowerCase(),
            steps,
            TestType.WEB_UI
        );
    }
    
    private TestCase createSelectTestCase(WebElement element, String scenario, String action) {
        List<TestStep> steps = new ArrayList<>();
        
        steps.add(new TestStep("Navigate to the page containing the select element", "Page should be displayed"));
        steps.add(new TestStep(
            action,
            "Option should be selected"
        ));
        steps.add(new TestStep(
            "Submit the form or trigger an action",
            "Application should handle the selection appropriately"
        ));
        
        return new TestCase(
            (element.getName() != null ? element.getName() : "Select") + " Test - " + scenario,
            "Test select element interaction with " + scenario.toLowerCase(),
            steps,
            TestType.WEB_UI
        );
    }
    
    private TestCase createApiTestCase(ApiEndpoint endpoint, String name, String action, String expectedResult) {
        List<TestStep> steps = new ArrayList<>();
        
        steps.add(new TestStep(
            "Prepare " + endpoint.getMethod() + " request to " + endpoint.getUrl(),
            "Request should be prepared with appropriate headers"
        ));
        steps.add(new TestStep(
            action,
            expectedResult
        ));
        
        return new TestCase(
            endpoint.getMethod() + " " + endpoint.getUrl() + " - " + name,
            "Test " + endpoint.getMethod() + " " + endpoint.getUrl() + " with " + name.toLowerCase(),
            steps,
            TestType.API
        );
    }
    
    private TestCase createApiAuthenticationTestCase(List<ApiEndpoint> endpoints) {
        List<TestStep> steps = new ArrayList<>();
        
        steps.add(new TestStep(
            "Authenticate with valid credentials",
            "Authentication should succeed and return valid token"
        ));
        
        if (!endpoints.isEmpty()) {
            ApiEndpoint endpoint = endpoints.get(0);
            steps.add(new TestStep(
                "Send " + endpoint.getMethod() + " request to " + endpoint.getUrl() + " with authentication token",
                "Request should succeed with appropriate response"
            ));
            steps.add(new TestStep(
                "Send the same request with invalid token",
                "Request should fail with authentication error"
            ));
            steps.add(new TestStep(
                "Send the same request with expired token",
                "Request should fail with authentication error or refresh token should be used"
            ));
        }
        
        return new TestCase(
            "API Authentication Test",
            "Test API authentication with various token scenarios",
            steps,
            TestType.API
        );
    }
    
    private TestCase createApiPerformanceTestCase(List<ApiEndpoint> endpoints) {
        List<TestStep> steps = new ArrayList<>();
        
        if (!endpoints.isEmpty()) {
            ApiEndpoint endpoint = endpoints.get(0);
            steps.add(new TestStep(
                "Send " + endpoint.getMethod() + " request to " + endpoint.getUrl() + " and measure response time",
                "Response time should be within acceptable limits"
            ));
            steps.add(new TestStep(
                "Send multiple concurrent requests to " + endpoint.getUrl(),
                "All requests should be handled appropriately with reasonable response times"
            ));
        }
        
        return new TestCase(
            "API Performance Test",
            "Test API performance under load",
            steps,
            TestType.API
        );
    }
    
    private TestCase createSecurityTestCase(String name, String description, String action, String expectedResult) {
        List<TestStep> steps = new ArrayList<>();
        
        steps.add(new TestStep(
            action,
            expectedResult
        ));
        
        return new TestCase(
            name,
            description,
            steps,
            TestType.SECURITY
        );
    }
    
    private TestCase createApiSecurityTestCase(String name, String description, String action, String expectedResult) {
        List<TestStep> steps = new ArrayList<>();
        
        steps.add(new TestStep(
            action,
            expectedResult
        ));
        
        return new TestCase(
            name,
            description,
            steps,
            TestType.SECURITY
        );
    }
    
    private TestCase createAccessibilityTestCase(String name, String description, String action, String expectedResult) {
        List<TestStep> steps = new ArrayList<>();
        
        steps.add(new TestStep(
            action,
            expectedResult
        ));
        
        return new TestCase(
            name,
            description,
            steps,
            TestType.ACCESSIBILITY
        );
    }
    
    private Map<String, Integer> analyzeFunctionalCoverage(List<TestCase> testCases) {
        Map<String, Integer> coverage = new HashMap<>();
        coverage.put("Web UI Tests", 0);
        coverage.put("API Tests", 0);
        coverage.put("Security Tests", 0);
        coverage.put("Accessibility Tests", 0);
        
        for (TestCase testCase : testCases) {
            if (testCase.getType() == TestType.WEB_UI) {
                coverage.put("Web UI Tests", coverage.get("Web UI Tests") + 1);
            } else if (testCase.getType() == TestType.API) {
                coverage.put("API Tests", coverage.get("API Tests") + 1);
            } else if (testCase.getType() == TestType.SECURITY) {
                coverage.put("Security Tests", coverage.get("Security Tests") + 1);
            } else if (testCase.getType() == TestType.ACCESSIBILITY) {
                coverage.put("Accessibility Tests", coverage.get("Accessibility Tests") + 1);
            }
        }
        
        return coverage;
    }
    
    private int countEdgeCases(List<TestCase> testCases) {
        int count = 0;
        for (TestCase testCase : testCases) {
            String name = testCase.getName().toLowerCase();
            if (name.contains("edge case") || 
                name.contains("validation") || 
                name.contains("invalid") || 
                name.contains("empty") || 
                name.contains("large")) {
                count++;
            }
        }
        return count;
    }
    
    private int countSecurityTests(List<TestCase> testCases) {
        int count = 0;
        for (TestCase testCase : testCases) {
            if (testCase.getType() == TestType.SECURITY ||
                testCase.getName().toLowerCase().contains("security") ||
                testCase.getName().toLowerCase().contains("xss") ||
                testCase.getName().toLowerCase().contains("csrf") ||
                testCase.getName().toLowerCase().contains("injection") ||
                testCase.getName().toLowerCase().contains("authentication") ||
                testCase.getName().toLowerCase().contains("authorization")) {
                count++;
            }
        }
        return count;
    }
    
    private int countAccessibilityTests(List<TestCase> testCases) {
        int count = 0;
        for (TestCase testCase : testCases) {
            if (testCase.getType() == TestType.ACCESSIBILITY ||
                testCase.getName().toLowerCase().contains("accessibility") ||
                testCase.getName().toLowerCase().contains("alt text") ||
                testCase.getName().toLowerCase().contains("label") ||
                testCase.getName().toLowerCase().contains("keyboard") ||
                testCase.getName().toLowerCase().contains("contrast")) {
                count++;
            }
        }
        return count;
    }
    @Override
    public List<TestCase> suggestPerformanceTests(List<WebElement> elements) {
        List<TestCase> performanceTests = new ArrayList<>();
        
        // Page load time test
        performanceTests.add(createPerformanceTestCase(
            "Page Load Time Test",
            "Test page load time",
            "Measure the time it takes for the page to load completely",
            "Page should load within acceptable time thresholds"
        ));
        
        // Resource loading test
        performanceTests.add(createPerformanceTestCase(
            "Resource Loading Test",
            "Test loading time for page resources",
            "Measure loading time for images, CSS, JavaScript, and other resources",
            "Resources should load within acceptable time thresholds"
        ));
        
        // Form submission performance
        performanceTests.add(createPerformanceTestCase(
            "Form Submission Performance Test",
            "Test performance of form submissions",
            "Measure the time it takes to submit forms and receive responses",
            "Form submissions should be processed within acceptable time thresholds"
        ));
        
        // UI responsiveness test
        performanceTests.add(createPerformanceTestCase(
            "UI Responsiveness Test",
            "Test UI responsiveness",
            "Interact with UI elements and measure response time",
            "UI should remain responsive during user interactions"
        ));
        
        // Concurrent users test
        performanceTests.add(createPerformanceTestCase(
            "Concurrent Users Test",
            "Test performance under concurrent user load",
            "Simulate multiple concurrent users interacting with the page",
            "Page should maintain responsiveness under moderate user load"
        ));
        
        return performanceTests;
    }

    @Override
    public List<TestCase> suggestApiPerformanceTests(List<ApiEndpoint> endpoints) {
        List<TestCase> performanceTests = new ArrayList<>();
        
        // Response time test
        performanceTests.add(createApiPerformanceTestCase(
            "API Response Time Test",
            "Test API response time",
            "Measure the time it takes for the API to respond to requests",
            "API should respond within acceptable time thresholds"
        ));
        
        // Throughput test
        performanceTests.add(createApiPerformanceTestCase(
            "API Throughput Test",
            "Test API throughput",
            "Measure the number of requests the API can handle per unit of time",
            "API should handle a minimum number of requests per second"
        ));
        
        // Concurrent requests test
        performanceTests.add(createApiPerformanceTestCase(
            "Concurrent Requests Test",
            "Test API performance under concurrent requests",
            "Send multiple concurrent requests to the API",
            "API should maintain response time under concurrent load"
        ));
        
        // Data volume test
        performanceTests.add(createApiPerformanceTestCase(
            "Data Volume Test",
            "Test API performance with large data volumes",
            "Send requests with large data payloads and retrieve large responses",
            "API should handle large data volumes efficiently"
        ));
        
        // Long-running request test
        performanceTests.add(createApiPerformanceTestCase(
            "Long-Running Request Test",
            "Test API performance for long-running operations",
            "Initiate long-running operations and measure performance",
            "API should efficiently handle long-running operations"
        ));
        
        return performanceTests;
    }
    
    private TestCase createPerformanceTestCase(String name, String description, String stepDescription, String expectedResult) {
        List<TestStep> steps = new ArrayList<>();
        steps.add(new TestStep(stepDescription, expectedResult));
        
        return new TestCase(
            name,
            description,
            steps,
            TestType.PERFORMANCE
        );
    }
    
    private TestCase createApiPerformanceTestCase(String name, String description, String stepDescription, String expectedResult) {
        List<TestStep> steps = new ArrayList<>();
        steps.add(new TestStep(stepDescription, expectedResult));
        
        return new TestCase(
            name,
            description,
            steps,
            TestType.PERFORMANCE
        );
    }
}
