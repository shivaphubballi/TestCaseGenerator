package com.testgen.generator;

import com.testgen.model.TestCase;
import com.testgen.model.TestCase.TestStep;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Generates Jira test cases based on test case models.
 */
public class JiraTestGenerator {
    
    /**
     * Generates Jira test cases.
     *
     * @param testCases The test cases to generate
     * @param outputDir The output directory
     * @param projectKey The Jira project key (optional, can be null)
     * @throws IOException If an error occurs during generation
     */
    public void generate(List<TestCase> testCases, String outputDir, String projectKey) throws IOException {
        // Create output directory if it doesn't exist
        Files.createDirectories(Paths.get(outputDir));
        
        // Generate a Jira-compatible CSV file
        String filename = "jira_test_cases.csv";
        if (projectKey != null && !projectKey.isEmpty()) {
            filename = projectKey + "_test_cases.csv";
        }
        
        File outputFile = new File(outputDir, filename);
        
        try (FileWriter writer = new FileWriter(outputFile)) {
            // Write CSV header
            writer.write("Summary,Description,Test Type,Steps,Expected Results\n");
            
            // Write each test case
            for (TestCase testCase : testCases) {
                // Escape double quotes and commas in fields
                String summary = escape(testCase.getName());
                String description = escape(testCase.getDescription());
                String testType = escape(testCase.getType().toString());
                
                // Build steps and expected results as numbered lists
                StringBuilder steps = new StringBuilder();
                StringBuilder expectedResults = new StringBuilder();
                
                int stepNumber = 1;
                for (TestStep step : testCase.getSteps()) {
                    if (stepNumber > 1) {
                        steps.append("\\n");
                        expectedResults.append("\\n");
                    }
                    
                    steps.append(stepNumber).append(". ").append(escape(step.getDescription()));
                    expectedResults.append(stepNumber).append(". ").append(escape(step.getExpectedResult()));
                    
                    stepNumber++;
                }
                
                // Write the CSV row
                writer.write(String.format("%s,%s,%s,%s,%s\n", 
                        summary, description, testType, steps.toString(), expectedResults.toString()));
            }
        }
        
        System.out.println("Generated Jira test cases in " + outputFile.getAbsolutePath());
    }
    
    /**
     * Escapes special characters in CSV fields.
     *
     * @param field The field to escape
     * @return The escaped field
     */
    private String escape(String field) {
        if (field == null) {
            return "";
        }
        
        // Escape double quotes by doubling them
        String escaped = field.replace("\"", "\"\"");
        
        // If the field contains commas, quotes, or newlines, enclose it in double quotes
        if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n")) {
            escaped = "\"" + escaped + "\"";
        }
        
        return escaped;
    }
}
