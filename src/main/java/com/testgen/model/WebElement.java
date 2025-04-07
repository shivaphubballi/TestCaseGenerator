package com.testgen.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents an interactive element found in a web page.
 */
public class WebElement {
    private String type;              // e.g., "input-text", "button", "link", "form", etc.
    private String locator;           // e.g., "css", "xpath", "id"
    private String locatorValue;      // The actual selector value
    private String id;                // Element ID
    private String name;              // Element name attribute
    private String text;              // Element text content
    private String placeholder;       // Placeholder text (for inputs)
    private String href;              // For links
    private String parentForm;        // Reference to parent form if applicable
    private boolean required;         // Whether the element is required
    private boolean checked;          // For checkboxes and radio buttons
    private List<String> options;     // For select elements
    private Map<String, String> attributes; // Other element attributes
    private String action;            // For forms
    private String method;            // For forms
    
    public WebElement() {
        this.options = new ArrayList<>();
        this.attributes = new HashMap<>();
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getLocator() {
        return locator;
    }
    
    public void setLocator(String locator) {
        this.locator = locator;
    }
    
    public String getLocatorValue() {
        return locatorValue;
    }
    
    public void setLocatorValue(String locatorValue) {
        this.locatorValue = locatorValue;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public String getPlaceholder() {
        return placeholder;
    }
    
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }
    
    public String getHref() {
        return href;
    }
    
    public void setHref(String href) {
        this.href = href;
    }
    
    public String getParentForm() {
        return parentForm;
    }
    
    public void setParentForm(String parentForm) {
        this.parentForm = parentForm;
    }
    
    public boolean isRequired() {
        return required;
    }
    
    public void setRequired(boolean required) {
        this.required = required;
    }
    
    public boolean isChecked() {
        return checked;
    }
    
    public void setChecked(boolean checked) {
        this.checked = checked;
    }
    
    public List<String> getOptions() {
        return options;
    }
    
    public void setOptions(List<String> options) {
        this.options = options;
    }
    
    public Map<String, String> getAttributes() {
        return attributes;
    }
    
    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }
    
    public void setAttribute(String key, String value) {
        if (this.attributes == null) {
            this.attributes = new HashMap<>();
        }
        this.attributes.put(key, value);
    }
    
    public String getAttribute(String key) {
        return this.attributes != null ? this.attributes.get(key) : null;
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public String getMethod() {
        return method;
    }
    
    public void setMethod(String method) {
        this.method = method;
    }
    
    /**
     * Gets a descriptive name for the element, using the first non-empty value from:
     * id, name, placeholder, text, locator value
     *
     * @return A descriptive name for the element
     */
    public String getDescriptiveName() {
        if (id != null && !id.isEmpty()) {
            return id;
        }
        
        if (name != null && !name.isEmpty()) {
            return name;
        }
        
        if (placeholder != null && !placeholder.isEmpty()) {
            return placeholder;
        }
        
        if (text != null && !text.isEmpty()) {
            // If text is too long, truncate it
            if (text.length() > 20) {
                return text.substring(0, 17) + "...";
            }
            return text;
        }
        
        if (locatorValue != null && !locatorValue.isEmpty()) {
            // Extract the key part from CSS selector or XPath
            String[] parts = locatorValue.split("\\s+");
            return parts[parts.length - 1];
        }
        
        return "unnamed_element";
    }
    
    /**
     * Gets a valid Java identifier name from the descriptive name.
     *
     * @return A valid Java identifier
     */
    public String getJavaIdentifierName() {
        String name = getDescriptiveName()
                .replaceAll("[^a-zA-Z0-9_]", "_")
                .replaceAll("_+", "_");
        
        // If name starts with a digit, prepend with 'e'
        if (name.matches("^[0-9].*")) {
            name = "e" + name;
        }
        
        return camelCase(name);
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
     * Creates a suggestion for test data based on the element type and attributes.
     *
     * @return A suggested test value for the element
     */
    public String getSuggestedTestData() {
        switch (type) {
            case "input-text":
            case "textarea":
                if (name != null) {
                    if (name.toLowerCase().contains("email")) {
                        return "test@example.com";
                    } else if (name.toLowerCase().contains("name")) {
                        return "Test User";
                    } else if (name.toLowerCase().contains("phone")) {
                        return "1234567890";
                    } else if (name.toLowerCase().contains("address")) {
                        return "123 Test Street";
                    } else if (name.toLowerCase().contains("password")) {
                        return "TestPassword123!";
                    }
                }
                
                if (placeholder != null) {
                    if (placeholder.toLowerCase().contains("email")) {
                        return "test@example.com";
                    } else if (placeholder.toLowerCase().contains("name")) {
                        return "Test User";
                    } else if (placeholder.toLowerCase().contains("phone")) {
                        return "1234567890";
                    } else if (placeholder.toLowerCase().contains("address")) {
                        return "123 Test Street";
                    } else if (placeholder.toLowerCase().contains("password")) {
                        return "TestPassword123!";
                    }
                }
                
                return "Test Value";
                
            case "input-number":
                return "123";
                
            case "input-email":
                return "test@example.com";
                
            case "input-password":
                return "TestPassword123!";
                
            case "input-checkbox":
            case "input-radio":
                return "true";
                
            case "select":
                if (!options.isEmpty()) {
                    return options.get(0);
                }
                return "Option 1";
                
            case "input-file":
                return "test-file.txt";
                
            case "input-date":
                return "2023-01-01";
                
            case "input-time":
                return "12:00";
                
            case "input-color":
                return "#ff0000";
                
            case "input-range":
                String min = getAttribute("min");
                String max = getAttribute("max");
                if (min != null && max != null) {
                    try {
                        int minVal = Integer.parseInt(min);
                        int maxVal = Integer.parseInt(max);
                        return String.valueOf((minVal + maxVal) / 2);
                    } catch (NumberFormatException e) {
                        // Ignore and use default
                    }
                }
                return "50";
                
            default:
                return "";
        }
    }
    
    @Override
    public String toString() {
        return "WebElement{" +
                "type='" + type + '\'' +
                ", locator='" + locator + '\'' +
                ", locatorValue='" + locatorValue + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
