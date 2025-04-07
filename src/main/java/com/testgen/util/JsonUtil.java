package com.testgen.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility methods for JSON processing.
 */
public class JsonUtil {
    private static final Logger LOGGER = Logger.getLogger(JsonUtil.class.getName());
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    
    /**
     * Checks if a string is valid JSON.
     *
     * @param json The JSON string to check
     * @return True if the string is valid JSON, false otherwise
     */
    public static boolean isValidJson(String json) {
        if (json == null || json.isEmpty()) {
            return false;
        }
        
        try {
            OBJECT_MAPPER.readTree(json);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    /**
     * Converts a JSON string to a pretty-printed JSON string.
     *
     * @param json The JSON string to pretty-print
     * @return A pretty-printed JSON string, or the original string if it's not valid JSON
     */
    public static String prettyPrintJson(String json) {
        if (json == null || json.isEmpty()) {
            return json;
        }
        
        try {
            JsonNode jsonNode = OBJECT_MAPPER.readTree(json);
            return OBJECT_MAPPER.writeValueAsString(jsonNode);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to pretty-print JSON: " + e.getMessage());
            return json;
        }
    }
    
    /**
     * Creates a JSON object from a map.
     *
     * @param map The map to convert
     * @return A JSON string
     */
    public static String mapToJson(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return "{}";
        }
        
        try {
            return OBJECT_MAPPER.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            LOGGER.log(Level.WARNING, "Failed to convert map to JSON: " + e.getMessage());
            return "{}";
        }
    }
    
    /**
     * Extracts field names from a JSON object.
     *
     * @param json The JSON string
     * @return A comma-separated list of field names
     */
    public static String extractFieldNames(String json) {
        if (json == null || json.isEmpty()) {
            return "";
        }
        
        try {
            JsonNode jsonNode = OBJECT_MAPPER.readTree(json);
            if (!jsonNode.isObject()) {
                return "";
            }
            
            StringBuilder fieldNames = new StringBuilder();
            Iterator<String> fieldNameIterator = jsonNode.fieldNames();
            
            while (fieldNameIterator.hasNext()) {
                if (fieldNames.length() > 0) {
                    fieldNames.append(", ");
                }
                fieldNames.append(fieldNameIterator.next());
            }
            
            return fieldNames.toString();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to extract field names from JSON: " + e.getMessage());
            return "";
        }
    }
    
    /**
     * Merges two JSON objects.
     *
     * @param json1 The first JSON object
     * @param json2 The second JSON object
     * @return The merged JSON object
     */
    public static String mergeJsonObjects(String json1, String json2) {
        if (json1 == null || json1.isEmpty()) {
            return json2 != null ? json2 : "{}";
        }
        
        if (json2 == null || json2.isEmpty()) {
            return json1;
        }
        
        try {
            JsonNode node1 = OBJECT_MAPPER.readTree(json1);
            JsonNode node2 = OBJECT_MAPPER.readTree(json2);
            
            if (!node1.isObject() || !node2.isObject()) {
                throw new IllegalArgumentException("Both JSON strings must be objects");
            }
            
            ObjectNode merged = OBJECT_MAPPER.createObjectNode();
            merged.setAll((ObjectNode) node1);
            merged.setAll((ObjectNode) node2);
            
            return OBJECT_MAPPER.writeValueAsString(merged);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to merge JSON objects: " + e.getMessage());
            return json1;
        }
    }
    
    /**
     * Creates a JSON object with the specified field and value.
     *
     * @param field The field name
     * @param value The field value
     * @return A JSON string
     */
    public static String createJsonObject(String field, Object value) {
        if (field == null || field.isEmpty()) {
            return "{}";
        }
        
        try {
            ObjectNode node = OBJECT_MAPPER.createObjectNode();
            
            if (value == null) {
                node.putNull(field);
            } else if (value instanceof String) {
                node.put(field, (String) value);
            } else if (value instanceof Integer) {
                node.put(field, (Integer) value);
            } else if (value instanceof Long) {
                node.put(field, (Long) value);
            } else if (value instanceof Double) {
                node.put(field, (Double) value);
            } else if (value instanceof Boolean) {
                node.put(field, (Boolean) value);
            } else {
                node.put(field, value.toString());
            }
            
            return OBJECT_MAPPER.writeValueAsString(node);
        } catch (JsonProcessingException e) {
            LOGGER.log(Level.WARNING, "Failed to create JSON object: " + e.getMessage());
            return "{}";
        }
    }
    
    /**
     * Generates a sample JSON object based on the provided schema.
     *
     * @param schemaJson JSON schema
     * @return A sample JSON object
     */
    public static String generateSampleJson(String schemaJson) {
        if (schemaJson == null || schemaJson.isEmpty()) {
            return "{}";
        }
        
        try {
            JsonNode schemaNode = OBJECT_MAPPER.readTree(schemaJson);
            return generateSampleFromSchema(schemaNode).toString();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to generate sample JSON: " + e.getMessage());
            return "{}";
        }
    }
    
    /**
     * Recursively generates a sample value based on a JSON schema node.
     *
     * @param schemaNode The schema node
     * @return A sample value
     */
    private static JsonNode generateSampleFromSchema(JsonNode schemaNode) {
        String type = schemaNode.path("type").asText("object");
        
        switch (type) {
            case "object":
                return generateObjectFromSchema(schemaNode);
            case "array":
                return generateArrayFromSchema(schemaNode);
            case "string":
                return OBJECT_MAPPER.valueToTree(generateStringFromSchema(schemaNode));
            case "integer":
            case "number":
                return OBJECT_MAPPER.valueToTree(123);
            case "boolean":
                return OBJECT_MAPPER.valueToTree(true);
            case "null":
                return OBJECT_MAPPER.nullNode();
            default:
                return OBJECT_MAPPER.createObjectNode();
        }
    }
    
    /**
     * Generates a sample object based on a JSON schema.
     *
     * @param schemaNode The schema node
     * @return A sample object
     */
    private static ObjectNode generateObjectFromSchema(JsonNode schemaNode) {
        ObjectNode result = OBJECT_MAPPER.createObjectNode();
        
        // Process properties
        JsonNode properties = schemaNode.path("properties");
        if (properties.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = properties.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String propertyName = field.getKey();
                JsonNode propertySchema = field.getValue();
                result.set(propertyName, generateSampleFromSchema(propertySchema));
            }
        }
        
        return result;
    }
    
    /**
     * Generates a sample array based on a JSON schema.
     *
     * @param schemaNode The schema node
     * @return A sample array
     */
    private static ArrayNode generateArrayFromSchema(JsonNode schemaNode) {
        ArrayNode result = OBJECT_MAPPER.createArrayNode();
        
        // Process items
        JsonNode items = schemaNode.path("items");
        if (!items.isMissingNode()) {
            // Add a single sample item to the array
            result.add(generateSampleFromSchema(items));
        }
        
        return result;
    }
    
    /**
     * Generates a sample string based on a JSON schema.
     *
     * @param schemaNode The schema node
     * @return A sample string
     */
    private static String generateStringFromSchema(JsonNode schemaNode) {
        JsonNode formatNode = schemaNode.path("format");
        if (!formatNode.isMissingNode()) {
            String format = formatNode.asText();
            
            switch (format) {
                case "date":
                    return "2023-01-01";
                case "date-time":
                    return "2023-01-01T12:00:00Z";
                case "email":
                    return "user@example.com";
                case "hostname":
                    return "example.com";
                case "ipv4":
                    return "192.168.1.1";
                case "ipv6":
                    return "2001:0db8:85a3:0000:0000:8a2e:0370:7334";
                case "uri":
                    return "https://example.com";
                case "uuid":
                    return "550e8400-e29b-41d4-a716-446655440000";
                default:
                    return "string";
            }
        }
        
        // Use enum value if available
        JsonNode enumNode = schemaNode.path("enum");
        if (enumNode.isArray() && enumNode.size() > 0) {
            return enumNode.get(0).asText("string");
        }
        
        // Use default value if available
        JsonNode defaultNode = schemaNode.path("default");
        if (!defaultNode.isMissingNode() && defaultNode.isTextual()) {
            return defaultNode.asText();
        }
        
        // Use example value if available
        JsonNode exampleNode = schemaNode.path("example");
        if (!exampleNode.isMissingNode() && exampleNode.isTextual()) {
            return exampleNode.asText();
        }
        
        return "string";
    }
}
