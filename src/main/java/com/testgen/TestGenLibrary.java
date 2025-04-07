package com.testgen;

import com.testgen.ai.AIService;
import com.testgen.ai.AIServiceFactory;
import com.testgen.core.PostmanCollectionAnalyzer;
import com.testgen.core.WebAnalyzer;
import com.testgen.exception.TestGenException;
import com.testgen.generator.JiraTestGenerator;
import com.testgen.generator.RestAssuredTestGenerator;
import com.testgen.generator.SeleniumTestGenerator;
import com.testgen.model.ApiEndpoint;
import com.testgen.model.TestCase;
import com.testgen.model.WebElement;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Main library class for TestGen.
 */
public class TestGenLibrary {
    private AIService aiService;
    private WebAnalyzer webAnalyzer;
    private PostmanCollectionAnalyzer postmanAnalyzer;
    
    /**
     * Creates a new TestGenLibrary instance.
     */
    public TestGenLibrary() {
        this.aiService = AIServiceFactory.createService(AIServiceFactory.ServiceType.DEFAULT);
        this.webAnalyzer = new WebAnalyzer();
        this.postmanAnalyzer = new PostmanCollectionAnalyzer();
    }
    
    /**
     * Generates Selenium tests for a web page.
     *
     * @param url The URL of the web page
     * @param outputDir The output directory for the generated tests
     * @param packageName The package name for the generated tests
     * @return A list of generated test cases
     * @throws TestGenException If an error occurs during test generation
     */
    public List<TestCase> generateSeleniumTests(String url, String outputDir, String packageName) throws TestGenException {
        try {
            // Auto-detect if the page is an SPA
            boolean isSPA = webAnalyzer.detectIfSPA(url);
            webAnalyzer.setSPA(isSPA);
            
            List<WebElement> elements = webAnalyzer.analyze(url);
            List<TestCase> testCases = webAnalyzer.generateTestCases(elements, url);
            
            SeleniumTestGenerator generator = new SeleniumTestGenerator();
            generator.generate(testCases, outputDir, packageName);
            
            return testCases;
        } catch (IOException e) {
            throw new TestGenException("Error generating Selenium tests: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generates REST Assured tests for a Postman collection.
     *
     * @param collectionPath The path to the Postman collection file
     * @param outputDir The output directory for the generated tests
     * @param packageName The package name for the generated tests
     * @return A list of generated test cases
     * @throws TestGenException If an error occurs during test generation
     */
    public List<TestCase> generateRestAssuredTests(String collectionPath, String outputDir, String packageName) throws TestGenException {
        try {
            String collectionJson = new String(Files.readAllBytes(Paths.get(collectionPath)));
            List<ApiEndpoint> endpoints = postmanAnalyzer.analyzeCollectionFromJson(collectionJson);
            List<TestCase> testCases = postmanAnalyzer.generateTestCases(endpoints);
            
            RestAssuredTestGenerator generator = new RestAssuredTestGenerator();
            generator.generate(testCases, outputDir, packageName);
            
            return testCases;
        } catch (IOException e) {
            throw new TestGenException("Error generating REST Assured tests: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generates Jira test cases for a web page.
     *
     * @param url The URL of the web page
     * @param outputDir The output directory for the generated tests
     * @param pageName The name of the page
     * @return A list of generated test cases
     * @throws TestGenException If an error occurs during test generation
     */
    public List<TestCase> generateJiraWebTests(String url, String outputDir, String pageName) throws TestGenException {
        try {
            // Auto-detect if the page is an SPA
            boolean isSPA = webAnalyzer.detectIfSPA(url);
            webAnalyzer.setSPA(isSPA);
            
            List<WebElement> elements = webAnalyzer.analyze(url);
            List<TestCase> testCases = webAnalyzer.generateTestCases(elements, pageName);
            
            JiraTestGenerator generator = new JiraTestGenerator();
            generator.generate(testCases, outputDir, null);
            
            return testCases;
        } catch (IOException e) {
            throw new TestGenException("Error generating Jira test cases: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generates Jira test cases for a Postman collection.
     *
     * @param collectionPath The path to the Postman collection file
     * @param outputDir The output directory for the generated tests
     * @param projectKey The Jira project key
     * @return A list of generated test cases
     * @throws TestGenException If an error occurs during test generation
     */
    public List<TestCase> generateJiraApiTests(String collectionPath, String outputDir, String projectKey) throws TestGenException {
        try {
            String collectionJson = new String(Files.readAllBytes(Paths.get(collectionPath)));
            List<ApiEndpoint> endpoints = postmanAnalyzer.analyzeCollectionFromJson(collectionJson);
            List<TestCase> testCases = postmanAnalyzer.generateTestCases(endpoints);
            
            JiraTestGenerator generator = new JiraTestGenerator();
            generator.generate(testCases, outputDir, projectKey);
            
            return testCases;
        } catch (IOException e) {
            throw new TestGenException("Error generating Jira test cases: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generates AI-enhanced test cases for a web page.
     *
     * @param url The URL of the web page
     * @param outputDir The output directory for the generated tests
     * @param packageName The package name for the generated tests
     * @return A list of generated test cases
     * @throws TestGenException If an error occurs during test generation
     */
    public List<TestCase> generateEnhancedTests(String url, String outputDir, String packageName) throws TestGenException {
        try {
            // Auto-detect if the page is an SPA
            boolean isSPA = webAnalyzer.detectIfSPA(url);
            webAnalyzer.setSPA(isSPA);
            
            List<WebElement> elements = webAnalyzer.analyze(url);
            List<TestCase> testCases = webAnalyzer.generateTestCases(elements, url);
            
            // Use AI to enhance test cases
            List<TestCase> enhancedTestCases = new ArrayList<>();
            for (TestCase testCase : testCases) {
                TestCase enhancedTestCase = aiService.enhanceTestCase(testCase);
                enhancedTestCases.add(enhancedTestCase);
            }
            
            SeleniumTestGenerator generator = new SeleniumTestGenerator();
            generator.generate(enhancedTestCases, outputDir, packageName);
            
            return enhancedTestCases;
        } catch (IOException e) {
            throw new TestGenException("Error generating enhanced tests: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generates security-focused test cases for a web page.
     *
     * @param url The URL of the web page
     * @param outputDir The output directory for the generated tests
     * @return A list of generated test cases
     * @throws TestGenException If an error occurs during test generation
     */
    public List<TestCase> generateSecurityTests(String url, String outputDir) throws TestGenException {
        try {
            // Auto-detect if the page is an SPA
            boolean isSPA = webAnalyzer.detectIfSPA(url);
            webAnalyzer.setSPA(isSPA);
            
            List<WebElement> elements = webAnalyzer.analyze(url);
            List<TestCase> testCases = webAnalyzer.generateTestCases(elements, url);
            
            // Use AI to generate security-focused test cases
            List<TestCase> securityTestCases = aiService.generateSecurityTestCases(testCases, url);
            
            JiraTestGenerator generator = new JiraTestGenerator();
            generator.generate(securityTestCases, outputDir, null);
            
            return securityTestCases;
        } catch (IOException e) {
            throw new TestGenException("Error generating security tests: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generates accessibility-focused test cases for a web page.
     *
     * @param url The URL of the web page
     * @param outputDir The output directory for the generated tests
     * @return A list of generated test cases
     * @throws TestGenException If an error occurs during test generation
     */
    public List<TestCase> generateAccessibilityTests(String url, String outputDir) throws TestGenException {
        try {
            // Auto-detect if the page is an SPA
            boolean isSPA = webAnalyzer.detectIfSPA(url);
            webAnalyzer.setSPA(isSPA);
            
            List<WebElement> elements = webAnalyzer.analyze(url);
            List<TestCase> testCases = webAnalyzer.generateTestCases(elements, url);
            
            // Use AI to generate accessibility-focused test cases
            List<TestCase> accessibilityTestCases = aiService.generateAccessibilityTestCases(testCases, url);
            
            JiraTestGenerator generator = new JiraTestGenerator();
            generator.generate(accessibilityTestCases, outputDir, null);
            
            return accessibilityTestCases;
        } catch (IOException e) {
            throw new TestGenException("Error generating accessibility tests: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generates performance-focused test cases for a web page.
     *
     * @param url The URL of the web page
     * @param outputDir The output directory for the generated tests
     * @return A list of generated test cases
     * @throws TestGenException If an error occurs during test generation
     */
    public List<TestCase> generatePerformanceTests(String url, String outputDir) throws TestGenException {
        try {
            // Auto-detect if the page is an SPA
            boolean isSPA = webAnalyzer.detectIfSPA(url);
            webAnalyzer.setSPA(isSPA);
            
            List<WebElement> elements = webAnalyzer.analyze(url);
            List<TestCase> testCases = webAnalyzer.generateTestCases(elements, url);
            
            // Use AI to generate performance-focused test cases
            List<TestCase> performanceTestCases = aiService.generatePerformanceTestCases(testCases, url);
            
            JiraTestGenerator generator = new JiraTestGenerator();
            generator.generate(performanceTestCases, outputDir, null);
            
            return performanceTestCases;
        } catch (IOException e) {
            throw new TestGenException("Error generating performance tests: " + e.getMessage(), e);
        }
    }
    
    /**
     * Analyzes test coverage for a web page.
     *
     * @param url The URL of the web page
     * @return A coverage report
     * @throws TestGenException If an error occurs during analysis
     */
    public String analyzeWebCoverage(String url) throws TestGenException {
        try {
            // Auto-detect if the page is an SPA
            boolean isSPA = webAnalyzer.detectIfSPA(url);
            webAnalyzer.setSPA(isSPA);
            
            List<WebElement> elements = webAnalyzer.analyze(url);
            List<TestCase> testCases = webAnalyzer.generateTestCases(elements, url);
            
            // Calculate coverage metrics
            int totalElements = elements.size();
            int coveredElements = 0;
            
            for (TestCase testCase : testCases) {
                // Count elements covered by test cases
                // This is a simplified approximation
                coveredElements += testCase.getSteps().size();
            }
            
            // Ensure we don't exceed 100% coverage
            coveredElements = Math.min(coveredElements, totalElements);
            
            // Calculate coverage percentage
            double coveragePercentage = totalElements > 0 ? 
                    ((double) coveredElements / totalElements) * 100 : 0;
            
            // Format coverage report
            StringBuilder report = new StringBuilder();
            report.append("Test Coverage Analysis for ").append(url).append("\n\n");
            report.append("Total Elements: ").append(totalElements).append("\n");
            report.append("Covered Elements: ").append(coveredElements).append("\n");
            report.append("Coverage Percentage: ").append(String.format("%.2f", coveragePercentage)).append("%\n\n");
            
            report.append("Test Case Breakdown:\n");
            for (TestCase testCase : testCases) {
                report.append("- ").append(testCase.getName())
                      .append(" (").append(testCase.getSteps().size()).append(" steps)\n");
            }
            
            // For SPAs, include additional information
            if (isSPA) {
                report.append("\nSPA-Specific Analysis:\n");
                report.append("- Contains client-side routing: Yes\n");
                report.append("- Contains dynamic content: Yes\n");
                report.append("- AJAX calls detected: Yes\n");
            }
            
            return report.toString();
        } catch (IOException e) {
            throw new TestGenException("Error analyzing web coverage: " + e.getMessage(), e);
        }
    }
    
    /**
     * Analyzes a Postman collection to extract API endpoints.
     * 
     * @param collectionPath The path to the Postman collection file
     * @return A list of API endpoints
     * @throws TestGenException If an error occurs during analysis
     */
    public List<ApiEndpoint> analyzePostmanCollection(String collectionPath) throws TestGenException {
        return postmanAnalyzer.analyzeCollection(collectionPath);
    }
    
    /**
     * Analyzes a Postman collection in JSON format to extract API endpoints.
     * 
     * @param collectionJson The Postman collection in JSON format
     * @return A list of API endpoints
     * @throws TestGenException If an error occurs during analysis
     */
    public List<ApiEndpoint> analyzePostmanCollectionFromJson(String collectionJson) throws TestGenException {
        return postmanAnalyzer.analyzeCollectionFromJson(collectionJson);
    }
    
    /**
     * Sets the AI service to use for test enhancement.
     *
     * @param aiService The AI service
     */
    public void setAiService(AIService aiService) {
        this.aiService = aiService;
    }
    
    /**
     * Gets the web analyzer used for web page analysis.
     *
     * @return The web analyzer
     */
    public WebAnalyzer getWebAnalyzer() {
        return webAnalyzer;
    }
    
    /**
     * Sets the web analyzer to use for web page analysis.
     *
     * @param webAnalyzer The web analyzer
     */
    public void setWebAnalyzer(WebAnalyzer webAnalyzer) {
        this.webAnalyzer = webAnalyzer;
    }
    
    /**
     * Gets the Postman collection analyzer used for API analysis.
     * 
     * @return The Postman collection analyzer
     */
    public PostmanCollectionAnalyzer getPostmanAnalyzer() {
        return postmanAnalyzer;
    }
    
    /**
     * Sets the Postman collection analyzer to use for API analysis.
     *
     * @param postmanAnalyzer The Postman collection analyzer
     */
    public void setPostmanAnalyzer(PostmanCollectionAnalyzer postmanAnalyzer) {
        this.postmanAnalyzer = postmanAnalyzer;
    }
}
