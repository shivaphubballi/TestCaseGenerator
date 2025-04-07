package com.testgen.ai;

import com.testgen.model.ApiEndpoint;
import com.testgen.model.TestCase;
import com.testgen.model.WebElement;

import java.util.List;

/**
 * Interface for AI services that enhance test case generation.
 * Implementations can use different AI providers or techniques.
 */
public interface AIService {
    
    /**
     * Enhances test cases for web UI testing by suggesting additional test scenarios,
     * edge cases, and validation steps based on the identified web elements.
     * 
     * @param elements The web elements identified on the page
     * @param baseTestCases Initial test cases created from basic analysis
     * @return Enhanced list of test cases with improved coverage
     */
    List<TestCase> enhanceWebTestCases(List<WebElement> elements, List<TestCase> baseTestCases);
    
    /**
     * Enhances test cases for API testing by suggesting additional test scenarios,
     * parameter variations, and validation steps based on the identified endpoints.
     * 
     * @param endpoints The API endpoints identified from the Postman collection
     * @param baseTestCases Initial test cases created from basic analysis
     * @return Enhanced list of test cases with improved coverage
     */
    List<TestCase> enhanceApiTestCases(List<ApiEndpoint> endpoints, List<TestCase> baseTestCases);
    
    /**
     * Generates potential edge cases for a specific web element.
     * 
     * @param element The web element to analyze
     * @return A list of test cases focusing on edge cases for the element
     */
    List<TestCase> generateWebElementEdgeCases(WebElement element);
    
    /**
     * Generates potential edge cases for a specific API endpoint.
     * 
     * @param endpoint The API endpoint to analyze
     * @return A list of test cases focusing on edge cases for the endpoint
     */
    List<TestCase> generateApiEndpointEdgeCases(ApiEndpoint endpoint);
    
    /**
     * Suggests security test cases for web UI.
     * 
     * @param elements The web elements identified on the page
     * @return A list of security-focused test cases
     */
    List<TestCase> suggestWebSecurityTests(List<WebElement> elements);
    
    /**
     * Suggests security test cases for API endpoints.
     * 
     * @param endpoints The API endpoints identified from the Postman collection
     * @return A list of security-focused test cases
     */
    List<TestCase> suggestApiSecurityTests(List<ApiEndpoint> endpoints);
    
    /**
     * Suggests accessibility test cases for web UI.
     * 
     * @param elements The web elements identified on the page
     * @return A list of accessibility-focused test cases
     */
    List<TestCase> suggestAccessibilityTests(List<WebElement> elements);
    
    /**
     * Analyzes existing test coverage and suggests improvements.
     * 
     * @param existingTests The existing test cases
     * @return Suggestions for improving test coverage
     */
    String analyzeCoverageGaps(List<TestCase> existingTests);
}
