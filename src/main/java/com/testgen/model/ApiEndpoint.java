package com.testgen.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an API endpoint in the TestGen library.
 * An API endpoint contains information about a RESTful API endpoint.
 */
public class ApiEndpoint {
    private String url;
    private String method;
    private Map<String, String> headers;
    private String requestBody;
    private String responseBody;
    private int expectedStatusCode;
    private String name;
    private String description;

    /**
     * Creates a new API endpoint.
     */
    public ApiEndpoint() {
        this.headers = new HashMap<>();
    }

    /**
     * Creates a new API endpoint with the specified URL and HTTP method.
     *
     * @param url    The URL of the endpoint
     * @param method The HTTP method (GET, POST, PUT, DELETE, etc.)
     */
    public ApiEndpoint(String url, String method) {
        this.url = url;
        this.method = method;
        this.headers = new HashMap<>();
    }

    /**
     * Gets the URL of the endpoint.
     *
     * @return The URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the URL of the endpoint.
     *
     * @param url The URL
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Gets the HTTP method of the endpoint.
     *
     * @return The HTTP method
     */
    public String getMethod() {
        return method;
    }

    /**
     * Sets the HTTP method of the endpoint.
     *
     * @param method The HTTP method
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * Gets the headers of the endpoint.
     *
     * @return The headers
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * Sets the headers of the endpoint.
     *
     * @param headers The headers
     */
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    /**
     * Adds a header to the endpoint.
     *
     * @param name  The name of the header
     * @param value The value of the header
     */
    public void addHeader(String name, String value) {
        this.headers.put(name, value);
    }

    /**
     * Gets the request body of the endpoint.
     *
     * @return The request body
     */
    public String getRequestBody() {
        return requestBody;
    }

    /**
     * Sets the request body of the endpoint.
     *
     * @param requestBody The request body
     */
    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    /**
     * Gets the response body of the endpoint.
     *
     * @return The response body
     */
    public String getResponseBody() {
        return responseBody;
    }

    /**
     * Sets the response body of the endpoint.
     *
     * @param responseBody The response body
     */
    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    /**
     * Gets the expected status code of the endpoint.
     *
     * @return The expected status code
     */
    public int getExpectedStatusCode() {
        return expectedStatusCode;
    }

    /**
     * Sets the expected status code of the endpoint.
     *
     * @param expectedStatusCode The expected status code
     */
    public void setExpectedStatusCode(int expectedStatusCode) {
        this.expectedStatusCode = expectedStatusCode;
    }

    /**
     * Gets the name of the endpoint.
     *
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the endpoint.
     *
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the description of the endpoint.
     *
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the endpoint.
     *
     * @param description The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "ApiEndpoint{" +
                "url='" + url + '\'' +
                ", method='" + method + '\'' +
                ", headers=" + headers +
                ", requestBody='" + requestBody + '\'' +
                ", responseBody='" + responseBody + '\'' +
                ", expectedStatusCode=" + expectedStatusCode +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
