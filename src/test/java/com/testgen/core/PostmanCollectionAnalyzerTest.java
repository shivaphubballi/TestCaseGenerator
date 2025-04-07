package com.testgen.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testgen.exception.TestGenException;
import com.testgen.model.ApiEndpoint;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class PostmanCollectionAnalyzerTest {

    private PostmanCollectionAnalyzer analyzer;
    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        analyzer = new PostmanCollectionAnalyzer();
        objectMapper = new ObjectMapper();
    }

    @Test(expected = TestGenException.class)
    public void testAnalyzeCollectionWithNullPath() throws TestGenException {
        analyzer.analyzeCollection(null);
    }

    @Test(expected = TestGenException.class)
    public void testAnalyzeCollectionWithEmptyPath() throws TestGenException {
        analyzer.analyzeCollection("");
    }

    @Test(expected = TestGenException.class)
    public void testAnalyzeCollectionFromJsonWithNullJson() throws TestGenException {
        analyzer.analyzeCollectionFromJson(null);
    }

    @Test(expected = TestGenException.class)
    public void testAnalyzeCollectionFromJsonWithEmptyJson() throws TestGenException {
        analyzer.analyzeCollectionFromJson("");
    }

    @Test
    public void testAnalyzeCollectionFromJson() throws Exception {
        // Create a simple Postman collection JSON
        String collectionJson = "{\n" +
                "  \"info\": {\n" +
                "    \"_postman_id\": \"test-id\",\n" +
                "    \"name\": \"Test Collection\",\n" +
                "    \"schema\": \"https://schema.getpostman.com/json/collection/v2.1.0/collection.json\"\n" +
                "  },\n" +
                "  \"item\": [\n" +
                "    {\n" +
                "      \"name\": \"Test Request\",\n" +
                "      \"request\": {\n" +
                "        \"method\": \"GET\",\n" +
                "        \"url\": {\n" +
                "          \"raw\": \"https://api.example.com/users\",\n" +
                "          \"protocol\": \"https\",\n" +
                "          \"host\": [\"api\", \"example\", \"com\"],\n" +
                "          \"path\": [\"users\"]\n" +
                "        },\n" +
                "        \"header\": [\n" +
                "          {\n" +
                "            \"key\": \"Content-Type\",\n" +
                "            \"value\": \"application/json\"\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      \"response\": [\n" +
                "        {\n" +
                "          \"name\": \"Success\",\n" +
                "          \"code\": 200,\n" +
                "          \"body\": \"{\\\"id\\\": 1, \\\"name\\\": \\\"Test User\\\"}\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        List<ApiEndpoint> endpoints = analyzer.analyzeCollectionFromJson(collectionJson);
        
        // Verify results
        assertNotNull(endpoints);
        assertEquals(1, endpoints.size());
        
        ApiEndpoint endpoint = endpoints.get(0);
        assertEquals("Test Request", endpoint.getName());
        assertEquals("GET", endpoint.getMethod());
        assertEquals("https://api.example.com/users", endpoint.getUrl());
        assertEquals("/users", endpoint.getPath());
        
        // Verify headers
        Map<String, String> headers = endpoint.getHeaders();
        assertNotNull(headers);
        assertEquals("application/json", headers.get("Content-Type"));
        
        // Verify examples
        List<Map<String, Object>> examples = endpoint.getExampleResponses();
        assertNotNull(examples);
        assertEquals(1, examples.size());
        assertEquals(200, examples.get(0).get("code"));
    }

    @Test
    public void testParseRequest() throws Exception {
        // Create a simple request node
        String requestJson = "{\n" +
                "  \"name\": \"Create User\",\n" +
                "  \"request\": {\n" +
                "    \"method\": \"POST\",\n" +
                "    \"url\": {\n" +
                "      \"raw\": \"https://api.example.com/users\",\n" +
                "      \"protocol\": \"https\",\n" +
                "      \"host\": [\"api\", \"example\", \"com\"],\n" +
                "      \"path\": [\"users\"]\n" +
                "    },\n" +
                "    \"header\": [\n" +
                "      {\n" +
                "        \"key\": \"Content-Type\",\n" +
                "        \"value\": \"application/json\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"body\": {\n" +
                "      \"mode\": \"raw\",\n" +
                "      \"raw\": \"{\\\"name\\\": \\\"New User\\\", \\\"email\\\": \\\"user@example.com\\\"}\",\n" +
                "      \"options\": {\n" +
                "        \"raw\": {\n" +
                "          \"language\": \"json\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        JsonNode requestNode = objectMapper.readTree(requestJson);
        
        // Use reflection to access private method
        java.lang.reflect.Method parseMethod = PostmanCollectionAnalyzer.class.getDeclaredMethod(
                "parseRequest", JsonNode.class, String.class, String.class, Map.class, Map.class);
        parseMethod.setAccessible(true);
        
        ApiEndpoint endpoint = (ApiEndpoint) parseMethod.invoke(analyzer, requestNode, "TestFolder", 
                "TestCollection", Map.of(), Map.of());
        
        // Verify results
        assertNotNull(endpoint);
        assertEquals("Create User", endpoint.getName());
        assertEquals("POST", endpoint.getMethod());
        assertEquals("https://api.example.com/users", endpoint.getUrl());
        assertEquals("/users", endpoint.getPath());
        assertEquals("json", endpoint.getRequestBodyType());
        assertTrue(endpoint.getRequestBody().contains("New User"));
    }

    @Test
    public void testProcessVariables() throws Exception {
        // Setup collection and folder variables
        Map<String, String> collectionVars = Map.of(
                "baseUrl", "https://api.example.com",
                "version", "v1",
                "commonVar", "collection-value"
        );
        
        Map<String, String> folderVars = Map.of(
                "endpoint", "users",
                "commonVar", "folder-value"  // This should take precedence
        );
        
        String input = "{{baseUrl}}/{{version}}/{{endpoint}}?var={{commonVar}}";
        
        // Use reflection to access private method
        java.lang.reflect.Method processMethod = PostmanCollectionAnalyzer.class.getDeclaredMethod(
                "processVariables", String.class, Map.class, Map.class);
        processMethod.setAccessible(true);
        
        String result = (String) processMethod.invoke(analyzer, input, collectionVars, folderVars);
        
        // Verify results
        assertEquals("https://api.example.com/v1/users?var=folder-value", result);
    }
}
