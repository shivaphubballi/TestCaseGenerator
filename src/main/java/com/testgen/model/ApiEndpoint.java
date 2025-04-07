package com.testgen.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents an API endpoint in a Postman collection.
 */
public class ApiEndpoint {
    private String name;
    private String url;
    private String method;
    private Map<String, String> headers;
    private String body;
    private List<String> queryParams;
    private List<String> pathParams;
    private Map<String, String> auth;
    
    /**
     * Creates a new API endpoint.
     */
    public ApiEndpoint() {
        this.headers = new HashMap<>();
        this.queryParams = new ArrayList<>();
        this.pathParams = new ArrayList<>();
        this.auth = new HashMap<>();
    }
    
    /**
     * Gets the name of the endpoint.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the name of the endpoint.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Gets the URL of the endpoint.
     */
    public String getUrl() {
        return url;
    }
    
    /**
     * Sets the URL of the endpoint.
     */
    public void setUrl(String url) {
        this.url = url;
    }
    
    /**
     * Gets the HTTP method of the endpoint.
     */
    public String getMethod() {
        return method;
    }
    
    /**
     * Sets the HTTP method of the endpoint.
     */
    public void setMethod(String method) {
        this.method = method;
    }
    
    /**
     * Gets the headers of the endpoint.
     */
    public Map<String, String> getHeaders() {
        return headers;
    }
    
    /**
     * Sets the headers of the endpoint.
     */
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
    
    /**
     * Adds a header to the endpoint.
     */
    public void addHeader(String name, String value) {
        this.headers.put(name, value);
    }
    
    /**
     * Gets the request body of the endpoint.
     */
    public String getBody() {
        return body;
    }
    
    /**
     * Sets the request body of the endpoint.
     */
    public void setBody(String body) {
        this.body = body;
    }
    
    /**
     * Gets the query parameters of the endpoint.
     */
    public List<String> getQueryParams() {
        return queryParams;
    }
    
    /**
     * Sets the query parameters of the endpoint.
     */
    public void setQueryParams(List<String> queryParams) {
        this.queryParams = queryParams;
    }
    
    /**
     * Adds a query parameter to the endpoint.
     */
    public void addQueryParam(String param) {
        this.queryParams.add(param);
    }
    
    /**
     * Gets the path parameters of the endpoint.
     */
    public List<String> getPathParams() {
        return pathParams;
    }
    
    /**
     * Sets the path parameters of the endpoint.
     */
    public void setPathParams(List<String> pathParams) {
        this.pathParams = pathParams;
    }
    
    /**
     * Adds a path parameter to the endpoint.
     */
    public void addPathParam(String param) {
        this.pathParams.add(param);
    }
    
    /**
     * Gets the authentication details of the endpoint.
     */
    public Map<String, String> getAuth() {
        return auth;
    }
    
    /**
     * Sets the authentication details of the endpoint.
     */
    public void setAuth(Map<String, String> auth) {
        this.auth = auth;
    }
    
    /**
     * Adds an authentication detail to the endpoint.
     */
    public void addAuth(String name, String value) {
        this.auth.put(name, value);
    }
}
