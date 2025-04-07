package com.testgen.generator;

import com.testgen.exception.TestGenException;
import com.testgen.model.ApiEndpoint;
import com.testgen.model.TestCase;
import com.testgen.model.WebElement;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class JiraTestGeneratorTest {

    private JiraTestGenerator generator;

    @Before
    public void setUp() {
        generator = new JiraTestGenerator();
    }

    @Test(expected = TestGenException.class)
    public void testGenerateFromWebElementsWithNullElements() throws TestGenException {
        generator.generateFromWebElements(null, "https://example.com", "Example Page");
    }

    @Test(expected = TestGenException.class)
    public void testGenerateFromWebElementsWithEmptyElements() throws TestGenException {
        generator.generateFromWebElements(new ArrayList<>(), "https://example.com", "Example Page");
    }

    @Test
    public void testGenerateFromWebElements() throws TestGenException {
        // Create sample web elements
        List<WebElement> elements = new ArrayList<>();
        
        // Create a form element
        WebElement form = new WebElement();
        form.setType("form");
        form.setId("loginForm");
        form.setLocator("css");
        form.setLocatorValue("#loginForm");
        form.setAction("/dashboard");
        form.setMethod("POST");
        elements.add(form);
        
        // Create form input elements
        WebElement usernameInput = new WebElement();
        usernameInput.setType("input-text");
        usernameInput.setId("username");
        usernameInput.setName("username");
        usernameInput.setLocator("css");
        usernameInput.setLocatorValue("#username");
        usernameInput.setParentForm(form.getLocatorValue());
        usernameInput.setPlaceholder("Enter username");
        elements.add(usernameInput);
        
        WebElement passwordInput = new WebElement();
        passwordInput.setType("input-password");
        passwordInput.setId("password");
        passwordInput.setName("password");
        passwordInput.setLocator("css");
        passwordInput.setLocatorValue("#password");
        passwordInput.setParentForm(form.getLocatorValue());
        passwordInput.setPlaceholder("Enter password");
        elements.add(passwordInput);
        
        WebElement submitButton = new WebElement();
        submitButton.setType("input-submit");
        submitButton.setId("loginButton");
        submitButton.setLocator("css");
        submitButton.setLocatorValue("#loginButton");
        submitButton.setParentForm(form.getLocatorValue());
        elements.add(submitButton);
        
        // Create a link element
        WebElement link = new WebElement();
        link.setType("link");
        link.setId("signupLink");
        link.setLocator("css");
        link.setLocatorValue("#signupLink");
        link.setText("Sign Up");
        link.setHref("https://example.com/signup");
        elements.add(link);
        
        // Generate test cases
        List<TestCase> testCases = generator.generateFromWebElements(elements, "https://example.com", "Login Page");
        
        // Verify results
        assertNotNull(testCases);
        assertFalse(testCases.isEmpty());
        
        // Should generate form test case
        boolean hasFormTest = false;
        boolean hasNavigationTest = false;
        boolean hasComprehensiveTest = false;
        
        for (TestCase testCase : testCases) {
            if (testCase.getName().contains("Form Submission")) {
                hasFormTest = true;
                assertEquals(TestCase.TestType.WEB_UI, testCase.getType());
                assertNotNull(testCase.getSteps());
                assertTrue(testCase.getSteps().size() >= 4); // Navigate, inputs, submit, verify
            } else if (testCase.getName().contains("Navigation")) {
                hasNavigationTest = true;
                assertEquals(TestCase.TestType.WEB_UI, testCase.getType());
                assertNotNull(testCase.getSteps());
            } else if (testCase.getName().contains("Comprehensive")) {
                hasComprehensiveTest = true;
                assertEquals(TestCase.TestType.WEB_UI, testCase.getType());
                assertNotNull(testCase.getSteps());
            }
        }
        
        assertTrue("Should generate form test case", hasFormTest);
        assertTrue("Should generate navigation test case", hasNavigationTest);
        assertTrue("Should generate comprehensive test case", hasComprehensiveTest);
        
        // Test Jira format conversion
        List<String> jiraTestCases = generator.convertToJiraFormat(testCases);
        assertNotNull(jiraTestCases);
        assertEquals(testCases.size(), jiraTestCases.size());
        
        // Check format of first test case
        assertTrue(jiraTestCases.get(0).contains("h1. "));
        assertTrue(jiraTestCases.get(0).contains("h2. Test Steps"));
        assertTrue(jiraTestCases.get(0).contains("||Step||Action||Expected Result||"));
    }

    @Test(expected = TestGenException.class)
    public void testGenerateFromApiEndpointsWithNullEndpoints() throws TestGenException {
        generator.generateFromApiEndpoints(null, "Test Collection");
    }

    @Test(expected = TestGenException.class)
    public void testGenerateFromApiEndpointsWithEmptyEndpoints() throws TestGenException {
        generator.generateFromApiEndpoints(new ArrayList<>(), "Test Collection");
    }

    @Test
    public void testGenerateFromApiEndpoints() throws TestGenException {
        // Create sample API endpoints
        List<ApiEndpoint> endpoints = new ArrayList<>();
        
        // GET endpoint
        ApiEndpoint getEndpoint = new ApiEndpoint();
        getEndpoint.setName("Get Users");
        getEndpoint.setMethod("GET");
        getEndpoint.setUrl("https://api.example.com/users");
        getEndpoint.setPath("/users");
        getEndpoint.setHost("https://api.example.com");
        getEndpoint.setDescription("Retrieves a list of users");
        
        Map<String, String> getHeaders = new HashMap<>();
        getHeaders.put("Accept", "application/json");
        getEndpoint.setHeaders(getHeaders);
        
        // Add example response
        Map<String, Object> exampleResponse = new HashMap<>();
        exampleResponse.put("code", 200);
        exampleResponse.put("body", "{\"users\": [{\"id\": 1, \"name\": \"Test User\"}]}");
        getEndpoint.getExampleResponses().add(exampleResponse);
        
        endpoints.add(getEndpoint);
        
        // POST endpoint
        ApiEndpoint postEndpoint = new ApiEndpoint();
        postEndpoint.setName("Create User");
        postEndpoint.setMethod("POST");
        postEndpoint.setUrl("https://api.example.com/users");
        postEndpoint.setPath("/users");
        postEndpoint.setHost("https://api.example.com");
        postEndpoint.setDescription("Creates a new user");
        
        Map<String, String> postHeaders = new HashMap<>();
        postHeaders.put("Content-Type", "application/json");
        postEndpoint.setHeaders(postHeaders);
        
        postEndpoint.setRequestBody("{\"name\": \"New User\", \"email\": \"user@example.com\"}");
        postEndpoint.setRequestBodyType("json");
        
        // Add example response
        Map<String, Object> postExampleResponse = new HashMap<>();
        postExampleResponse.put("code", 201);
        postExampleResponse.put("body", "{\"id\": 2, \"name\": \"New User\"}");
        postEndpoint.getExampleResponses().add(postExampleResponse);
        
        endpoints.add(postEndpoint);
        
        // Generate test cases
        List<TestCase> testCases = generator.generateFromApiEndpoints(endpoints, "User API");
        
        // Verify results
        assertNotNull(testCases);
        assertEquals(2, testCases.size());
        
        // Check GET test case
        TestCase getTestCase = testCases.get(0);
        assertEquals("API Test: Get Users", getTestCase.getName());
        assertEquals(TestCase.TestType.API, getTestCase.getType());
        assertNotNull(getTestCase.getSteps());
        assertTrue(getTestCase.getSteps().size() >= 3); // Prepare, send, verify
        
        // Check POST test case
        TestCase postTestCase = testCases.get(1);
        assertEquals("API Test: Create User", postTestCase.getName());
        assertEquals(TestCase.TestType.API, postTestCase.getType());
        assertNotNull(postTestCase.getSteps());
        assertTrue(postTestCase.getSteps().size() >= 4); // Prepare, body, send, verify
        
        // Test Jira format conversion
        List<String> jiraTestCases = generator.convertToJiraFormat(testCases);
        assertNotNull(jiraTestCases);
        assertEquals(testCases.size(), jiraTestCases.size());
        
        // Check format of test cases
        for (String jiraTestCase : jiraTestCases) {
            assertTrue(jiraTestCase.contains("h1. API Test:"));
            assertTrue(jiraTestCase.contains("h2. Test Steps"));
            assertTrue(jiraTestCase.contains("||Step||Action||Expected Result||"));
        }
    }

    @Test
    public void testExtractPageNameFromUrl() throws Exception {
        // Use reflection to access private method
        java.lang.reflect.Method extractMethod = JiraTestGenerator.class.getDeclaredMethod(
                "extractPageName", String.class);
        extractMethod.setAccessible(true);
        
        // Test different URLs
        assertEquals("Homepage", extractMethod.invoke(generator, "https://example.com"));
        assertEquals("Login", extractMethod.invoke(generator, "https://example.com/login"));
        assertEquals("User", extractMethod.invoke(generator, "https://example.com/user/123"));
        assertEquals("Products", extractMethod.invoke(generator, "https://example.com/products?category=electronics"));
    }
}
