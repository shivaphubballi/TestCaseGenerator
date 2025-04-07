package com.testgen.util;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Utility methods for web-related operations.
 */
public class WebUtil {
    private static final Logger LOGGER = Logger.getLogger(WebUtil.class.getName());
    
    // Set of common attributes to use for identification
    private static final Set<String> IDENTIFYING_ATTRIBUTES = new HashSet<>(
            Arrays.asList("id", "name", "class", "href", "data-testid", "data-cy", "data-automation-id")
    );
    
    /**
     * Extracts the domain from a URL.
     *
     * @param url The URL
     * @return The domain
     */
    public static String extractDomain(String url) {
        if (url == null || url.isEmpty()) {
            return "";
        }
        
        try {
            URL parsedUrl = new URL(url);
            return parsedUrl.getHost();
        } catch (MalformedURLException e) {
            LOGGER.log(Level.WARNING, "Failed to extract domain from URL: " + url, e);
            return "";
        }
    }
    
    /**
     * Extracts the base URL (protocol + domain) from a URL.
     *
     * @param url The URL
     * @return The base URL
     */
    public static String extractBaseUrl(String url) {
        if (url == null || url.isEmpty()) {
            return "";
        }
        
        try {
            URL parsedUrl = new URL(url);
            return parsedUrl.getProtocol() + "://" + parsedUrl.getHost();
        } catch (MalformedURLException e) {
            LOGGER.log(Level.WARNING, "Failed to extract base URL from URL: " + url, e);
            return "";
        }
    }
    
    /**
     * Resolves a relative URL against a base URL.
     *
     * @param baseUrl The base URL
     * @param relativeUrl The relative URL
     * @return The absolute URL
     */
    public static String resolveUrl(String baseUrl, String relativeUrl) {
        if (baseUrl == null || baseUrl.isEmpty() || relativeUrl == null || relativeUrl.isEmpty()) {
            return relativeUrl;
        }
        
        // If the relative URL is already absolute, return it
        if (relativeUrl.startsWith("http://") || relativeUrl.startsWith("https://")) {
            return relativeUrl;
        }
        
        try {
            URL base = new URL(baseUrl);
            URL resolved = new URL(base, relativeUrl);
            return resolved.toString();
        } catch (MalformedURLException e) {
            LOGGER.log(Level.WARNING, "Failed to resolve URL: " + relativeUrl + " against base: " + baseUrl, e);
            return relativeUrl;
        }
    }
    
    /**
     * Generates a CSS selector for an element.
     *
     * @param element The Jsoup element
     * @return A CSS selector
     */
    public static String generateCssSelector(Element element) {
        if (element == null) {
            return "";
        }
        
        // Try ID selector (most specific)
        if (!element.id().isEmpty()) {
            return "#" + escapeCssSelector(element.id());
        }
        
        // Try attribute selectors for other identifying attributes
        for (String attr : IDENTIFYING_ATTRIBUTES) {
            if (attr.equals("id")) {
                continue; // Already checked
            }
            
            String attrValue = element.attr(attr);
            if (!attrValue.isEmpty()) {
                // For class attributes, create a compound selector with tag and class
                if (attr.equals("class")) {
                    String className = attrValue.split("\\s+")[0]; // Use first class only
                    return element.tagName() + "." + escapeCssSelector(className);
                } else {
                    return element.tagName() + "[" + attr + "=\"" + escapeCssSelector(attrValue) + "\"]";
                }
            }
        }
        
        // As a last resort, generate a path-based selector
        return generatePathSelector(element);
    }
    
    /**
     * Generates a path-based CSS selector for an element.
     *
     * @param element The Jsoup element
     * @return A CSS selector
     */
    private static String generatePathSelector(Element element) {
        if (element.parent() == null) {
            return element.tagName();
        }
        
        // Get index of element among siblings of same type
        Elements siblings = element.parent().select("> " + element.tagName());
        int index = 0;
        for (Element sibling : siblings) {
            if (sibling.equals(element)) {
                break;
            }
            index++;
        }
        
        // If this is the only element of its type, or it's the first one, omit the index
        String indexSuffix = (siblings.size() <= 1 || index == 0) ? "" : ":nth-of-type(" + (index + 1) + ")";
        
        // Limit the path length to avoid overly complex selectors
        Element parent = element.parent();
        int depth = 0;
        while (parent != null && parent.tagName() != "html" && depth < 3) {
            String parentSelector = generateSimpleSelector(parent);
            if (!parentSelector.isEmpty()) {
                return parentSelector + " > " + element.tagName() + indexSuffix;
            }
            parent = parent.parent();
            depth++;
        }
        
        // If we couldn't find a good parent selector, return a simple tag with index
        return element.tagName() + indexSuffix;
    }
    
    /**
     * Generates a simple selector for an element (without checking path).
     *
     * @param element The Jsoup element
     * @return A CSS selector
     */
    private static String generateSimpleSelector(Element element) {
        if (element == null) {
            return "";
        }
        
        // Try ID selector
        if (!element.id().isEmpty()) {
            return "#" + escapeCssSelector(element.id());
        }
        
        // Try class selector (use the first class only)
        if (!element.className().isEmpty()) {
            String firstClass = element.className().split("\\s+")[0];
            return element.tagName() + "." + escapeCssSelector(firstClass);
        }
        
        // Try other identifying attributes
        for (String attr : IDENTIFYING_ATTRIBUTES) {
            if (attr.equals("id") || attr.equals("class")) {
                continue; // Already checked
            }
            
            String attrValue = element.attr(attr);
            if (!attrValue.isEmpty()) {
                return element.tagName() + "[" + attr + "=\"" + escapeCssSelector(attrValue) + "\"]";
            }
        }
        
        // Just return the tag name if nothing else
        return element.tagName();
    }
    
    /**
     * Escapes special characters in CSS selectors.
     *
     * @param value The value to escape
     * @return The escaped value
     */
    private static String escapeCssSelector(String value) {
        // Escape special characters in CSS selectors
        // Based on https://mathiasbynens.be/notes/css-escapes
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            
            // Escape : . [ ] ( ) # + > ~ " ! $ % & ' * , / ; = ? @ ^ ` { | } ~
            if (":[]()+>#~\"!$%&'*,/;=?@^`{|}~".indexOf(c) != -1) {
                result.append("\\").append(c);
            } else if (c == '\\') {
                result.append("\\\\");
            } else {
                result.append(c);
            }
        }
        
        return result.toString();
    }
    
    /**
     * Generates a unique name for a test file based on the URL.
     *
     * @param url The URL
     * @return A filename
     */
    public static String generateTestFileName(String url) {
        if (url == null || url.isEmpty()) {
            return "UnknownPage";
        }
        
        try {
            URL parsedUrl = new URL(url);
            
            // Extract domain
            String domain = parsedUrl.getHost();
            domain = domain.replace("www.", "");
            
            // Extract path
            String path = parsedUrl.getPath();
            path = path.replaceAll("/+$", ""); // Remove trailing slashes
            
            if (path.isEmpty() || path.equals("/")) {
                return toJavaClassName(domain) + "HomePage";
            }
            
            // Get the last part of the path
            String[] pathParts = path.split("/");
            String lastPart = pathParts[pathParts.length - 1];
            
            if (lastPart.isEmpty() && pathParts.length > 1) {
                lastPart = pathParts[pathParts.length - 2];
            }
            
            // Remove extensions and query parameters
            if (lastPart.contains(".")) {
                lastPart = lastPart.substring(0, lastPart.lastIndexOf('.'));
            }
            
            return toJavaClassName(domain) + toJavaClassName(lastPart) + "Page";
            
        } catch (MalformedURLException e) {
            LOGGER.log(Level.WARNING, "Failed to parse URL: " + url, e);
            
            // Fallback: sanitize the URL to create a valid class name
            String sanitized = url.replaceAll("[^a-zA-Z0-9]", "_");
            sanitized = sanitized.replaceAll("_+", "_");
            sanitized = sanitized.replaceAll("^_|_$", "");
            
            if (sanitized.isEmpty()) {
                return "UnknownPage";
            }
            
            // Ensure it starts with an uppercase letter
            return sanitized.substring(0, 1).toUpperCase() + sanitized.substring(1) + "Page";
        }
    }
    
    /**
     * Converts a string to a valid Java class name.
     *
     * @param input The input string
     * @return A valid Java class name
     */
    public static String toJavaClassName(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        
        // Replace non-alphanumeric characters with underscores
        String sanitized = input.replaceAll("[^a-zA-Z0-9]", "_");
        
        // Split into words by underscore
        String[] parts = sanitized.split("_");
        
        // Convert to PascalCase
        StringBuilder result = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                result.append(part.substring(0, 1).toUpperCase());
                if (part.length() > 1) {
                    result.append(part.substring(1).toLowerCase());
                }
            }
        }
        
        String className = result.toString();
        
        // If class name starts with a digit, prefix with 'Page'
        if (!className.isEmpty() && Character.isDigit(className.charAt(0))) {
            className = "Page" + className;
        }
        
        return className;
    }
    
    /**
     * Checks if a URL is valid.
     *
     * @param url The URL to check
     * @return True if the URL is valid, false otherwise
     */
    public static boolean isValidUrl(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }
        
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
