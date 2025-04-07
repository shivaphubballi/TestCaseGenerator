package com.testgen.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents an API endpoint found in a Postman collection.
 */
public class ApiEndpoint {
    private String name;
    private String method;
    private String url;
    private String host;
    private String path;
    private Map<String, String> queryParams;
    private Map<String, String> headers;
    private String requestBody;
    private String requestBodyType;  // "json", "xml", "form-data", etc.
    private Map<String, String> formData;
    private String folderPath;
    private String collectionName;
    private List<Map<String, Object>> exampleResponses;
    private String testScript;
    private String description;
    
    public ApiEndpoint() {
        this.queryParams = new HashMap<>();
        this.headers = new HashMap<>();
        this.formData = new HashMap<>();
        this.exampleResponses = new ArrayList<>();
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getMethod() {
        return method;
    }
    
    public void setMethod(String method) {
        this.method = method;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getHost() {
        return host;
    }
    
    public void setHost(String host) {
        this.host = host;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public Map<String, String> getQueryParams() {
        return queryParams;
    }
    
    public void setQueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }
    
    public Map<String, String> getHeaders() {
        return headers;
    }
    
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
    
    public String getRequestBody() {
        return requestBody;
    }
    
    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }
    
    public String getRequestBodyType() {
        return requestBodyType;
    }
    
    public void setRequestBodyType(String requestBodyType) {
        this.requestBodyType = requestBodyType;
    }
    
    public Map<String, String> getFormData() {
        return formData;
    }
    
    public void setFormData(Map<String, String> formData) {
        this.formData = formData;
    }
    
    public String getFolderPath() {
        return folderPath;
    }
    
    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }
    
    public String getCollectionName() {
        return collectionName;
    }
    
    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }
    
    public List<Map<String, Object>> getExampleResponses() {
        return exampleResponses;
    }
    
    public void setExampleResponses(List<Map<String, Object>> exampleResponses) {
        this.exampleResponses = exampleResponses;
    }
    
    public String getTestScript() {
        return testScript;
    }
    
    public void setTestScript(String testScript) {
        this.testScript = testScript;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Gets a combined path including folder structure and endpoint name.
     *
     * @return Combined path
     */
    public String getFullPath() {
        return (folderPath.isEmpty() ? "" : folderPath + "/") + name;
    }
    
    /**
     * Gets a shortened name suitable for use as a method name.
     *
     * @return Shortened name
     */
    public String getShortName() {
        // Remove special characters and replace with underscores
        String shortName = name.replaceAll("[^a-zA-Z0-9]", "_").replaceAll("_+", "_");
        
        // If the name is empty after replacement, use the HTTP method and path
        if (shortName.trim().isEmpty()) {
            shortName = method.toLowerCase() + "_endpoint";
            
            if (path != null && !path.isEmpty()) {
                // Extract the last part of the path
                String[] pathParts = path.split("/");
                if (pathParts.length > 0 && !pathParts[pathParts.length - 1].isEmpty()) {
                    shortName += "_" + pathParts[pathParts.length - 1].replaceAll("[^a-zA-Z0-9]", "_");
                }
            }
        }
        
        // Ensure it starts with a letter
        if (!shortName.matches("^[a-zA-Z].*")) {
            shortName = "endpoint_" + shortName;
        }
        
        return camelCase(shortName);
    }
    
    /**
     * Converts a string with underscores to camelCase.
     *
     * @param input String with underscores
     * @return String in camelCase
     */
    private String camelCase(String input) {
        StringBuilder sb = new StringBuilder();
        boolean capitalizeNext = false;
        
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            
            if (c == '_') {
                capitalizeNext = true;
            } else {
                if (capitalizeNext) {
                    sb.append(Character.toUpperCase(c));
                    capitalizeNext = false;
                } else {
                    if (i == 0) {
                        sb.append(Character.toLowerCase(c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Gets an expected status code based on the HTTP method or example responses.
     *
     * @return Expected status code
     */
    public int getExpectedStatusCode() {
        // First check if we have example responses
        if (!exampleResponses.isEmpty()) {
            for (Map<String, Object> response : exampleResponses) {
                if (response.containsKey("code")) {
                    Object code = response.get("code");
                    if (code instanceof Integer) {
                        return (Integer) code;
                    } else if (code instanceof String) {
                        try {
                            return Integer.parseInt((String) code);
                        } catch (NumberFormatException e) {
                            // Ignore and continue
                        }
                    }
                }
            }
        }
        
        // Fallback to HTTP method-based expectation
        switch (method.toUpperCase()) {
            case "GET":
                return 200;
            case "POST":
                return 201;
            case "PUT":
            case "PATCH":
                return 200;
            case "DELETE":
                return 204;
            default:
                return 200;
        }
    }
    
    /**
     * Gets a summary of this endpoint suitable for Jira test cases.
     *
     * @return A summary string
     */
    public String getSummary() {
        return method.toUpperCase() + " " + (path.isEmpty() ? url : path);
    }
    
    @Override
    public String toString() {
        return "ApiEndpoint{" +
                "name='" + name + '\'' +
                ", method='" + method + '\'' +
                ", url='" + url + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
