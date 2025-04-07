package com.testgen;

import com.testgen.exception.TestGenException;
import com.testgen.model.TestCase;

import java.util.List;

/**
 * Runs a demo of the TestGen library to showcase its capabilities.
 */
public class DemoRunner {
    /**
     * The main method that runs the demo.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("Running TestGen Library Demo");
        System.out.println("============================");
        
        try {
            TestGenLibrary testGen = new TestGenLibrary();
            
            // Demo 1: Generate Selenium tests for a regular web page
            System.out.println("\nDemo 1: Generate Selenium tests for a web page");
            System.out.println("------------------------------------------");
            List<TestCase> seleniumTests = testGen.generateSeleniumTests(
                    "https://example.com", "output", "com.example.tests");
            printTestCases(seleniumTests);
            
            // Demo 2: Generate REST Assured tests for a Postman collection
            System.out.println("\nDemo 2: Generate REST Assured tests from a Postman collection");
            System.out.println("------------------------------------------------------");
            // Simulate a Postman collection (in a real scenario, this would be a file path)
            List<TestCase> restAssuredTests = simulatePostmanCollection(testGen);
            printTestCases(restAssuredTests);
            
            // Demo 3: Generate Jira test cases for a web page
            System.out.println("\nDemo 3: Generate Jira test cases for a web page");
            System.out.println("------------------------------------------");
            List<TestCase> jiraWebTests = testGen.generateJiraWebTests(
                    "https://example.com", "output", "HomePage");
            printTestCases(jiraWebTests);
            
            // Demo 4: Generate enhanced tests with AI
            System.out.println("\nDemo 4: Generate AI-enhanced tests for a web page");
            System.out.println("---------------------------------------------");
            List<TestCase> enhancedTests = testGen.generateEnhancedTests(
                    "https://example.com", "output", "com.example.tests");
            printTestCases(enhancedTests);
            
            // Demo 5: Generate security-focused tests
            System.out.println("\nDemo 5: Generate security-focused tests for a web page");
            System.out.println("------------------------------------------------");
            List<TestCase> securityTests = testGen.generateSecurityTests(
                    "https://example.com", "output");
            printTestCases(securityTests);
            
            // Demo 6: Generate accessibility-focused tests
            System.out.println("\nDemo 6: Generate accessibility-focused tests for a web page");
            System.out.println("---------------------------------------------------");
            List<TestCase> accessibilityTests = testGen.generateAccessibilityTests(
                    "https://example.com", "output");
            printTestCases(accessibilityTests);
            
            // Demo 7: Generate performance-focused tests
            System.out.println("\nDemo 7: Generate performance-focused tests for a web page");
            System.out.println("--------------------------------------------------");
            List<TestCase> performanceTests = testGen.generatePerformanceTests(
                    "https://example.com", "output");
            printTestCases(performanceTests);
            
            // Demo 8: Analyze test coverage
            System.out.println("\nDemo 8: Analyze test coverage for a web page");
            System.out.println("------------------------------------------");
            String coverageReport = testGen.analyzeWebCoverage("https://example.com");
            System.out.println(coverageReport);

            // Demo 9: Generate tests for a Single Page Application
            System.out.println("\nDemo 9: Generate tests for a Single Page Application");
            System.out.println("------------------------------------------------");
            List<TestCase> spaTests = testGen.generateSeleniumTests(
                    "https://spa-example.com", "output", "com.example.spatests");
            printTestCases(spaTests);
            
            // Count total test cases
            int totalTestCases = seleniumTests.size() + restAssuredTests.size() + jiraWebTests.size() +
                    enhancedTests.size() + securityTests.size() + accessibilityTests.size() +
                    performanceTests.size() + spaTests.size();
            
            System.out.println("\nDemo Summary");
            System.out.println("============");
            System.out.println("Total test cases generated: " + totalTestCases);
            
        } catch (TestGenException e) {
            System.err.println("Error running demo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Simulates generating tests from a Postman collection.
     * In a real scenario, this would use an actual file path.
     *
     * @param testGen The TestGen library instance
     * @return A list of generated test cases
     * @throws TestGenException If an error occurs during test generation
     */
    private static List<TestCase> simulatePostmanCollection(TestGenLibrary testGen) throws TestGenException {
        // Create test cases programmatically instead of loading from a file
        TestCase getUsers = new TestCase(
                "Get Users",
                "Test the GET /api/users endpoint",
                TestCase.TestType.API
        );
        
        getUsers.addStep(new TestCase.TestStep(
                "Send GET request to /api/users",
                "Response status code should be 200"
        ));
        
        getUsers.addStep(new TestCase.TestStep(
                "Verify response format",
                "Response should be a JSON array of users"
        ));
        
        TestCase createUser = new TestCase(
                "Create User",
                "Test the POST /api/users endpoint",
                TestCase.TestType.API
        );
        
        createUser.addStep(new TestCase.TestStep(
                "Send POST request to /api/users with user data",
                "Response status code should be 201"
        ));
        
        createUser.addStep(new TestCase.TestStep(
                "Verify response contains created user",
                "Response should contain the created user's ID"
        ));
        
        TestCase updateUser = new TestCase(
                "Update User",
                "Test the PUT /api/users/{id} endpoint",
                TestCase.TestType.API
        );
        
        updateUser.addStep(new TestCase.TestStep(
                "Send PUT request to /api/users/{id} with updated user data",
                "Response status code should be 200"
        ));
        
        updateUser.addStep(new TestCase.TestStep(
                "Verify response contains updated fields",
                "Response should contain the updated fields"
        ));
        
        TestCase deleteUser = new TestCase(
                "Delete User",
                "Test the DELETE /api/users/{id} endpoint",
                TestCase.TestType.API
        );
        
        deleteUser.addStep(new TestCase.TestStep(
                "Send DELETE request to /api/users/{id}",
                "Response status code should be 204"
        ));
        
        deleteUser.addStep(new TestCase.TestStep(
                "Verify user is deleted",
                "Send a GET request to /api/users/{id} and verify it returns 404"
        ));
        
        List<TestCase> testCases = List.of(getUsers, createUser, updateUser, deleteUser);
        return testCases;
    }
    
    /**
     * Prints the details of a list of test cases.
     *
     * @param testCases The list of test cases to print
     */
    private static void printTestCases(List<TestCase> testCases) {
        System.out.println("Generated " + testCases.size() + " test cases:");
        
        for (TestCase testCase : testCases) {
            System.out.println("- " + testCase.getName() + " (" + testCase.getType() + ")");
            System.out.println("  Description: " + testCase.getDescription());
            System.out.println("  Steps: " + testCase.getSteps().size());
        }
    }
}
