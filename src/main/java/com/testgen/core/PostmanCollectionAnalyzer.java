package com.testgen.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testgen.exception.TestGenException;
import com.testgen.model.ApiEndpoint;
import com.testgen.util.JsonUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Analyzes Postman collections to extract API endpoints and their details.
 */
public class PostmanCollectionAnalyzer {
    private static final Logger LOGGER = Logger.getLogger(PostmanCollectionAnalyzer.class.getName());
    private final ObjectMapper objectMapper;
    
    public PostmanCollectionAnalyzer() {
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Analyzes a Postman collection file and extracts API endpoints.
     *
     * @param collectionFilePath Path to the Postman collection JSON file
     * @return A list of ApiEndpoint objects representing API endpoints
     * @throws TestGenException If the collection cannot be analyzed
     */
    public List<ApiEndpoint> analyzeCollection(String collectionFilePath) throws TestGenException {
        LOGGER.info("Analyzing Postman collection: " + collectionFilePath);
        
        if (collectionFilePath == null || collectionFilePath.trim().isEmpty()) {
            throw new TestGenException("Collection file path cannot be null or empty");
        }
        
        try {
            JsonNode rootNode = objectMapper.readTree(new File(collectionFilePath));
            return parseCollection(rootNode);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to analyze Postman collection: " + collectionFilePath, e);
            throw new TestGenException("Failed to analyze Postman collection: " + e.getMessage(), e);
        }
    }
    
    /**
     * Analyzes a Postman collection from a JSON string and extracts API endpoints.
     *
     * @param collectionJson JSON string of the Postman collection
     * @return A list of ApiEndpoint objects representing API endpoints
     * @throws TestGenException If the collection cannot be analyzed
     */
    public List<ApiEndpoint> analyzeCollectionFromJson(String collectionJson) throws TestGenException {
        LOGGER.info("Analyzing Postman collection from JSON");
        
        if (collectionJson == null || collectionJson.trim().isEmpty()) {
            throw new TestGenException("Collection JSON cannot be null or empty");
        }
        
        try {
            JsonNode rootNode = objectMapper.readTree(collectionJson);
            return parseCollection(rootNode);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to analyze Postman collection from JSON", e);
            throw new TestGenException("Failed to analyze Postman collection from JSON: " + e.getMessage(), e);
        }
    }
    
    /**
     * Parses the Postman collection JSON and extracts API endpoints.
     *
     * @param rootNode The root JSON node of the Postman collection
     * @return A list of ApiEndpoint objects
     * @throws TestGenException If the collection structure is invalid
     */
    private List<ApiEndpoint> parseCollection(JsonNode rootNode) throws TestGenException {
        List<ApiEndpoint> endpoints = new ArrayList<>();
        
        // Check collection format (v2.x or v2.1.0)
        if (!rootNode.has("info") || !rootNode.get("info").has("schema")) {
            throw new TestGenException("Invalid Postman collection format: missing info.schema");
        }
        
        String schema = rootNode.get("info").get("schema").asText();
        if (!schema.contains("v2")) {
            throw new TestGenException("Unsupported Postman collection version. Only v2.x is supported.");
        }
        
        String collectionName = rootNode.path("info").path("name").asText("Unnamed Collection");
        
        // Extract collection variables if present
        Map<String, String> collectionVariables = extractCollectionVariables(rootNode);
        
        // Parse items (folders and requests)
        if (rootNode.has("item")) {
            parseItems(rootNode.get("item"), endpoints, "", collectionName, collectionVariables, new HashMap<>());
        } else {
            LOGGER.warning("No items found in the collection");
        }
        
        return endpoints;
    }
    
    /**
     * Extracts collection-level variables from the Postman collection.
     *
     * @param rootNode The root JSON node of the Postman collection
     * @return A map of variable names to their values
     */
    private Map<String, String> extractCollectionVariables(JsonNode rootNode) {
        Map<String, String> variables = new HashMap<>();
        
        // Extract variables from collection variables
        if (rootNode.has("variable")) {
            JsonNode varsNode = rootNode.get("variable");
            if (varsNode.isArray()) {
                for (JsonNode var : varsNode) {
                    String key = var.path("key").asText();
                    String value = var.path("value").asText("");
                    if (!key.isEmpty()) {
                        variables.put(key, value);
                    }
                }
            }
        }
        
        return variables;
    }
    
    /**
     * Recursively parses items (folders and requests) in the Postman collection.
     *
     * @param itemsNode JSON node containing items
     * @param endpoints List to add extracted endpoints to
     * @param parentPath Path to the current item (for nested folders)
     * @param collectionName Name of the collection
     * @param collectionVariables Collection-level variables
     * @param folderVariables Variables defined at the folder level
     */
    private void parseItems(JsonNode itemsNode, List<ApiEndpoint> endpoints, String parentPath,
                           String collectionName, Map<String, String> collectionVariables,
                           Map<String, String> folderVariables) {
        if (!itemsNode.isArray()) {
            LOGGER.warning("Items node is not an array");
            return;
        }
        
        for (JsonNode item : itemsNode) {
            // Check if it's a folder (has items)
            if (item.has("item")) {
                String folderName = item.path("name").asText("Unnamed Folder");
                String newPath = parentPath.isEmpty() ? folderName : parentPath + "/" + folderName;
                
                // Extract folder variables
                Map<String, String> newFolderVariables = new HashMap<>(folderVariables);
                if (item.has("variable")) {
                    JsonNode varsNode = item.get("variable");
                    if (varsNode.isArray()) {
                        for (JsonNode var : varsNode) {
                            String key = var.path("key").asText();
                            String value = var.path("value").asText("");
                            if (!key.isEmpty()) {
                                newFolderVariables.put(key, value);
                            }
                        }
                    }
                }
                
                // Recursively process items in the folder
                parseItems(item.get("item"), endpoints, newPath, collectionName, collectionVariables, newFolderVariables);
            } else {
                // It's a request
                try {
                    ApiEndpoint endpoint = parseRequest(item, parentPath, collectionName, collectionVariables, folderVariables);
                    if (endpoint != null) {
                        endpoints.add(endpoint);
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Error parsing request: " + item.path("name").asText(), e);
                }
            }
        }
    }
    
    /**
     * Parses a request item from the Postman collection.
     *
     * @param requestNode JSON node containing the request
     * @param folderPath Path to the folder containing the request
     * @param collectionName Name of the collection
     * @param collectionVariables Collection-level variables
     * @param folderVariables Variables defined at the folder level
     * @return An ApiEndpoint object representing the request
     */
    private ApiEndpoint parseRequest(JsonNode requestNode, String folderPath, String collectionName,
                                    Map<String, String> collectionVariables, Map<String, String> folderVariables) {
        String name = requestNode.path("name").asText("Unnamed Request");
        
        // Check if request object exists
        if (!requestNode.has("request")) {
            LOGGER.warning("Request node does not have 'request' field: " + name);
            return null;
        }
        
        JsonNode request = requestNode.get("request");
        
        // Extract method
        String method = request.path("method").asText("GET");
        
        // Extract URL
        String url = "";
        String host = "";
        String path = "";
        Map<String, String> queryParams = new HashMap<>();
        
        if (request.has("url")) {
            JsonNode urlNode = request.get("url");
            
            // URL can be a string or an object
            if (urlNode.isTextual()) {
                url = urlNode.asText();
            } else {
                // If URL is an object, we need to construct it
                if (urlNode.has("raw")) {
                    url = urlNode.get("raw").asText();
                }
                
                // Extract host (base URL)
                if (urlNode.has("host") && urlNode.get("host").isArray()) {
                    StringBuilder hostBuilder = new StringBuilder();
                    JsonNode hostArray = urlNode.get("host");
                    
                    // Check if protocol exists
                    String protocol = "";
                    if (urlNode.has("protocol")) {
                        protocol = urlNode.get("protocol").asText() + "://";
                    }
                    
                    hostBuilder.append(protocol);
                    
                    for (int i = 0; i < hostArray.size(); i++) {
                        hostBuilder.append(hostArray.get(i).asText());
                        if (i < hostArray.size() - 1) {
                            hostBuilder.append(".");
                        }
                    }
                    
                    host = hostBuilder.toString();
                }
                
                // Extract path
                if (urlNode.has("path") && urlNode.get("path").isArray()) {
                    StringBuilder pathBuilder = new StringBuilder();
                    JsonNode pathArray = urlNode.get("path");
                    
                    for (int i = 0; i < pathArray.size(); i++) {
                        pathBuilder.append("/").append(pathArray.get(i).asText());
                    }
                    
                    path = pathBuilder.toString();
                }
                
                // Extract query parameters
                if (urlNode.has("query") && urlNode.get("query").isArray()) {
                    JsonNode queryArray = urlNode.get("query");
                    
                    for (JsonNode query : queryArray) {
                        String key = query.path("key").asText();
                        String value = query.path("value").asText("");
                        if (!key.isEmpty()) {
                            queryParams.put(key, value);
                        }
                    }
                }
            }
        }
        
        // Use combined URL if host and path were extracted successfully
        if (!host.isEmpty() && !path.isEmpty()) {
            url = host + path;
        }
        
        // Process variables in the URL
        url = processVariables(url, collectionVariables, folderVariables);
        
        // Extract headers
        Map<String, String> headers = new HashMap<>();
        if (request.has("header") && request.get("header").isArray()) {
            JsonNode headerArray = request.get("header");
            
            for (JsonNode header : headerArray) {
                if (header.has("disabled") && header.get("disabled").asBoolean()) {
                    continue; // Skip disabled headers
                }
                
                String key = header.path("key").asText();
                String value = header.path("value").asText("");
                if (!key.isEmpty()) {
                    headers.put(key, processVariables(value, collectionVariables, folderVariables));
                }
            }
        }
        
        // Extract request body
        String requestBody = "";
        String requestBodyType = "";
        Map<String, String> formData = new HashMap<>();
        
        if (request.has("body")) {
            JsonNode bodyNode = request.get("body");
            String mode = bodyNode.path("mode").asText("");
            
            switch (mode) {
                case "raw":
                    requestBody = bodyNode.path("raw").asText("");
                    requestBodyType = "raw";
                    
                    // Try to determine if it's JSON or other format
                    if (bodyNode.has("options") && bodyNode.get("options").has("raw") && 
                        bodyNode.get("options").get("raw").has("language")) {
                        String language = bodyNode.get("options").get("raw").get("language").asText("");
                        if ("json".equalsIgnoreCase(language)) {
                            requestBodyType = "json";
                        } else {
                            requestBodyType = language.toLowerCase();
                        }
                    } else if (requestBody.trim().startsWith("{") || requestBody.trim().startsWith("[")) {
                        requestBodyType = "json";
                    }
                    
                    requestBody = processVariables(requestBody, collectionVariables, folderVariables);
                    break;
                    
                case "urlencoded":
                    requestBodyType = "urlencoded";
                    JsonNode urlEncodedArray = bodyNode.path("urlencoded");
                    if (urlEncodedArray.isArray()) {
                        for (JsonNode param : urlEncodedArray) {
                            if (param.has("disabled") && param.get("disabled").asBoolean()) {
                                continue; // Skip disabled params
                            }
                            
                            String key = param.path("key").asText();
                            String value = param.path("value").asText("");
                            if (!key.isEmpty()) {
                                formData.put(key, processVariables(value, collectionVariables, folderVariables));
                            }
                        }
                    }
                    break;
                    
                case "formdata":
                    requestBodyType = "formdata";
                    JsonNode formDataArray = bodyNode.path("formdata");
                    if (formDataArray.isArray()) {
                        for (JsonNode param : formDataArray) {
                            if (param.has("disabled") && param.get("disabled").asBoolean()) {
                                continue; // Skip disabled params
                            }
                            
                            String key = param.path("key").asText();
                            String value = param.path("value").asText("");
                            if (!key.isEmpty()) {
                                formData.put(key, processVariables(value, collectionVariables, folderVariables));
                            }
                        }
                    }
                    break;
                    
                default:
                    break;
            }
        }
        
        // Extract example responses
        List<Map<String, Object>> exampleResponses = new ArrayList<>();
        if (requestNode.has("response") && requestNode.get("response").isArray()) {
            JsonNode responseArray = requestNode.get("response");
            
            for (JsonNode response : responseArray) {
                Map<String, Object> exampleResponse = new HashMap<>();
                
                String responseName = response.path("name").asText("Example Response");
                int responseCode = response.path("code").asInt(200);
                
                exampleResponse.put("name", responseName);
                exampleResponse.put("code", responseCode);
                
                // Extract response body
                if (response.has("body")) {
                    String responseBody = response.get("body").asText("");
                    exampleResponse.put("body", responseBody);
                    
                    // Try to parse JSON response body
                    try {
                        if (responseBody.trim().startsWith("{") || responseBody.trim().startsWith("[")) {
                            JsonNode jsonBody = objectMapper.readTree(responseBody);
                            exampleResponse.put("jsonBody", jsonBody);
                        }
                    } catch (IOException e) {
                        // Not valid JSON, ignore
                    }
                }
                
                // Extract response headers
                Map<String, String> responseHeaders = new HashMap<>();
                if (response.has("header") && response.get("header").isArray()) {
                    JsonNode headerArray = response.get("header");
                    
                    for (JsonNode header : headerArray) {
                        String key = header.path("key").asText();
                        String value = header.path("value").asText("");
                        if (!key.isEmpty()) {
                            responseHeaders.put(key, value);
                        }
                    }
                }
                
                exampleResponse.put("headers", responseHeaders);
                exampleResponses.add(exampleResponse);
            }
        }
        
        // Create and return the ApiEndpoint
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setName(name);
        endpoint.setMethod(method);
        endpoint.setUrl(url);
        endpoint.setPath(path);
        endpoint.setHost(host);
        endpoint.setQueryParams(queryParams);
        endpoint.setHeaders(headers);
        endpoint.setRequestBody(requestBody);
        endpoint.setRequestBodyType(requestBodyType);
        endpoint.setFormData(formData);
        endpoint.setFolderPath(folderPath);
        endpoint.setCollectionName(collectionName);
        endpoint.setExampleResponses(exampleResponses);
        
        // Get any tests associated with the request
        if (requestNode.has("event") && requestNode.get("event").isArray()) {
            JsonNode eventArray = requestNode.get("event");
            
            for (JsonNode event : eventArray) {
                String listen = event.path("listen").asText("");
                if ("test".equals(listen) && event.has("script") && event.get("script").has("exec")) {
                    JsonNode execNode = event.get("script").get("exec");
                    StringBuilder testScript = new StringBuilder();
                    
                    if (execNode.isArray()) {
                        for (JsonNode line : execNode) {
                            testScript.append(line.asText()).append("\n");
                        }
                    } else if (execNode.isTextual()) {
                        testScript.append(execNode.asText());
                    }
                    
                    endpoint.setTestScript(testScript.toString());
                }
            }
        }
        
        // Try to extract the endpoint description
        if (request.has("description")) {
            JsonNode descNode = request.get("description");
            if (descNode.isTextual()) {
                endpoint.setDescription(descNode.asText());
            } else if (descNode.has("content") && descNode.get("content").isTextual()) {
                endpoint.setDescription(descNode.get("content").asText());
            }
        }
        
        return endpoint;
    }
    
    /**
     * Processes variables in a string, replacing variable references with their values.
     *
     * @param input String containing variable references
     * @param collectionVariables Collection-level variables
     * @param folderVariables Folder-level variables
     * @return String with variable references replaced with their values
     */
    private String processVariables(String input, Map<String, String> collectionVariables, Map<String, String> folderVariables) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        
        String result = input;
        
        // Replace folder variables first (they have higher precedence)
        for (Map.Entry<String, String> entry : folderVariables.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        
        // Replace collection variables
        for (Map.Entry<String, String> entry : collectionVariables.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        
        return result;
    }
}
