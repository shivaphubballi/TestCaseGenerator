package com.testgen.generator;

import com.testgen.exception.TestGenException;
import com.testgen.model.ApiEndpoint;
import com.testgen.model.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class RestAssuredTestGeneratorTest {

    private RestAssuredTestGenerator generator;

    @Before
    public void setUp() {
        generator = new RestAssuredTestGenerator();
    }

    @Test(expected = TestGenException.class)
    public void testGenerateTestClassWithNullEndpoints() throws TestGenException {
        generator.generateTestClass(null, "TestClass", "com.example");
    }

    @Test(expected = TestGenException.class)
    public void testGenerateTestClassWithEmptyEndpoints() throws TestGenException {
        generator.generateTestClass(new ArrayList<>(), "TestClass", "com.example");
    }

    @Test
    public void testGenerateTestClass() throws TestGenException {
        // Create sample API endpoints
        List<ApiEndpoint> endpoints = new ArrayList<>();
        
        // GET endpoint
        ApiEndpoint getEndpoint = new ApiEndpoint();
        getEndpoint.setName("Get Users");
        getEndpoint.setMethod("GET");
        getEndpoint.setUrl("https://api.example.com/users");
        getEndpoint.setPath("/users");
        getEndpoint.setHost("https://api.example.com");
        
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
        
        // Generate test class
        String testCode = generator.generateTestClass(endpoints, "UserApiTest", "com.example.tests");
        
        // Verify results
        assertNotNull(testCode);
        assertTrue(testCode.contains("package com.example.tests;"));
        assertTrue(testCode.contains("public class UserApiTest {"));
        assertTrue(testCode.contains("private String baseUrl = \"https://api.example.com\";"));
        assertTrue(testCode.contains("RestAssured.baseURI = baseUrl;"));
        assertTrue(testCode.contains("Response response = request.get(\"/users\");"));
        assertTrue(testCode.contains("Response response = request.post(\"/users\");"));
        assertTrue(testCode.contains("Assert.assertEquals(\"Status code should be 200\", 200, response.getStatusCode());"));
        assertTrue(testCode.contains("Assert.assertEquals(\"Status code should be 201\", 201, response.getStatusCode());"));
        assertTrue(testCode.contains("String requestBody = \"{\\\"name\\\": \\\"New User\\\", \\\"email\\\": \\\"user@example.com\\\"}\";"));
    }

    @Test(expected = TestGenException.class)
    public void testGenerateTestClassFromTestCasesWithNullTestCases() throws TestGenException {
        generator.generateTestClassFromTestCases(null, "TestClass", "com.example");
    }

    @Test(expected = TestGenException.class)
    public void testGenerateTestClassFromTestCasesWithEmptyTestCases() throws TestGenException {
        generator.generateTestClassFromTestCases(new ArrayList<>(), "TestClass", "com.example");
    }

    @Test
    public void testGenerateTestClassFromTestCases() throws TestGenException {
        // Create sample test cases
        List<TestCase> testCases = new ArrayList<>();
        
        // GET test case
        TestCase getTestCase = new TestCase();
        getTestCase.setName("Get Users Test");
        getTestCase.setType(TestCase.TestType.API);
        
        Map<String, String> getMetadata = new HashMap<>();
        getMetadata.put("baseUrl", "https://api.example.com");
        getMetadata.put("url", "/users");
        getMetadata.put("method", "GET");
        getMetadata.put("expectedStatus", "200");
        getTestCase.setMetadata(getMetadata);
        
        getTestCase.addStep("Send GET request to /users endpoint", "Response received with status 200");
        
        TestCase.TestStep verifyStep = new TestCase.TestStep();
        verifyStep.setAction("Verify response contains user data");
        verifyStep.setExpectedResult("User data is present in response");
        verifyStep.addTestData("jsonPath", "users[0].name");
        verifyStep.addTestData("expectedValue", "Test User");
        getTestCase.addStep(verifyStep);
        
        testCases.add(getTestCase);
        
        // POST test case
        TestCase postTestCase = new TestCase();
        postTestCase.setName("Create User Test");
        postTestCase.setType(TestCase.TestType.API);
        
        Map<String, String> postMetadata = new HashMap<>();
        postMetadata.put("baseUrl", "https://api.example.com");
        postMetadata.put("url", "/users");
        postMetadata.put("method", "POST");
        postMetadata.put("contentType", "application/json");
        postMetadata.put("expectedStatus", "201");
        postTestCase.setMetadata(postMetadata);
        
        TestCase.TestStep bodyStep = new TestCase.TestStep();
        bodyStep.setAction("Set request body");
        bodyStep.setExpectedResult("Request body is set");
        bodyStep.addTestData("body", "{\"name\": \"New User\", \"email\": \"user@example.com\"}");
        postTestCase.addStep(bodyStep);
        
        postTestCase.addStep("Send POST request to /users endpoint", "Response received with status 201");
        
        TestCase.TestStep postVerifyStep = new TestCase.TestStep();
        postVerifyStep.setAction("Verify response contains created user");
        postVerifyStep.setExpectedResult("Created user data is present in response");
        postVerifyStep.addTestData("jsonPath", "id");
        postVerifyStep.addTestData("expectedValue", "2");
        postTestCase.addStep(postVerifyStep);
        
        testCases.add(postTestCase);
        
        // Generate test class
        String testCode = generator.generateTestClassFromTestCases(testCases, "UserApiTest", "com.example.tests");
        
        // Verify results
        assertNotNull(testCode);
        assertTrue(testCode.contains("package com.example.tests;"));
        assertTrue(testCode.contains("public class UserApiTest {"));
        assertTrue(testCode.contains("private String baseUrl = \"https://api.example.com\";"));
        assertTrue(testCode.contains("RestAssured.baseURI = baseUrl;"));
        assertTrue(testCode.contains("Response response = request.get(\"/users\");"));
        assertTrue(testCode.contains("Response response = request.post(\"/users\");"));
        assertTrue(testCode.contains("Assert.assertEquals(\"Status code should be 200\", 200, response.getStatusCode());"));
        assertTrue(testCode.contains("Assert.assertEquals(\"Status code should be 201\", 201, response.getStatusCode());"));
        assertTrue(testCode.contains("String expectedUsersName = \"Test User\";"));
        assertTrue(testCode.contains("String requestBody = \"{\\\"name\\\": \\\"New User\\\", \\\"email\\\": \\\"user@example.com\\\"}\";"));
    }
}
