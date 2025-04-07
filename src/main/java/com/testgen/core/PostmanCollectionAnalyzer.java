package com.testgen.core;

import com.testgen.exception.TestGenException;
import com.testgen.model.ApiEndpoint;
import com.testgen.model.TestCase;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Analyzes Postman collections to extract endpoints and generate test cases.
 */
public class PostmanCollectionAnalyzer {
    
    /**
     * Analyzes a Postman collection to extract API endpoints.
     * 
     * @param collectionPath The path to the Postman collection file
     * @return A list of API endpoints
     * @throws TestGenException If an error occurs during analysis
     */
    public List<ApiEndpoint> analyzeCollection(String collectionPath) throws TestGenException {
        if (collectionPath == null || collectionPath.isEmpty()) {
            throw new TestGenException("Collection path cannot be null or empty");
        }
        
        try {
            String json = new String(Files.readAllBytes(Paths.get(collectionPath)));
            return analyzeCollectionFromJson(json);
        } catch (IOException e) {
            throw new TestGenException("Error reading Postman collection file: " + e.getMessage(), e);
        }
    }
    
    /**
     * Analyzes a Postman collection in JSON format to extract API endpoints.
     * 
     * @param json The Postman collection in JSON format
     * @return A list of API endpoints
     * @throws TestGenException If an error occurs during analysis
     */
    public List<ApiEndpoint> analyzeCollectionFromJson(String json) throws TestGenException {
        if (json == null || json.isEmpty()) {
            throw new TestGenException("Collection JSON cannot be null or empty");
        }
        
        // TODO: Implement Postman collection analysis
        // This is a placeholder implementation
        List<ApiEndpoint> endpoints = new ArrayList<>();
        
        // Add some sample endpoints for demonstration
        ApiEndpoint getUsersEndpoint = new ApiEndpoint();
        getUsersEndpoint.setName("Get Users");
        getUsersEndpoint.setUrl("https://api.example.com/users");
        getUsersEndpoint.setMethod("GET");
        endpoints.add(getUsersEndpoint);
        
        ApiEndpoint createUserEndpoint = new ApiEndpoint();
        createUserEndpoint.setName("Create User");
        createUserEndpoint.setUrl("https://api.example.com/users");
        createUserEndpoint.setMethod("POST");
        endpoints.add(createUserEndpoint);
        
        ApiEndpoint updateUserEndpoint = new ApiEndpoint();
        updateUserEndpoint.setName("Update User");
        updateUserEndpoint.setUrl("https://api.example.com/users/{id}");
        updateUserEndpoint.setMethod("PUT");
        endpoints.add(updateUserEndpoint);
        
        ApiEndpoint deleteUserEndpoint = new ApiEndpoint();
        deleteUserEndpoint.setName("Delete User");
        deleteUserEndpoint.setUrl("https://api.example.com/users/{id}");
        deleteUserEndpoint.setMethod("DELETE");
        endpoints.add(deleteUserEndpoint);
        
        return endpoints;
    }
    
    /**
     * Generates test cases for a list of API endpoints.
     *
     * @param endpoints The API endpoints
     * @return A list of test cases
     */
    public List<TestCase> generateTestCases(List<ApiEndpoint> endpoints) {
        List<TestCase> testCases = new ArrayList<>();
        
        for (ApiEndpoint endpoint : endpoints) {
            TestCase testCase = new TestCase(
                    endpoint.getName(),
                    "Test the " + endpoint.getMethod() + " " + endpoint.getUrl() + " endpoint",
                    TestCase.TestType.API
            );
            
            // Add steps based on the HTTP method
            switch (endpoint.getMethod()) {
                case "GET":
                    testCase.addStep(new TestCase.TestStep(
                            "Send GET request to " + endpoint.getUrl(),
                            "Response status code should be 200 OK"
                    ));
                    
                    testCase.addStep(new TestCase.TestStep(
                            "Verify response format",
                            "Response should be in the expected format (JSON, XML, etc.)"
                    ));
                    break;
                    
                case "POST":
                    testCase.addStep(new TestCase.TestStep(
                            "Send POST request to " + endpoint.getUrl() + " with valid data",
                            "Response status code should be 201 Created"
                    ));
                    
                    testCase.addStep(new TestCase.TestStep(
                            "Verify the created resource",
                            "The response should contain the created resource with an ID"
                    ));
                    break;
                    
                case "PUT":
                    testCase.addStep(new TestCase.TestStep(
                            "Send PUT request to " + endpoint.getUrl() + " with valid data",
                            "Response status code should be 200 OK"
                    ));
                    
                    testCase.addStep(new TestCase.TestStep(
                            "Verify the updated resource",
                            "The response should contain the updated resource"
                    ));
                    break;
                    
                case "DELETE":
                    testCase.addStep(new TestCase.TestStep(
                            "Send DELETE request to " + endpoint.getUrl(),
                            "Response status code should be 204 No Content"
                    ));
                    
                    testCase.addStep(new TestCase.TestStep(
                            "Verify the resource was deleted",
                            "A subsequent GET request should return 404 Not Found"
                    ));
                    break;
                    
                default:
                    testCase.addStep(new TestCase.TestStep(
                            "Send " + endpoint.getMethod() + " request to " + endpoint.getUrl(),
                            "Response should have an appropriate status code"
                    ));
                    break;
            }
            
            testCases.add(testCase);
        }
        
        return testCases;
    }
    
    /**
     * Analyzes a Postman collection to extract API endpoints.
     * This overload is used when parsing the JSON directly.
     * 
     * @param json The Postman collection in JSON format
     * @return A list of API endpoints
     * @throws TestGenException If an error occurs during analysis
     */
    public List<ApiEndpoint> analyze(String json) throws TestGenException {
        return analyzeCollectionFromJson(json);
    }
}
