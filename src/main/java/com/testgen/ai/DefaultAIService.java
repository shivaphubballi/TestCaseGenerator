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
    public TestCase enhanceTestCase(TestCase testCase) {
        // Create a copy of the test case
        TestCase enhancedTestCase = new TestCase(
                testCase.getName() + " (Enhanced)",
                testCase.getDescription(),
                testCase.getType()
        );
        
        // Copy all existing steps
        for (TestStep step : testCase.getSteps()) {
            enhancedTestCase.addStep(new TestStep(step.getDescription(), step.getExpectedResult()));
        }
        
        // Add additional steps based on the test case type
        switch (testCase.getType()) {
            case WEB_UI:
                enhanceWebUITestCase(enhancedTestCase);
                break;
            case API:
                enhanceAPITestCase(enhancedTestCase);
                break;
            case SECURITY:
                enhanceSecurityTestCase(enhancedTestCase);
                break;
            case ACCESSIBILITY:
                enhanceAccessibilityTestCase(enhancedTestCase);
                break;
            case PERFORMANCE:
                enhancePerformanceTestCase(enhancedTestCase);
                break;
        }
        
        return enhancedTestCase;
    }
    
    @Override
    public List<TestCase> generateSecurityTestCases(List<TestCase> baseTestCases, String url) {
        List<TestCase> securityTestCases = new ArrayList<>();
        
        // XSS Test
        TestCase xssTest = new TestCase(
                "XSS Security Test - " + url,
                "Test for Cross-Site Scripting vulnerabilities on " + url,
                TestCase.TestType.SECURITY
        );
        
        xssTest.addStep(new TestStep(
                "Navigate to the target page " + url,
                "Page loads successfully"
        ));
        
        xssTest.addStep(new TestStep(
                "Identify input fields on the page",
                "Input fields should be present"
        ));
        
        xssTest.addStep(new TestStep(
                "Enter XSS payload (<script>alert('XSS')</script>) in each input field",
                "Input should be sanitized or rejected"
        ));
        
        xssTest.addStep(new TestStep(
                "Submit the form or trigger the action",
                "The script should not execute and no alert should appear"
        ));
        
        securityTestCases.add(xssTest);
        
        // SQL Injection Test
        TestCase sqlInjectionTest = new TestCase(
                "SQL Injection Security Test - " + url,
                "Test for SQL Injection vulnerabilities on " + url,
                TestCase.TestType.SECURITY
        );
        
        sqlInjectionTest.addStep(new TestStep(
                "Navigate to the target page " + url,
                "Page loads successfully"
        ));
        
        sqlInjectionTest.addStep(new TestStep(
                "Identify input fields on the page",
                "Input fields should be present"
        ));
        
        sqlInjectionTest.addStep(new TestStep(
                "Enter SQL Injection payload (e.g., ' OR '1'='1) in each input field",
                "Input should be sanitized or rejected"
        ));
        
        sqlInjectionTest.addStep(new TestStep(
                "Submit the form or trigger the action",
                "The application should not reveal database errors or unexpected data"
        ));
        
        securityTestCases.add(sqlInjectionTest);
        
        // CSRF Test
        TestCase csrfTest = new TestCase(
                "CSRF Security Test - " + url,
                "Test for Cross-Site Request Forgery vulnerabilities on " + url,
                TestCase.TestType.SECURITY
        );
        
        csrfTest.addStep(new TestStep(
                "Log in to the application",
                "Login should be successful"
        ));
        
        csrfTest.addStep(new TestStep(
                "Inspect form submissions for CSRF tokens",
                "Forms should include CSRF tokens"
        ));
        
        csrfTest.addStep(new TestStep(
                "Attempt to submit a form without the CSRF token",
                "The submission should be rejected"
        ));
        
        securityTestCases.add(csrfTest);
        
        return securityTestCases;
    }
    
    @Override
    public List<TestCase> generateAccessibilityTestCases(List<TestCase> baseTestCases, String url) {
        List<TestCase> accessibilityTestCases = new ArrayList<>();
        
        // Keyboard Navigation Test
        TestCase keyboardTest = new TestCase(
                "Keyboard Navigation Test - " + url,
                "Test keyboard accessibility on " + url,
                TestCase.TestType.ACCESSIBILITY
        );
        
        keyboardTest.addStep(new TestStep(
                "Navigate to the target page " + url,
                "Page loads successfully"
        ));
        
        keyboardTest.addStep(new TestStep(
                "Press Tab key repeatedly to navigate through all interactive elements",
                "Focus should move visibly from one interactive element to the next"
        ));
        
        keyboardTest.addStep(new TestStep(
                "Press Enter key on focused buttons and links",
                "The appropriate action should be triggered"
        ));
        
        keyboardTest.addStep(new TestStep(
                "Press Space key on focused checkboxes and radio buttons",
                "The state of the control should change"
        ));
        
        accessibilityTestCases.add(keyboardTest);
        
        // Screen Reader Test
        TestCase screenReaderTest = new TestCase(
                "Screen Reader Compatibility Test - " + url,
                "Test screen reader compatibility on " + url,
                TestCase.TestType.ACCESSIBILITY
        );
        
        screenReaderTest.addStep(new TestStep(
                "Navigate to the target page " + url,
                "Page loads successfully"
        ));
        
        screenReaderTest.addStep(new TestStep(
                "Check if all images have alt text",
                "All images should have descriptive alt text"
        ));
        
        screenReaderTest.addStep(new TestStep(
                "Check if form fields have associated labels",
                "All form fields should have explicit labels"
        ));
        
        screenReaderTest.addStep(new TestStep(
                "Check if headings are properly structured (h1, h2, etc.)",
                "Headings should follow a logical hierarchy"
        ));
        
        accessibilityTestCases.add(screenReaderTest);
        
        // Color Contrast Test
        TestCase contrastTest = new TestCase(
                "Color Contrast Test - " + url,
                "Test color contrast for accessibility on " + url,
                TestCase.TestType.ACCESSIBILITY
        );
        
        contrastTest.addStep(new TestStep(
                "Navigate to the target page " + url,
                "Page loads successfully"
        ));
        
        contrastTest.addStep(new TestStep(
                "Check text color against background color",
                "Text should have sufficient contrast ratio (at least 4.5:1 for normal text)"
        ));
        
        contrastTest.addStep(new TestStep(
                "Check link color against background color",
                "Links should be distinguishable from surrounding text"
        ));
        
        contrastTest.addStep(new TestStep(
                "Check form field borders and labels",
                "Form fields should have visible borders or labels"
        ));
        
        accessibilityTestCases.add(contrastTest);
        
        return accessibilityTestCases;
    }
    
    @Override
    public List<TestCase> generatePerformanceTestCases(List<TestCase> baseTestCases, String url) {
        List<TestCase> performanceTestCases = new ArrayList<>();
        
        // Page Load Time Test
        TestCase pageLoadTest = new TestCase(
                "Page Load Time Test - " + url,
                "Test page load performance on " + url,
                TestCase.TestType.PERFORMANCE
        );
        
        pageLoadTest.addStep(new TestStep(
                "Measure time to navigate to " + url,
                "Page should load within 3 seconds"
        ));
        
        pageLoadTest.addStep(new TestStep(
                "Check time to first byte (TTFB)",
                "TTFB should be less than 200ms"
        ));
        
        pageLoadTest.addStep(new TestStep(
                "Check time to first contentful paint",
                "First contentful paint should occur within 1 second"
        ));
        
        pageLoadTest.addStep(new TestStep(
                "Check time to interactive",
                "Page should be interactive within 5 seconds"
        ));
        
        performanceTestCases.add(pageLoadTest);
        
        // Resource Loading Test
        TestCase resourceTest = new TestCase(
                "Resource Loading Test - " + url,
                "Test resource loading performance on " + url,
                TestCase.TestType.PERFORMANCE
        );
        
        resourceTest.addStep(new TestStep(
                "Check the number of HTTP requests",
                "The number of requests should be minimized (less than 50)"
        ));
        
        resourceTest.addStep(new TestStep(
                "Check the size of resources (images, CSS, JavaScript)",
                "Total page size should be less than 3MB"
        ));
        
        resourceTest.addStep(new TestStep(
                "Check if resources are cached properly",
                "Resources should have appropriate cache headers"
        ));
        
        resourceTest.addStep(new TestStep(
                "Check if resources are compressed",
                "Text-based resources should be gzipped"
        ));
        
        performanceTestCases.add(resourceTest);
        
        // Interaction Response Time Test
        TestCase interactionTest = new TestCase(
                "Interaction Response Time Test - " + url,
                "Test response time for user interactions on " + url,
                TestCase.TestType.PERFORMANCE
        );
        
        interactionTest.addStep(new TestStep(
                "Measure response time for button clicks",
                "Response should be within 100ms"
        ));
        
        interactionTest.addStep(new TestStep(
                "Measure response time for form submissions",
                "Response should be within 1 second"
        ));
        
        interactionTest.addStep(new TestStep(
                "Check for visual feedback during loading",
                "Loading indicators should be present for operations taking more than 1 second"
        ));
        
        performanceTestCases.add(interactionTest);
        
        return performanceTestCases;
    }
    
    @Override
    public List<TestCase> generateSPATestCases(List<WebElement> elements, List<TestCase> baseTestCases, String url) {
        List<TestCase> spaTestCases = new ArrayList<>(baseTestCases);
        
        // Route Navigation Test
        TestCase routeTest = new TestCase(
                "SPA Route Navigation Test - " + url,
                "Test navigation between routes in the SPA",
                TestCase.TestType.WEB_UI
        );
        
        routeTest.addStep(new TestStep(
                "Navigate to the SPA at " + url,
                "Application should load successfully"
        ));
        
        routeTest.addStep(new TestStep(
                "Identify navigation links or menu items",
                "Navigation elements should be present"
        ));
        
        routeTest.addStep(new TestStep(
                "Click on each navigation link",
                "URL should change (hash or path) and corresponding content should update without page reload"
        ));
        
        routeTest.addStep(new TestStep(
                "Check browser history",
                "Browser back/forward buttons should navigate between SPA routes"
        ));
        
        spaTestCases.add(routeTest);
        
        // Dynamic Content Loading Test
        TestCase dynamicContentTest = new TestCase(
                "SPA Dynamic Content Loading Test - " + url,
                "Test loading of dynamic content in the SPA",
                TestCase.TestType.WEB_UI
        );
        
        dynamicContentTest.addStep(new TestStep(
                "Navigate to the SPA at " + url,
                "Application should load successfully"
        ));
        
        dynamicContentTest.addStep(new TestStep(
                "Identify elements that trigger dynamic content loading",
                "Such elements should be present (e.g., 'Load More' buttons, infinite scroll, tabs)"
        ));
        
        dynamicContentTest.addStep(new TestStep(
                "Trigger dynamic content loading",
                "Loading indicator should appear and then new content should be displayed"
        ));
        
        dynamicContentTest.addStep(new TestStep(
                "Check that existing content is preserved",
                "Previously loaded content should remain visible alongside newly loaded content"
        ));
        
        spaTestCases.add(dynamicContentTest);
        
        // State Persistence Test
        TestCase stateTest = new TestCase(
                "SPA State Persistence Test - " + url,
                "Test persistence of application state in the SPA",
                TestCase.TestType.WEB_UI
        );
        
        stateTest.addStep(new TestStep(
                "Navigate to the SPA at " + url,
                "Application should load successfully"
        ));
        
        stateTest.addStep(new TestStep(
                "Interact with the application to change its state",
                "Application state should update accordingly"
        ));
        
        stateTest.addStep(new TestStep(
                "Navigate to a different route and then back",
                "Previous state should be preserved"
        ));
        
        stateTest.addStep(new TestStep(
                "Refresh the browser",
                "Critical application state should be restored (if applicable)"
        ));
        
        spaTestCases.add(stateTest);
        
        // Form Validation and Submission Test
        boolean hasForm = elements.stream().anyMatch(e -> "form".equals(e.getTagName()));
        if (hasForm) {
            TestCase formTest = new TestCase(
                    "SPA Form Handling Test - " + url,
                    "Test form validation and submission in the SPA",
                    TestCase.TestType.WEB_UI
            );
            
            formTest.addStep(new TestStep(
                    "Navigate to a form in the SPA",
                    "Form should be displayed"
            ));
            
            formTest.addStep(new TestStep(
                    "Submit the form without required fields",
                    "Form validation should trigger and prevent submission"
            ));
            
            formTest.addStep(new TestStep(
                    "Fill in the form with valid data",
                    "Validation should pass"
            ));
            
            formTest.addStep(new TestStep(
                    "Submit the form",
                    "Form should be submitted via AJAX without page reload and success feedback should be displayed"
            ));
            
            spaTestCases.add(formTest);
        }
        
        return spaTestCases;
    }
    
    // Helper methods
    
    private void enhanceWebUITestCase(TestCase testCase) {
        // Add edge case checks for web UI test cases
        testCase.addStep(new TestStep(
                "Verify behavior with different browser window sizes",
                "UI should adapt responsively to different screen sizes"
        ));
        
        testCase.addStep(new TestStep(
                "Check for error handling on invalid inputs",
                "Appropriate error messages should be displayed"
        ));
    }
    
    private void enhanceAPITestCase(TestCase testCase) {
        // Add edge case checks for API test cases
        testCase.addStep(new TestStep(
                "Send request with invalid authentication",
                "API should return 401 Unauthorized status"
        ));
        
        testCase.addStep(new TestStep(
                "Verify response headers and content type",
                "Response should have appropriate headers and content type"
        ));
    }
    
    private void enhanceSecurityTestCase(TestCase testCase) {
        // Add additional security checks
        testCase.addStep(new TestStep(
                "Verify secure HTTP headers",
                "Response should include security-related headers like Content-Security-Policy"
        ));
    }
    
    private void enhanceAccessibilityTestCase(TestCase testCase) {
        // Add additional accessibility checks
        testCase.addStep(new TestStep(
                "Verify page structure with ARIA landmarks",
                "Page should have appropriate ARIA roles and landmarks"
        ));
    }
    
    private void enhancePerformanceTestCase(TestCase testCase) {
        // Add additional performance checks
        testCase.addStep(new TestStep(
                "Measure memory usage during extended use",
                "Memory usage should remain stable over time"
        ));
    }
    
    private List<WebElement> findFormElements(List<WebElement> elements) {
        List<WebElement> formElements = new ArrayList<>();
        for (WebElement element : elements) {
            if ("form".equals(element.getTagName()) || 
                "input".equals(element.getTagName()) || 
                "textarea".equals(element.getTagName()) || 
                "select".equals(element.getTagName()) || 
                "button".equals(element.getTagName())) {
                formElements.add(element);
            }
        }
        return formElements;
    }
    
    private List<WebElement> findNavigationElements(List<WebElement> elements) {
        List<WebElement> navElements = new ArrayList<>();
        for (WebElement element : elements) {
            if ("a".equals(element.getTagName()) || 
                "nav".equals(element.getTagName()) || 
                element.getClassName() != null && (
                    element.getClassName().contains("nav") || 
                    element.getClassName().contains("menu")
                )) {
                navElements.add(element);
            }
        }
        return navElements;
    }
    
    private TestCase createFormValidationTestCase(List<WebElement> formElements) {
        TestCase testCase = new TestCase(
                "Form Validation Test",
                "Test form validation for input fields",
                TestCase.TestType.WEB_UI
        );
        
        testCase.addStep(new TestStep(
                "Submit the form without filling any fields",
                "Required field validation errors should be displayed"
        ));
        
        testCase.addStep(new TestStep(
                "Enter invalid data in each field (e.g., non-email in email field)",
                "Format validation errors should be displayed"
        ));
        
        testCase.addStep(new TestStep(
                "Enter valid data in all fields",
                "No validation errors should be displayed"
        ));
        
        return testCase;
    }
    
    private TestCase createNavigationFlowTestCase(List<WebElement> navigationElements) {
        TestCase testCase = new TestCase(
                "Navigation Flow Test",
                "Test navigation flow between pages",
                TestCase.TestType.WEB_UI
        );
        
        testCase.addStep(new TestStep(
                "Click on main navigation links",
                "User should be taken to the correct pages"
        ));
        
        testCase.addStep(new TestStep(
                "Verify the browser's back button behavior",
                "User should be taken to the previous page"
        ));
        
        testCase.addStep(new TestStep(
                "Verify the browser's forward button behavior",
                "User should be taken to the next page"
        ));
        
        return testCase;
    }
    
    private List<TestCase> suggestAccessibilityTests(List<WebElement> elements) {
        List<TestCase> accessibilityTests = new ArrayList<>();
        
        // Keyboard navigation test
        TestCase keyboardTest = new TestCase(
                "Keyboard Navigation Test",
                "Test keyboard accessibility",
                TestCase.TestType.ACCESSIBILITY
        );
        
        keyboardTest.addStep(new TestStep(
                "Navigate through the page using only the Tab key",
                "All interactive elements should be focusable"
        ));
        
        keyboardTest.addStep(new TestStep(
                "Interact with focused elements using keyboard (Enter, Space)",
                "Elements should respond to keyboard interactions"
        ));
        
        accessibilityTests.add(keyboardTest);
        
        return accessibilityTests;
    }
    
    private List<TestCase> suggestWebSecurityTests(List<WebElement> elements) {
        List<TestCase> securityTests = new ArrayList<>();
        
        // Input validation test
        TestCase inputValidationTest = new TestCase(
                "Input Validation Test",
                "Test input field validation for security",
                TestCase.TestType.SECURITY
        );
        
        inputValidationTest.addStep(new TestStep(
                "Enter script tags in text fields",
                "Script should not be executed"
        ));
        
        inputValidationTest.addStep(new TestStep(
                "Enter SQL injection patterns in text fields",
                "Input should be sanitized or rejected"
        ));
        
        securityTests.add(inputValidationTest);
        
        return securityTests;
    }
    
    private List<TestCase> generateWebElementEdgeCases(WebElement element) {
        List<TestCase> edgeCaseTests = new ArrayList<>();
        
        // For input elements, generate edge cases
        if ("input".equals(element.getTagName())) {
            TestCase inputEdgeCaseTest = new TestCase(
                    "Input Edge Case Test for " + element.getId(),
                    "Test edge cases for input field",
                    TestCase.TestType.WEB_UI
            );
            
            inputEdgeCaseTest.addStep(new TestStep(
                    "Enter very long text in the input field",
                    "Input should be handled gracefully (truncated or scrollable)"
            ));
            
            inputEdgeCaseTest.addStep(new TestStep(
                    "Enter special characters in the input field",
                    "Input should be handled correctly"
            ));
            
            edgeCaseTests.add(inputEdgeCaseTest);
        }
        
        return edgeCaseTests;
    }
}
