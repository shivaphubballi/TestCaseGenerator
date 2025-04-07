package com.testgen.generator;

import com.testgen.model.TestCase;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generates Jira test cases.
 */
public class JiraTestGenerator {
    /**
     * Generates Jira test cases for web tests.
     *
     * @param testCases The test cases
     * @param outputDir The directory to save the generated test cases
     * @throws IOException If an error occurs during generation
     */
    public void generateFromWebTestCases(List<TestCase> testCases, String outputDir) throws IOException {
        generateTestCases(testCases, outputDir, "web");
    }
    
    /**
     * Generates Jira test cases for API tests.
     *
     * @param testCases The test cases
     * @param outputDir The directory to save the generated test cases
     * @throws IOException If an error occurs during generation
     */
    public void generateFromApiTestCases(List<TestCase> testCases, String outputDir) throws IOException {
        generateTestCases(testCases, outputDir, "api");
    }
    
    /**
     * Generates Jira test cases for security tests.
     *
     * @param testCases The test cases
     * @param outputDir The directory to save the generated test cases
     * @throws IOException If an error occurs during generation
     */
    public void generateFromSecurityTestCases(List<TestCase> testCases, String outputDir) throws IOException {
        // Filter security test cases
        List<TestCase> securityTests = testCases.stream()
            .filter(tc -> tc.getType() == TestCase.TestType.SECURITY)
            .collect(Collectors.toList());
        
        generateTestCases(securityTests, outputDir, "security");
    }
    
    /**
     * Generates Jira test cases for accessibility tests.
     *
     * @param testCases The test cases
     * @param outputDir The directory to save the generated test cases
     * @throws IOException If an error occurs during generation
     */
    public void generateFromAccessibilityTestCases(List<TestCase> testCases, String outputDir) throws IOException {
        // Filter accessibility test cases
        List<TestCase> accessibilityTests = testCases.stream()
            .filter(tc -> tc.getType() == TestCase.TestType.ACCESSIBILITY)
            .collect(Collectors.toList());
        
        generateTestCases(accessibilityTests, outputDir, "accessibility");
    }
    
    /**
     * Generates Jira test cases for performance tests.
     *
     * @param testCases The test cases
     * @param outputDir The directory to save the generated test cases
     * @throws IOException If an error occurs during generation
     */
    public void generateFromPerformanceTestCases(List<TestCase> testCases, String outputDir) throws IOException {
        // Filter performance test cases
        List<TestCase> performanceTests = testCases.stream()
            .filter(tc -> tc.getType() == TestCase.TestType.PERFORMANCE)
            .collect(Collectors.toList());
        
        generateTestCases(performanceTests, outputDir, "performance");
    }
    
    /**
     * Generates Jira test cases.
     *
     * @param testCases The test cases
     * @param outputDir The directory to save the generated test cases
     * @param type      The type of test cases (web, api, security, accessibility, performance)
     * @throws IOException If an error occurs during generation
     */
    private void generateTestCases(List<TestCase> testCases, String outputDir, String type) throws IOException {
        // Create output directory if it doesn't exist
        File outputDirFile = new File(outputDir);
        if (!outputDirFile.exists()) {
            outputDirFile.mkdirs();
        }
        
        // Create subdirectory for Jira test cases
        File jiraDir = new File(outputDirFile, "jira");
        if (!jiraDir.exists()) {
            jiraDir.mkdirs();
        }
        
        // Generate Jira test cases file
        File outputFile = new File(jiraDir, "jira_" + type + "_test_cases.csv");
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            // Write CSV header
            writer.println("\"Summary\",\"Description\",\"Test Type\",\"Test Steps\",\"Expected Results\"");
            
            // Write test cases
            for (TestCase testCase : testCases) {
                writer.print("\"" + testCase.getName() + "\",");
                writer.print("\"" + testCase.getDescription() + "\",");
                writer.print("\"" + testCase.getType() + "\",");
                
                // Write test steps
                writer.print("\"");
                for (int i = 0; i < testCase.getSteps().size(); i++) {
                    TestCase.TestStep step = testCase.getSteps().get(i);
                    writer.print((i + 1) + ". " + step.getDescription());
                    if (i < testCase.getSteps().size() - 1) {
                        writer.print("\\n");
                    }
                }
                writer.print("\",");
                
                // Write expected results
                writer.print("\"");
                for (int i = 0; i < testCase.getSteps().size(); i++) {
                    TestCase.TestStep step = testCase.getSteps().get(i);
                    writer.print((i + 1) + ". " + step.getExpectedResult());
                    if (i < testCase.getSteps().size() - 1) {
                        writer.print("\\n");
                    }
                }
                writer.print("\"");
                
                writer.println();
            }
        }
        
        // Generate Jira test cases in Markdown format
        generateMarkdownTestCases(testCases, jiraDir, type);
    }
    
    /**
     * Generates Jira test cases in Markdown format.
     *
     * @param testCases The test cases
     * @param outputDir The directory to save the generated test cases
     * @param type      The type of test cases (web, api, security, accessibility, performance)
     * @throws IOException If an error occurs during generation
     */
    private void generateMarkdownTestCases(List<TestCase> testCases, File outputDir, String type) throws IOException {
        File outputFile = new File(outputDir, "jira_" + type + "_test_cases.md");
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            // Write header
            writer.println("# " + capitalize(type) + " Test Cases");
            writer.println();
            
            // Write each test case
            for (int i = 0; i < testCases.size(); i++) {
                TestCase testCase = testCases.get(i);
                
                writer.println("## Test Case " + (i + 1) + ": " + testCase.getName());
                writer.println();
                writer.println("**Description:** " + testCase.getDescription());
                writer.println();
                writer.println("**Test Type:** " + testCase.getType());
                writer.println();
                
                // Write test steps and expected results in a table
                writer.println("| Step | Description | Expected Result |");
                writer.println("|------|-------------|-----------------|");
                
                for (int j = 0; j < testCase.getSteps().size(); j++) {
                    TestCase.TestStep step = testCase.getSteps().get(j);
                    writer.println("| " + (j + 1) + " | " + step.getDescription() + " | " + step.getExpectedResult() + " |");
                }
                
                writer.println();
                writer.println("---");
                writer.println();
            }
        }
    }
    
    /**
     * Capitalizes the first letter of a string.
     *
     * @param str The string to capitalize
     * @return The capitalized string
     */
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}
