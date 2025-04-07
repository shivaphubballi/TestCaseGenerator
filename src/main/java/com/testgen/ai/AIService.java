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
     * Enhances test cases for a web page based on analysis of elements.
     *
     * @param elements The web elements on the page
     * @param baseTestCases The base test cases to enhance
     * @return Enhanced test cases
     */
    List<TestCase> enhanceWebTestCases(List<WebElement> elements, List<TestCase> baseTestCases);
    
    /**
     * Enhances a single test case with additional steps or assertions.
     *
     * @param testCase The test case to enhance
     * @return The enhanced test case
     */
    default TestCase enhanceTestCase(TestCase testCase) {
        // Default implementation simply returns the original test case
        return testCase;
    }
    
    /**
     * Generates security-focused test cases based on existing test cases.
     *
     * @param baseTestCases The base test cases to analyze
     * @param url The URL of the web page (for additional context)
     * @return Security-focused test cases
     */
    default List<TestCase> generateSecurityTestCases(List<TestCase> baseTestCases, String url) {
        // Default implementation can be empty or provide basic security tests
        return List.of();
    }
    
    /**
     * Generates accessibility-focused test cases based on existing test cases.
     *
     * @param baseTestCases The base test cases to analyze
     * @param url The URL of the web page (for additional context)
     * @return Accessibility-focused test cases
     */
    default List<TestCase> generateAccessibilityTestCases(List<TestCase> baseTestCases, String url) {
        // Default implementation can be empty or provide basic accessibility tests
        return List.of();
    }
    
    /**
     * Generates performance-focused test cases based on existing test cases.
     *
     * @param baseTestCases The base test cases to analyze
     * @param url The URL of the web page (for additional context)
     * @return Performance-focused test cases
     */
    default List<TestCase> generatePerformanceTestCases(List<TestCase> baseTestCases, String url) {
        // Default implementation can be empty or provide basic performance tests
        return List.of();
    }
    
    /**
     * Generates test cases for a Single Page Application (SPA).
     *
     * @param elements The web elements on the page
     * @param baseTestCases The base test cases to enhance
     * @param url The URL of the SPA (for additional context)
     * @return SPA-focused test cases
     */
    default List<TestCase> generateSPATestCases(List<WebElement> elements, List<TestCase> baseTestCases, String url) {
        // Default implementation uses the standard enhanceWebTestCases method
        return enhanceWebTestCases(elements, baseTestCases);
    }
}
