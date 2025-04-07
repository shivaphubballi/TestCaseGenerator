package com.testgen;

import com.testgen.core.WebAnalyzer;
import com.testgen.exception.TestGenException;
import com.testgen.model.ApiEndpoint;
import com.testgen.model.TestCase;
import com.testgen.model.WebElement;

import java.io.IOException;
import java.util.List;

/**
 * Main class for the TestGen library.
 */
public class Main {
    /**
     * The main method that runs the TestGen library.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("TestGen - Test Generation Library");
        System.out.println("===============================");
        
        try {
            // Create a TestGenLibrary instance
            TestGenLibrary testGen = new TestGenLibrary();
            
            // Example: Analyze a web page
            System.out.println("\nAnalyzing web page...");
            List<WebElement> elements = testGen.getWebAnalyzer().analyze("https://example.com");
            System.out.println("Found " + elements.size() + " elements");
            
            // Example: Create an API endpoint
            ApiEndpoint endpoint = new ApiEndpoint();
            endpoint.setUrl("https://api.example.com/users");
            endpoint.setMethod("GET");
            
            // Example: Generate test cases
            System.out.println("\nGenerating test cases...");
            List<TestCase> webTestCases = testGen.getWebAnalyzer().generateTestCases(elements, "Example Page");
            System.out.println("Generated " + webTestCases.size() + " web test cases");
            
            // Example: Print test case details
            for (TestCase testCase : webTestCases) {
                System.out.println("- " + testCase.getName() + " (" + testCase.getSteps().size() + " steps)");
            }
            
            System.out.println("\nTestGen completed successfully");
        } catch (TestGenException | IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
