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

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Main class for the TestGen library.
 * This class provides methods to generate various types of test cases.
 */
public class TestGenLibrary {
    private final WebAnalyzer webAnalyzer;
    private final PostmanCollectionAnalyzer postmanAnalyzer;
    private final SeleniumTestGenerator seleniumGenerator;
    private final RestAssuredTestGenerator restAssuredGenerator;
    private final JiraTestGenerator jiraGenerator;
    private final AIService aiService;
    private boolean aiEnhancementEnabled;

    /**
     * Creates a new TestGenLibrary with default settings.
     * AI enhancement is disabled by default.
     */
    public TestGenLibrary() {
        this.webAnalyzer = new WebAnalyzer();
        this.postmanAnalyzer = new PostmanCollectionAnalyzer();
        this.seleniumGenerator = new SeleniumTestGenerator();
        this.restAssuredGenerator = new RestAssuredTestGenerator();
        this.jiraGenerator = new JiraTestGenerator();
        this.aiService = AIServiceFactory.createDefaultService();
        this.aiEnhancementEnabled = false;
    }

    /**
     * Creates a new TestGenLibrary with specified AI enhancement setting.
     *
     * @param aiEnhancementEnabled Whether to enable AI enhancement
     */
    public TestGenLibrary(boolean aiEnhancementEnabled) {
        this();
        this.aiEnhancementEnabled = aiEnhancementEnabled;
    }

    /**
     * Creates a new TestGenLibrary with a custom AI service.
     *
     * @param aiService           The AI service to use
     * @param aiEnhancementEnabled Whether to enable AI enhancement
     */
    public TestGenLibrary(AIService aiService, boolean aiEnhancementEnabled) {
        this.webAnalyzer = new WebAnalyzer();
        this.postmanAnalyzer = new PostmanCollectionAnalyzer();
        this.seleniumGenerator = new SeleniumTestGenerator();
        this.restAssuredGenerator = new RestAssuredTestGenerator();
        this.jiraGenerator = new JiraTestGenerator();
        this.aiService = aiService;
        this.aiEnhancementEnabled = aiEnhancementEnabled;
    }

    /**
     * Enables or disables AI enhancement.
     *
     * @param enabled Whether to enable AI enhancement
     */
    public void setAiEnhancementEnabled(boolean enabled) {
        this.aiEnhancementEnabled = enabled;
    }

    /**
     * Checks if AI enhancement is enabled.
     *
     * @return Whether AI enhancement is enabled
     */
    public boolean isAiEnhancementEnabled() {
        return aiEnhancementEnabled;
    }

    /**
     * Generates Selenium tests for a web page.
     *
     * @param url         The URL of the web page
     * @param outputDir   The directory to save the generated tests
     * @param packageName The package name for the generated tests
     * @throws TestGenException If an error occurs during test generation
     */
    public void generateSeleniumTests(String url, String outputDir, String packageName) throws TestGenException {
        try {
            List<WebElement> elements = webAnalyzer.analyze(url);
            List<TestCase> testCases = webAnalyzer.generateTestCases(elements, url);
            
            if (aiEnhancementEnabled) {
                testCases = aiService.enhanceWebTestCases(elements, testCases);
            }
            
            seleniumGenerator.generate(elements, testCases, outputDir, packageName);
        } catch (IOException e) {
            throw new TestGenException("Error generating Selenium tests: " + e.getMessage(), e);
        }
    }

    /**
     * Generates REST Assured tests for a Postman collection.
     *
     * @param collectionPath The path to the Postman collection file
     * @param outputDir      The directory to save the generated tests
     * @param packageName    The package name for the generated tests
     * @throws TestGenException If an error occurs during test generation
     */
    public void generateRestAssuredTests(String collectionPath, String outputDir, String packageName) throws TestGenException {
        try {
            List<ApiEndpoint> endpoints = postmanAnalyzer.analyze(new File(collectionPath));
            List<TestCase> testCases = postmanAnalyzer.generateTestCases(endpoints);
            
            if (aiEnhancementEnabled) {
                testCases = aiService.enhanceApiTestCases(endpoints, testCases);
            }
            
            restAssuredGenerator.generate(endpoints, testCases, outputDir, packageName);
        } catch (IOException e) {
            throw new TestGenException("Error generating REST Assured tests: " + e.getMessage(), e);
        }
    }

    /**
     * Generates Jira test cases for a web page.
     *
     * @param url       The URL of the web page
     * @param outputDir The directory to save the generated test cases
     * @param pageName  The name of the page
     * @throws TestGenException If an error occurs during test case generation
     */
    public void generateJiraTestCasesForWebPage(String url, String outputDir, String pageName) throws TestGenException {
        try {
            List<WebElement> elements = webAnalyzer.analyze(url);
            List<TestCase> testCases = webAnalyzer.generateTestCases(elements, pageName != null ? pageName : url);
            
            if (aiEnhancementEnabled) {
                testCases = aiService.enhanceWebTestCases(elements, testCases);
            }
            
            jiraGenerator.generateFromWebTestCases(testCases, outputDir);
        } catch (IOException e) {
            throw new TestGenException("Error generating Jira test cases: " + e.getMessage(), e);
        }
    }

    /**
     * Generates Jira test cases for a Postman collection.
     *
     * @param collectionPath The path to the Postman collection file
     * @param outputDir      The directory to save the generated test cases
     * @throws TestGenException If an error occurs during test case generation
     */
    public void generateJiraTestCasesForPostmanCollection(String collectionPath, String outputDir) throws TestGenException {
        try {
            List<ApiEndpoint> endpoints = postmanAnalyzer.analyze(new File(collectionPath));
            List<TestCase> testCases = postmanAnalyzer.generateTestCases(endpoints);
            
            if (aiEnhancementEnabled) {
                testCases = aiService.enhanceApiTestCases(endpoints, testCases);
            }
            
            jiraGenerator.generateFromApiTestCases(testCases, outputDir);
        } catch (IOException e) {
            throw new TestGenException("Error generating Jira test cases: " + e.getMessage(), e);
        }
    }
    
    /**
     * Analyzes test coverage for web page tests and suggests improvements.
     *
     * @param url       The URL of the web page
     * @param pageName  The name of the page
     * @return          Coverage analysis and suggestions
     * @throws TestGenException If an error occurs during analysis
     */
    public String analyzeWebTestCoverage(String url, String pageName) throws TestGenException {
        try {
            List<WebElement> elements = webAnalyzer.analyze(url);
            List<TestCase> testCases = webAnalyzer.generateTestCases(elements, pageName != null ? pageName : url);
            return aiService.analyzeCoverageGaps(testCases);
        } catch (IOException e) {
            throw new TestGenException("Error analyzing test coverage: " + e.getMessage(), e);
        }
    }
    
    /**
     * Analyzes test coverage for API tests and suggests improvements.
     *
     * @param collectionPath The path to the Postman collection file
     * @return               Coverage analysis and suggestions
     * @throws TestGenException If an error occurs during analysis
     */
    public String analyzeApiTestCoverage(String collectionPath) throws TestGenException {
        try {
            List<ApiEndpoint> endpoints = postmanAnalyzer.analyze(new File(collectionPath));
            List<TestCase> testCases = postmanAnalyzer.generateTestCases(endpoints);
            return aiService.analyzeCoverageGaps(testCases);
        } catch (IOException e) {
            throw new TestGenException("Error analyzing test coverage: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generates security-focused test cases for a web page.
     *
     * @param url       The URL of the web page
     * @param outputDir The directory to save the generated test cases
     * @param pageName  The name of the page
     * @throws TestGenException If an error occurs during test case generation
     */
    public void generateSecurityTestCasesForWebPage(String url, String outputDir, String pageName) throws TestGenException {
        try {
            List<WebElement> elements = webAnalyzer.analyze(url);
            List<TestCase> securityTests = aiService.suggestWebSecurityTests(elements);
            jiraGenerator.generateFromSecurityTestCases(securityTests, outputDir);
            
            // Also generate Selenium tests for security test cases if possible
            seleniumGenerator.generate(elements, securityTests, outputDir, "com.example.security");
        } catch (IOException e) {
            throw new TestGenException("Error generating security test cases: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generates security-focused test cases for a Postman collection.
     *
     * @param collectionPath The path to the Postman collection file
     * @param outputDir      The directory to save the generated test cases
     * @throws TestGenException If an error occurs during test case generation
     */
    public void generateSecurityTestCasesForPostmanCollection(String collectionPath, String outputDir) throws TestGenException {
        try {
            List<ApiEndpoint> endpoints = postmanAnalyzer.analyze(new File(collectionPath));
            List<TestCase> securityTests = aiService.suggestApiSecurityTests(endpoints);
            jiraGenerator.generateFromSecurityTestCases(securityTests, outputDir);
            
            // Also generate REST Assured tests for security test cases
            restAssuredGenerator.generate(endpoints, securityTests, outputDir, "com.example.security");
        } catch (IOException e) {
            throw new TestGenException("Error generating security test cases: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generates accessibility-focused test cases for a web page.
     *
     * @param url       The URL of the web page
     * @param outputDir The directory to save the generated test cases
     * @param pageName  The name of the page
     * @throws TestGenException If an error occurs during test case generation
     */
    public void generateAccessibilityTestCasesForWebPage(String url, String outputDir, String pageName) throws TestGenException {
        try {
            List<WebElement> elements = webAnalyzer.analyze(url);
            List<TestCase> accessibilityTests = aiService.suggestAccessibilityTests(elements);
            jiraGenerator.generateFromAccessibilityTestCases(accessibilityTests, outputDir);
            
            // Also generate Selenium tests for accessibility test cases if possible
            seleniumGenerator.generate(elements, accessibilityTests, outputDir, "com.example.accessibility");
        } catch (IOException e) {
            throw new TestGenException("Error generating accessibility test cases: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generates performance-focused test cases for a web page.
     *
     * @param url       The URL of the web page
     * @param outputDir The directory to save the generated test cases
     * @param pageName  The name of the page
     * @throws TestGenException If an error occurs during test case generation
     */
    public void generatePerformanceTestCasesForWebPage(String url, String outputDir, String pageName) throws TestGenException {
        try {
            List<WebElement> elements = webAnalyzer.analyze(url);
            List<TestCase> performanceTests = aiService.suggestPerformanceTests(elements);
            jiraGenerator.generateFromPerformanceTestCases(performanceTests, outputDir);
        } catch (IOException e) {
            throw new TestGenException("Error generating performance test cases: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generates performance-focused test cases for a Postman collection.
     *
     * @param collectionPath The path to the Postman collection file
     * @param outputDir      The directory to save the generated test cases
     * @throws TestGenException If an error occurs during test case generation
     */
    public void generatePerformanceTestCasesForPostmanCollection(String collectionPath, String outputDir) throws TestGenException {
        try {
            List<ApiEndpoint> endpoints = postmanAnalyzer.analyze(new File(collectionPath));
            List<TestCase> performanceTests = aiService.suggestApiPerformanceTests(endpoints);
            jiraGenerator.generateFromPerformanceTestCases(performanceTests, outputDir);
        } catch (IOException e) {
            throw new TestGenException("Error generating performance test cases: " + e.getMessage(), e);
        }
    }
}
