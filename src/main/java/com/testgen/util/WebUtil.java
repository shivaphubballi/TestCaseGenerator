package com.testgen.util;

import com.testgen.exception.TestGenException;
import com.testgen.model.WebElement;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for web operations.
 */
public class WebUtil {
    /**
     * Fetches a web page.
     *
     * @param url The URL of the web page
     * @return The Document object representing the web page
     * @throws IOException If an error occurs during fetching
     */
    public static Document fetchWebPage(String url) throws IOException {
        try {
            return Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .get();
        } catch (IOException e) {
            throw new TestGenException("Error fetching web page: " + e.getMessage(), e);
        }
    }
    
    /**
     * Extracts web elements from a Document.
     *
     * @param document The Document to extract elements from
     * @return A list of web elements
     */
    public static List<WebElement> extractElements(Document document) {
        List<WebElement> elements = new ArrayList<>();
        
        // Extract forms
        Elements forms = document.select("form");
        for (Element form : forms) {
            WebElement formElement = convertElement(form);
            elements.add(formElement);
            
            // Extract form fields
            Elements formFields = form.select("input, select, textarea, button");
            for (Element field : formFields) {
                WebElement fieldElement = convertElement(field);
                elements.add(fieldElement);
            }
        }
        
        // Extract links
        Elements links = document.select("a");
        for (Element link : links) {
            WebElement linkElement = convertElement(link);
            elements.add(linkElement);
        }
        
        // Extract buttons
        Elements buttons = document.select("button, input[type=button], input[type=submit]");
        for (Element button : buttons) {
            WebElement buttonElement = convertElement(button);
            elements.add(buttonElement);
        }
        
        return elements;
    }
    
    /**
     * Converts a JSoup Element to a WebElement.
     *
     * @param element The JSoup Element
     * @return The WebElement
     */
    private static WebElement convertElement(Element element) {
        WebElement webElement = new WebElement(element.tagName());
        
        // Set ID
        if (element.hasAttr("id")) {
            webElement.setId(element.attr("id"));
        }
        
        // Set name
        if (element.hasAttr("name")) {
            webElement.setName(element.attr("name"));
        }
        
        // Set type for input elements
        if (element.tagName().equalsIgnoreCase("input") && element.hasAttr("type")) {
            webElement.setType(element.attr("type"));
        }
        
        // Set text
        String text = element.text();
        if (text != null && !text.isEmpty()) {
            webElement.setText(text);
        }
        
        // Set value
        if (element.hasAttr("value")) {
            webElement.setValue(element.attr("value"));
        }
        
        // Set CSS selector
        webElement.setCssSelector(generateCssSelector(element));
        
        // Set XPath
        webElement.setXpath(generateXPath(element));
        
        return webElement;
    }
    
    /**
     * Generates a CSS selector for an element.
     *
     * @param element The element
     * @return The CSS selector
     */
    private static String generateCssSelector(Element element) {
        StringBuilder selector = new StringBuilder();
        
        // If the element has an ID, use that
        if (element.hasAttr("id")) {
            return "#" + element.attr("id");
        }
        
        // Otherwise, use the tag name and other attributes
        selector.append(element.tagName());
        
        if (element.hasAttr("class")) {
            String[] classes = element.attr("class").split("\\s+");
            for (String className : classes) {
                if (!className.isEmpty()) {
                    selector.append(".").append(className);
                }
            }
        }
        
        if (element.hasAttr("name")) {
            selector.append("[name='").append(element.attr("name")).append("']");
        }
        
        return selector.toString();
    }
    
    /**
     * Generates an XPath expression for an element.
     *
     * @param element The element
     * @return The XPath expression
     */
    private static String generateXPath(Element element) {
        // If the element has an ID, use that
        if (element.hasAttr("id")) {
            return "//" + element.tagName() + "[@id='" + element.attr("id") + "']";
        }
        
        // If the element has a name, use that
        if (element.hasAttr("name")) {
            return "//" + element.tagName() + "[@name='" + element.attr("name") + "']";
        }
        
        // Otherwise, use a more complex XPath
        StringBuilder xpath = new StringBuilder("//" + element.tagName());
        
        if (element.hasAttr("class")) {
            xpath.append("[@class='").append(element.attr("class")).append("']");
        }
        
        if (element.hasText() && !element.text().isEmpty()) {
            xpath.append("[text()='").append(element.text()).append("']");
        }
        
        return xpath.toString();
    }
}
