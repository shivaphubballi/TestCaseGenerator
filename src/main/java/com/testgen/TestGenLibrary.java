package com.testgen;

import com.testgen.core.PostmanCollectionAnalyzer;
import com.testgen.core.WebAnalyzer;
import com.testgen.exception.TestGenException;
import com.testgen.generator.JiraTestGenerator;
import com.testgen.generator.RestAssuredTestGenerator;
import com.testgen.generator.SeleniumTestGenerator;
import com.testgen.model.ApiEndpoint;
import com.testgen.model.TestCase;
import com.testgen.model.WebElement;
import com.testgen.util.WebUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main entry point for the TestGen library.
 * Provides methods to generate test cases from web URLs and Postman collections.
 */
public class TestGenLibrary {
    private static final Logger LOGGER = Logger.getLogger(TestGenLibrary.class.getName());
    
    private final WebAnalyzer webAnalyzer;
    private final PostmanCollectionAnalyzer postmanAnalyzer;
    private final SeleniumTestGenerator seleniumGenerator;
    private final RestAssuredTestGenerator restAssuredGenerator;
    private final JiraTestGenerator jiraGenerator;
    
    /**
     * Constructs a new TestGenLibrary with default implementations of all components.
     */
    public TestGenLibrary() {
        this.webAnalyzer = new WebAnalyzer();
        this.postmanAnalyzer = new PostmanCollectionAnalyzer();
        this.seleniumGenerator = new SeleniumTestGenerator();
        this.restAssuredGenerator = new RestAssuredTestGenerator();
        this.jiraGenerator = new JiraTestGenerator();
    }
    
    /**
     * Constructs a new TestGenLibrary with custom implementations of components.
     *
     * @param webAnalyzer Web analyzer implementation
     * @param postmanAnalyzer Postman collection analyzer implementation
     * @param seleniumGenerator Selenium test generator implementation
     * @param restAssuredGenerator REST Assured test generator implementation
     * @param jiraGenerator Jira test generator implementation
     */
    public TestGenLibrary(WebAnalyzer webAnalyzer, PostmanCollectionAnalyzer postmanAnalyzer,
                          SeleniumTestGenerator seleniumGenerator, RestAssuredTestGenerator restAssuredGenerator,
                          JiraTestGenerator jiraGenerator) {
        this.webAnalyzer = webAnalyzer;
        this.postmanAnalyzer = postmanAnalyzer;
        this.seleniumGenerator = seleniumGenerator;
        this.restAssuredGenerator = restAssuredGenerator;
        this.jiraGenerator = jiraGenerator;
    }
    
    /**
     * Generates Selenium tests from a web URL.
     *
     * @param url URL of the web page to analyze
     * @param outputDirectory Directory to save the generated tests
     * @param packageName Package name for the generated tests
     * @return Path to the generated test file
     * @throws TestGenException If test generation fails
     */
    public String generateSeleniumTests(String url, String outputDirectory, String packageName) 
            throws TestGenException {
        LOGGER.info("Generating Selenium tests for URL: " + url);
        
        if (url == null || url.trim().isEmpty()) {
            throw new TestGenException("URL cannot be null or empty");
        }
        
        try {
            // Analyze the web page
            List<WebElement> elements = webAnalyzer.analyzeWebPage(url);
            
            // Generate a class name from the URL
            String className = WebUtil.generateTestFileName(url);
            
            // Generate the Selenium test class
            String testCode = seleniumGenerator.generateTestClassFromElements(elements, url, className, packageName);
            
            // Create the output directory if it doesn't exist
            Path dirPath = Paths.get(outputDirectory);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            
            // Convert package to directory structure
            String packagePath = packageName.replace('.', '/');
            Path packageDir = Paths.get(outputDirectory, packagePath);
            if (!Files.exists(packageDir)) {
                Files.createDirectories(packageDir);
            }
            
            // Save the test to a file
            String outputPath = outputDirectory + "/" + packagePath + "/" + className + ".java";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
                writer.write(testCode);
            }
            
            LOGGER.info("Selenium tests generated successfully: " + outputPath);
            return outputPath;
            
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to generate Selenium tests", e);
            throw new TestGenException("Failed to generate Selenium tests: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generates REST Assured tests from a Postman collection.
     *
     * @param collectionPath Path to the Postman collection file
     * @param outputDirectory Directory to save the generated tests
     * @param packageName Package name for the generated tests
     * @return Path to the generated test file
     * @throws TestGenException If test generation fails
     */
    public String generateRestAssuredTests(String collectionPath, String outputDirectory, String packageName) 
            throws TestGenException {
        LOGGER.info("Generating REST Assured tests for Postman collection: " + collectionPath);
        
        if (collectionPath == null || collectionPath.trim().isEmpty()) {
            throw new TestGenException("Collection path cannot be null or empty");
        }
        
        try {
            // Analyze the Postman collection
            List<ApiEndpoint> endpoints = postmanAnalyzer.analyzeCollection(collectionPath);
            
            // Extract collection name from the file path
            String fileName = new File(collectionPath).getName();
            String collectionName = fileName.replaceAll("\\.[^.]+$", ""); // Remove extension
            
            // Generate a class name from the collection name
            String className = WebUtil.toJavaClassName(collectionName) + "ApiTests";
            
            // Generate the REST Assured test class
            String testCode = restAssuredGenerator.generateTestClass(endpoints, className, packageName);
            
            // Create the output directory if it doesn't exist
            Path dirPath = Paths.get(outputDirectory);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            
            // Convert package to directory structure
            String packagePath = packageName.replace('.', '/');
            Path packageDir = Paths.get(outputDirectory, packagePath);
            if (!Files.exists(packageDir)) {
                Files.createDirectories(packageDir);
            }
            
            // Save the test to a file
            String outputPath = outputDirectory + "/" + packagePath + "/" + className + ".java";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
                writer.write(testCode);
            }
            
            LOGGER.info("REST Assured tests generated successfully: " + outputPath);
            return outputPath;
            
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to generate REST Assured tests", e);
            throw new TestGenException("Failed to generate REST Assured tests: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generates REST Assured tests from a Postman collection JSON string.
     *
     * @param collectionJson JSON string of the Postman collection
     * @param outputDirectory Directory to save the generated tests
     * @param packageName Package name for the generated tests
     * @param collectionName Name for the collection (used in class name)
     * @return Path to the generated test file
     * @throws TestGenException If test generation fails
     */
    public String generateRestAssuredTestsFromJson(String collectionJson, String outputDirectory, 
                                               String packageName, String collectionName) 
            throws TestGenException {
        LOGGER.info("Generating REST Assured tests from Postman collection JSON");
        
        if (collectionJson == null || collectionJson.trim().isEmpty()) {
            throw new TestGenException("Collection JSON cannot be null or empty");
        }
        
        try {
            // Analyze the Postman collection
            List<ApiEndpoint> endpoints = postmanAnalyzer.analyzeCollectionFromJson(collectionJson);
            
            // Generate a class name from the collection name
            String className = WebUtil.toJavaClassName(collectionName) + "ApiTests";
            
            // Generate the REST Assured test class
            String testCode = restAssuredGenerator.generateTestClass(endpoints, className, packageName);
            
            // Create the output directory if it doesn't exist
            Path dirPath = Paths.get(outputDirectory);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            
            // Convert package to directory structure
            String packagePath = packageName.replace('.', '/');
            Path packageDir = Paths.get(outputDirectory, packagePath);
            if (!Files.exists(packageDir)) {
                Files.createDirectories(packageDir);
            }
            
            // Save the test to a file
            String outputPath = outputDirectory + "/" + packagePath + "/" + className + ".java";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
                writer.write(testCode);
            }
            
            LOGGER.info("REST Assured tests generated successfully: " + outputPath);
            return outputPath;
            
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to generate REST Assured tests", e);
            throw new TestGenException("Failed to generate REST Assured tests: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generates Jira test cases from a web URL.
     *
     * @param url URL of the web page to analyze
     * @param outputDirectory Directory to save the generated test cases
     * @param pageName Name of the page (optional, derived from URL if null)
     * @return Path to the generated test cases file
     * @throws TestGenException If test case generation fails
     */
    public String generateJiraWebTests(String url, String outputDirectory, String pageName) 
            throws TestGenException {
        LOGGER.info("Generating Jira test cases for URL: " + url);
        
        if (url == null || url.trim().isEmpty()) {
            throw new TestGenException("URL cannot be null or empty");
        }
        
        try {
            // Analyze the web page
            List<WebElement> elements = webAnalyzer.analyzeWebPage(url);
            
            // Derive page name from URL if not provided
            if (pageName == null || pageName.isEmpty()) {
                pageName = WebUtil.generateTestFileName(url).replace("Page", "");
            }
            
            // Generate Jira test cases
            List<TestCase> testCases = jiraGenerator.generateFromWebElements(elements, url, pageName);
            
            // Convert to Jira format
            List<String> jiraTestCases = jiraGenerator.convertToJiraFormat(testCases);
            
            // Create the output directory if it doesn't exist
            Path dirPath = Paths.get(outputDirectory);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            
            // Save the test cases to a file
            String outputFileName = pageName.replaceAll("[^a-zA-Z0-9]", "_") + "_JiraTestCases.txt";
            String outputPath = outputDirectory + "/" + outputFileName;
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
                for (String jiraTestCase : jiraTestCases) {
                    writer.write(jiraTestCase);
                    writer.write("\n\n");
                    writer.write("-".repeat(80));
                    writer.write("\n\n");
                }
            }
            
            LOGGER.info("Jira test cases generated successfully: " + outputPath);
            return outputPath;
            
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to generate Jira test cases", e);
            throw new TestGenException("Failed to generate Jira test cases: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generates Jira test cases from a Postman collection.
     *
     * @param collectionPath Path to the Postman collection file
     * @param outputDirectory Directory to save the generated test cases
     * @return Path to the generated test cases file
     * @throws TestGenException If test case generation fails
     */
    public String generateJiraApiTests(String collectionPath, String outputDirectory) 
            throws TestGenException {
        LOGGER.info("Generating Jira test cases for Postman collection: " + collectionPath);
        
        if (collectionPath == null || collectionPath.trim().isEmpty()) {
            throw new TestGenException("Collection path cannot be null or empty");
        }
        
        try {
            // Analyze the Postman collection
            List<ApiEndpoint> endpoints = postmanAnalyzer.analyzeCollection(collectionPath);
            
            // Extract collection name from the file path
            String fileName = new File(collectionPath).getName();
            String collectionName = fileName.replaceAll("\\.[^.]+$", ""); // Remove extension
            
            // Generate Jira test cases
            List<TestCase> testCases = jiraGenerator.generateFromApiEndpoints(endpoints, collectionName);
            
            // Convert to Jira format
            List<String> jiraTestCases = jiraGenerator.convertToJiraFormat(testCases);
            
            // Create the output directory if it doesn't exist
            Path dirPath = Paths.get(outputDirectory);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            
            // Save the test cases to a file
            String outputFileName = collectionName.replaceAll("[^a-zA-Z0-9]", "_") + "_JiraTestCases.txt";
            String outputPath = outputDirectory + "/" + outputFileName;
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
                for (String jiraTestCase : jiraTestCases) {
                    writer.write(jiraTestCase);
                    writer.write("\n\n");
                    writer.write("-".repeat(80));
                    writer.write("\n\n");
                }
            }
            
            LOGGER.info("Jira test cases generated successfully: " + outputPath);
            return outputPath;
            
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to generate Jira test cases", e);
            throw new TestGenException("Failed to generate Jira test cases: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generates Jira test cases from a Postman collection JSON string.
     *
     * @param collectionJson JSON string of the Postman collection
     * @param outputDirectory Directory to save the generated test cases
     * @param collectionName Name for the collection
     * @return Path to the generated test cases file
     * @throws TestGenException If test case generation fails
     */
    public String generateJiraApiTestsFromJson(String collectionJson, String outputDirectory, String collectionName) 
            throws TestGenException {
        LOGGER.info("Generating Jira test cases from Postman collection JSON");
        
        if (collectionJson == null || collectionJson.trim().isEmpty()) {
            throw new TestGenException("Collection JSON cannot be null or empty");
        }
        
        try {
            // Analyze the Postman collection
            List<ApiEndpoint> endpoints = postmanAnalyzer.analyzeCollectionFromJson(collectionJson);
            
            // Generate Jira test cases
            List<TestCase> testCases = jiraGenerator.generateFromApiEndpoints(endpoints, collectionName);
            
            // Convert to Jira format
            List<String> jiraTestCases = jiraGenerator.convertToJiraFormat(testCases);
            
            // Create the output directory if it doesn't exist
            Path dirPath = Paths.get(outputDirectory);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            
            // Save the test cases to a file
            String outputFileName = collectionName.replaceAll("[^a-zA-Z0-9]", "_") + "_JiraTestCases.txt";
            String outputPath = outputDirectory + "/" + outputFileName;
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
                for (String jiraTestCase : jiraTestCases) {
                    writer.write(jiraTestCase);
                    writer.write("\n\n");
                    writer.write("-".repeat(80));
                    writer.write("\n\n");
                }
            }
            
            LOGGER.info("Jira test cases generated successfully: " + outputPath);
            return outputPath;
            
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to generate Jira test cases", e);
            throw new TestGenException("Failed to generate Jira test cases: " + e.getMessage(), e);
        }
    }
    
    /**
     * Analyzes a web page and returns the identified elements.
     * This is useful if you want to use the elements for custom processing.
     *
     * @param url URL of the web page to analyze
     * @return List of WebElement objects
     * @throws TestGenException If web page analysis fails
     */
    public List<WebElement> analyzeWebPage(String url) throws TestGenException {
        return webAnalyzer.analyzeWebPage(url);
    }
    
    /**
     * Analyzes a Postman collection and returns the identified API endpoints.
     * This is useful if you want to use the endpoints for custom processing.
     *
     * @param collectionPath Path to the Postman collection file
     * @return List of ApiEndpoint objects
     * @throws TestGenException If collection analysis fails
     */
    public List<ApiEndpoint> analyzePostmanCollection(String collectionPath) throws TestGenException {
        return postmanAnalyzer.analyzeCollection(collectionPath);
    }
    
    /**
     * Analyzes a Postman collection JSON string and returns the identified API endpoints.
     * This is useful if you want to use the endpoints for custom processing.
     *
     * @param collectionJson JSON string of the Postman collection
     * @return List of ApiEndpoint objects
     * @throws TestGenException If collection analysis fails
     */
    public List<ApiEndpoint> analyzePostmanCollectionFromJson(String collectionJson) throws TestGenException {
        return postmanAnalyzer.analyzeCollectionFromJson(collectionJson);
    }
    
    /**
     * Gets the WebAnalyzer instance.
     *
     * @return The WebAnalyzer instance
     */
    public WebAnalyzer getWebAnalyzer() {
        return webAnalyzer;
    }
    
    /**
     * Gets the PostmanCollectionAnalyzer instance.
     *
     * @return The PostmanCollectionAnalyzer instance
     */
    public PostmanCollectionAnalyzer getPostmanAnalyzer() {
        return postmanAnalyzer;
    }
    
    /**
     * Gets the SeleniumTestGenerator instance.
     *
     * @return The SeleniumTestGenerator instance
     */
    public SeleniumTestGenerator getSeleniumGenerator() {
        return seleniumGenerator;
    }
    
    /**
     * Gets the RestAssuredTestGenerator instance.
     *
     * @return The RestAssuredTestGenerator instance
     */
    public RestAssuredTestGenerator getRestAssuredGenerator() {
        return restAssuredGenerator;
    }
    
    /**
     * Gets the JiraTestGenerator instance.
     *
     * @return The JiraTestGenerator instance
     */
    public JiraTestGenerator getJiraGenerator() {
        return jiraGenerator;
    }
}
