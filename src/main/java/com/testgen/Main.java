package com.testgen;

import com.testgen.model.ApiEndpoint;
import com.testgen.model.TestCase;
import com.testgen.model.WebElement;

/**
 * Main class for the TestGen application.
 * This class provides a simple demonstration of the TestGen library.
 */
public class Main {
    /**
     * Main entry point for the application.
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        // Create a sample WebElement
        WebElement element = new WebElement("input");
        element.setId("username");
        element.setName("username");
        element.setType("text");
        element.setCssSelector("#username");
        element.setXpath("//input[@id='username']");
        
        // Create a sample ApiEndpoint
        ApiEndpoint endpoint = new ApiEndpoint("https://api.example.com/users", "GET");
        endpoint.addHeader("Content-Type", "application/json");
        endpoint.setExpectedStatusCode(200);
        
        // Create sample test cases of different types
        TestCase webUITestCase = new TestCase(
            "Login Test", 
            "Test the login functionality", 
            TestCase.TestType.WEB_UI
        );
        webUITestCase.addStep(new TestCase.TestStep("Enter username", "Username field should accept input"));
        webUITestCase.addStep(new TestCase.TestStep("Enter password", "Password field should accept input"));
        webUITestCase.addStep(new TestCase.TestStep("Click login button", "User should be redirected to dashboard"));
        
        TestCase apiTestCase = new TestCase(
            "Get Users API Test", 
            "Test the Get Users API", 
            TestCase.TestType.API
        );
        apiTestCase.addStep(new TestCase.TestStep("Prepare the API request", "Request should be prepared with headers"));
        apiTestCase.addStep(new TestCase.TestStep("Send the request", "Request should be sent successfully"));
        apiTestCase.addStep(new TestCase.TestStep("Validate the response", "Response should have status code 200"));
        
        TestCase securityTestCase = new TestCase(
            "XSS Vulnerability Test", 
            "Test for Cross-Site Scripting vulnerabilities", 
            TestCase.TestType.SECURITY
        );
        securityTestCase.addStep(new TestCase.TestStep("Enter script tags in input fields", "Application should sanitize input"));
        securityTestCase.addStep(new TestCase.TestStep("Submit the form", "Scripts should not be executed in the context of the page"));
        
        TestCase accessibilityTestCase = new TestCase(
            "Keyboard Navigation Test", 
            "Test keyboard navigation for all interactive elements", 
            TestCase.TestType.ACCESSIBILITY
        );
        accessibilityTestCase.addStep(new TestCase.TestStep("Navigate through all elements using Tab key", "All elements should be focusable"));
        accessibilityTestCase.addStep(new TestCase.TestStep("Activate elements using Enter/Space keys", "Elements should activate correctly"));
        
        TestCase performanceTestCase = new TestCase(
            "Page Load Time Test", 
            "Test page load performance", 
            TestCase.TestType.PERFORMANCE
        );
        performanceTestCase.addStep(new TestCase.TestStep("Measure page load time", "Page should load in under 3 seconds"));
        performanceTestCase.addStep(new TestCase.TestStep("Analyze resource loading times", "Resources should load efficiently"));
        
        // Print information about the samples
        System.out.println("Sample WebElement:");
        System.out.println(element);
        
        System.out.println("\nSample ApiEndpoint:");
        System.out.println(endpoint);
        
        System.out.println("\nSample Test Cases:");
        System.out.println("1. Web UI Test Case:");
        System.out.println(webUITestCase);
        
        System.out.println("\n2. API Test Case:");
        System.out.println(apiTestCase);
        
        System.out.println("\n3. Security Test Case:");
        System.out.println(securityTestCase);
        
        System.out.println("\n4. Accessibility Test Case:");
        System.out.println(accessibilityTestCase);
        
        System.out.println("\n5. Performance Test Case:");
        System.out.println(performanceTestCase);
        
        // Print a message about the TestGen library
        System.out.println("\nTestGen library is initialized and ready to use!");
        System.out.println("Run 'java -cp bin com.testgen.cli.CommandLineRunner help' for usage information.");
        
        // Example CLI commands
        System.out.println("\nExample Commands:");
        System.out.println("- Generate Selenium tests:");
        System.out.println("  java -cp bin com.testgen.cli.CommandLineRunner selenium https://example.com output com.example.tests");
        System.out.println("\n- Generate REST Assured tests:");
        System.out.println("  java -cp bin com.testgen.cli.CommandLineRunner restassured collection.json output com.example.tests");
        System.out.println("\n- Generate security tests for a web page:");
        System.out.println("  java -cp bin com.testgen.cli.CommandLineRunner security-web https://example.com output");
        System.out.println("\n- Generate accessibility tests for a web page:");
        System.out.println("  java -cp bin com.testgen.cli.CommandLineRunner accessibility https://example.com output");
        System.out.println("\n- Analyze test coverage for a web page:");
        System.out.println("  java -cp bin com.testgen.cli.CommandLineRunner analyze-web https://example.com");
    }
}
