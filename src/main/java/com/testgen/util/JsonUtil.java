package com.testgen.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testgen.exception.TestGenException;

import java.io.File;
import java.io.IOException;

/**
 * Utility class for JSON operations.
 */
public class JsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Parses a JSON file.
     *
     * @param file The JSON file
     * @return The JSON node
     * @throws IOException If an error occurs during parsing
     */
    public static JsonNode parseJsonFile(File file) throws IOException {
        try {
            return objectMapper.readTree(file);
        } catch (IOException e) {
            throw new TestGenException("Error parsing JSON file: " + e.getMessage(), e);
        }
    }
    
    /**
     * Parses a JSON string.
     *
     * @param json The JSON string
     * @return The JSON node
     * @throws IOException If an error occurs during parsing
     */
    public static JsonNode parseJsonString(String json) throws IOException {
        try {
            return objectMapper.readTree(json);
        } catch (IOException e) {
            throw new TestGenException("Error parsing JSON string: " + e.getMessage(), e);
        }
    }
    
    /**
     * Converts an object to a JSON string.
     *
     * @param object The object
     * @return The JSON string
     * @throws IOException If an error occurs during conversion
     */
    public static String toJsonString(Object object) throws IOException {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (IOException e) {
            throw new TestGenException("Error converting object to JSON: " + e.getMessage(), e);
        }
    }
}
