package com.testgen.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an AJAX call made by a Single Page Application (SPA).
 * This includes XHR requests, fetch API calls, and other asynchronous
 * network operations.
 */
public class AjaxCall {
    private String url;
    private String method;
    private Map<String, String> headers;
    private String requestBody;
    private String trigger;
    private int successStatusCode;
    private String responseType;
    
    public AjaxCall() {
        this.url = "";
        this.method = "GET";
        this.headers = new HashMap<>();
        this.requestBody = "";
        this.trigger = "";
        this.successStatusCode = 200;
        this.responseType = "application/json";
    }
    
    /**
     * Gets the URL that the AJAX call is made to.
     */
    public String getUrl() {
        return url;
    }
    
    /**
     * Sets the URL that the AJAX call is made to.
     */
    public void setUrl(String url) {
        this.url = url;
    }
    
    /**
     * Gets the HTTP method used for the AJAX call (GET, POST, etc.).
     */
    public String getMethod() {
        return method;
    }
    
    /**
     * Sets the HTTP method used for the AJAX call.
     */
    public void setMethod(String method) {
        this.method = method;
    }
    
    /**
     * Gets the HTTP headers sent with the AJAX call.
     */
    public Map<String, String> getHeaders() {
        return headers;
    }
    
    /**
     * Sets the HTTP headers sent with the AJAX call.
     */
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
    
    /**
     * Gets the request body sent with the AJAX call (for POST, PUT, etc.).
     */
    public String getRequestBody() {
        return requestBody;
    }
    
    /**
     * Sets the request body sent with the AJAX call.
     */
    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }
    
    /**
     * Gets the UI event or action that triggers this AJAX call.
     */
    public String getTrigger() {
        return trigger;
    }
    
    /**
     * Sets the UI event or action that triggers this AJAX call.
     */
    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }
    
    /**
     * Gets the HTTP status code that indicates a successful response.
     */
    public int getSuccessStatusCode() {
        return successStatusCode;
    }
    
    /**
     * Sets the HTTP status code that indicates a successful response.
     */
    public void setSuccessStatusCode(int successStatusCode) {
        this.successStatusCode = successStatusCode;
    }
    
    /**
     * Gets the expected content type of the response.
     */
    public String getResponseType() {
        return responseType;
    }
    
    /**
     * Sets the expected content type of the response.
     */
    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }
    
    /**
     * Adds a header to the AJAX call.
     */
    public void addHeader(String name, String value) {
        this.headers.put(name, value);
    }
}
