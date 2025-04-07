package com.testgen.core;

import com.testgen.exception.TestGenException;
import com.testgen.model.AjaxCall;
import com.testgen.model.DynamicEvent;
import com.testgen.model.RouteChange;
import com.testgen.model.TestCase;
import com.testgen.model.WebElement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Analyzes web pages to extract elements and generate test cases.
 * Enhanced to support both static websites and SPAs (Single Page Applications).
 */
public class WebAnalyzer {
    
    private SPAAnalyzer spaAnalyzer;
    private boolean isSPA;
    
    /**
     * Creates a new WebAnalyzer instance.
     */
    public WebAnalyzer() {
        this.spaAnalyzer = new SPAAnalyzer();
        this.isSPA = false;
    }
    
    /**
     * Analyzes a web page and extracts elements.
     *
     * @param url The URL of the web page
     * @return A list of web elements
     * @throws IOException If an error occurs during analysis
     */
    public List<WebElement> analyze(String url) throws IOException {
        try {
            if (isSPA) {
                return analyzeSPA(url);
            } else {
                return analyzeStaticPage(url);
            }
        } catch (TestGenException e) {
            throw new IOException("Error analyzing web page: " + e.getMessage(), e);
        }
    }
    
    /**
     * Analyzes a static web page and extracts elements.
     *
     * @param url The URL of the web page
     * @return A list of web elements
     * @throws IOException If an error occurs during analysis
     */
    private List<WebElement> analyzeStaticPage(String url) throws IOException {
        // TODO: Implement web page analysis using JSoup
        // This is a placeholder implementation
        List<WebElement> elements = new ArrayList<>();
        
        // Add some sample elements for demonstration
        WebElement loginForm = new WebElement("form");
        loginForm.setId("login-form");
        elements.add(loginForm);
        
        WebElement usernameField = new WebElement("input");
        usernameField.setId("username");
        usernameField.setType("text");
        elements.add(usernameField);
        
        WebElement passwordField = new WebElement("input");
        passwordField.setId("password");
        passwordField.setType("password");
        elements.add(passwordField);
        
        WebElement loginButton = new WebElement("button");
        loginButton.setId("login-button");
        loginButton.setText("Login");
        elements.add(loginButton);
        
        return elements;
    }
    
    /**
     * Analyzes a Single Page Application and extracts elements.
     *
     * @param url The URL of the SPA
     * @return A list of web elements
     * @throws TestGenException If an error occurs during analysis
     */
    private List<WebElement> analyzeSPA(String url) throws TestGenException {
        return spaAnalyzer.analyzeSPA(url);
    }
    
    /**
     * Generates test cases for a list of web elements.
     *
     * @param elements The web elements
     * @param pageName The name of the page
     * @return A list of test cases
     */
    public List<TestCase> generateTestCases(List<WebElement> elements, String pageName) {
        if (isSPA) {
            try {
                // For SPAs, we need more information to generate comprehensive tests
                List<RouteChange> routes = spaAnalyzer.detectRoutes(pageName);
                List<AjaxCall> ajaxCalls = spaAnalyzer.captureAjaxCalls(pageName);
                List<DynamicEvent> dynamicEvents = spaAnalyzer.detectDynamicEvents(pageName);
                
                // Generate SPA-specific test cases
                return spaAnalyzer.generateSPATestCases(elements, routes, ajaxCalls, dynamicEvents, pageName);
            } catch (TestGenException e) {
                // Fall back to basic test cases if SPA analysis fails
                return generateBasicTestCases(elements, pageName);
            }
        } else {
            return generateBasicTestCases(elements, pageName);
        }
    }
    
    /**
     * Generates basic test cases for a list of web elements.
     *
     * @param elements The web elements
     * @param pageName The name of the page
     * @return A list of test cases
     */
    private List<TestCase> generateBasicTestCases(List<WebElement> elements, String pageName) {
        // This is a placeholder implementation
        List<TestCase> testCases = new ArrayList<>();
        
        // Create a login test case
        TestCase loginTest = new TestCase(
                pageName + " - Login Test",
                "Test the login functionality on the " + pageName + " page",
                TestCase.TestType.WEB_UI
        );
        
        loginTest.addStep(new TestCase.TestStep(
                "Navigate to " + pageName,
                "The " + pageName + " page should load successfully"
        ));
        
        loginTest.addStep(new TestCase.TestStep(
                "Enter username in the username field",
                "Username should be accepted"
        ));
        
        loginTest.addStep(new TestCase.TestStep(
                "Enter password in the password field",
                "Password should be masked with asterisks"
        ));
        
        loginTest.addStep(new TestCase.TestStep(
                "Click the login button",
                "User should be redirected to the dashboard"
        ));
        
        testCases.add(loginTest);
        
        return testCases;
    }
    
    /**
     * Sets whether the analyzer should treat the target as a Single Page Application.
     *
     * @param isSPA Whether the target is a SPA
     */
    public void setSPA(boolean isSPA) {
        this.isSPA = isSPA;
    }
    
    /**
     * Checks if a web page is likely to be a Single Page Application.
     *
     * @param url The URL of the web page
     * @return true if the page is likely to be a SPA, false otherwise
     */
    public boolean detectIfSPA(String url) {
        // In a real implementation, this would:
        // 1. Look for SPA frameworks (React, Angular, Vue, etc.)
        // 2. Check for client-side routing
        // 3. Look for app containers and other SPA patterns
        
        // This is a placeholder implementation
        // In practice, we would analyze the page's structure and JavaScript
        return url.contains("spa") || url.contains("app") || url.contains("angular") || 
               url.contains("react") || url.contains("vue");
    }
    
    /**
     * Gets the SPAAnalyzer instance used by this WebAnalyzer.
     *
     * @return The SPAAnalyzer
     */
    public SPAAnalyzer getSpaAnalyzer() {
        return spaAnalyzer;
    }
    
    /**
     * Sets the SPAAnalyzer instance to be used by this WebAnalyzer.
     *
     * @param spaAnalyzer The SPAAnalyzer
     */
    public void setSpaAnalyzer(SPAAnalyzer spaAnalyzer) {
        this.spaAnalyzer = spaAnalyzer;
    }
}
