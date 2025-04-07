package com.testgen.core;

import com.testgen.exception.TestGenException;
import com.testgen.model.WebElement;
import com.testgen.util.WebUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Analyzes web applications by parsing HTML content and identifying interactive elements.
 */
public class WebAnalyzer {
    private static final Logger LOGGER = Logger.getLogger(WebAnalyzer.class.getName());
    private static final int TIMEOUT_MS = 10000;
    
    /**
     * Analyzes a web page and identifies interactive elements.
     *
     * @param url The URL of the web page to analyze
     * @return A list of WebElement objects representing interactive elements
     * @throws TestGenException If the page cannot be analyzed
     */
    public List<WebElement> analyzeWebPage(String url) throws TestGenException {
        LOGGER.info("Analyzing web page: " + url);
        
        if (url == null || url.trim().isEmpty()) {
            throw new TestGenException("URL cannot be null or empty");
        }
        
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .timeout(TIMEOUT_MS)
                    .get();
            
            return extractInteractiveElements(doc, url);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to analyze web page: " + url, e);
            throw new TestGenException("Failed to analyze web page: " + e.getMessage(), e);
        }
    }
    
    /**
     * Extracts interactive elements from the HTML document.
     *
     * @param doc The HTML document
     * @param baseUrl The base URL of the web page
     * @return A list of WebElement objects
     */
    private List<WebElement> extractInteractiveElements(Document doc, String baseUrl) {
        List<WebElement> elements = new ArrayList<>();
        
        // Extract form elements
        extractFormElements(doc, elements);
        
        // Extract links
        extractLinks(doc, elements, baseUrl);
        
        // Extract buttons not in forms
        extractButtons(doc, elements);
        
        // Extract select/dropdown elements
        extractDropdowns(doc, elements);
        
        // Extract other interactive elements (sliders, checkboxes, etc.)
        extractOtherInteractiveElements(doc, elements);
        
        return elements;
    }
    
    private void extractFormElements(Document doc, List<WebElement> elements) {
        Elements forms = doc.select("form");
        for (Element form : forms) {
            // Get form's action and method
            String action = form.attr("action").trim();
            String method = form.attr("method").trim();
            
            // Add the form itself
            WebElement formElement = new WebElement();
            formElement.setType("form");
            formElement.setLocator("css");
            formElement.setLocatorValue(WebUtil.generateCssSelector(form));
            formElement.setId(form.id());
            formElement.setName(form.attr("name"));
            formElement.setAction(action);
            formElement.setMethod(method.isEmpty() ? "GET" : method.toUpperCase());
            elements.add(formElement);
            
            // Extract input fields within the form
            Elements inputs = form.select("input:not([type=hidden]), textarea");
            for (Element input : inputs) {
                WebElement inputElement = new WebElement();
                String inputType = input.attr("type").toLowerCase();
                if (inputType.isEmpty()) {
                    inputType = "text"; // Default input type
                }
                
                inputElement.setType(input.tagName() + "-" + inputType);
                inputElement.setLocator("css");
                inputElement.setLocatorValue(WebUtil.generateCssSelector(input));
                inputElement.setId(input.id());
                inputElement.setName(input.attr("name"));
                inputElement.setPlaceholder(input.attr("placeholder"));
                inputElement.setParentForm(formElement.getLocatorValue());
                inputElement.setRequired(input.hasAttr("required"));
                
                // Add test data hints based on input attributes
                Map<String, String> attributes = new HashMap<>();
                for (org.jsoup.nodes.Attribute attr : input.attributes()) {
                    attributes.put(attr.getKey(), attr.getValue());
                }
                inputElement.setAttributes(attributes);
                
                elements.add(inputElement);
            }
            
            // Extract select dropdowns within the form
            Elements selects = form.select("select");
            for (Element select : selects) {
                WebElement selectElement = new WebElement();
                selectElement.setType("select");
                selectElement.setLocator("css");
                selectElement.setLocatorValue(WebUtil.generateCssSelector(select));
                selectElement.setId(select.id());
                selectElement.setName(select.attr("name"));
                selectElement.setParentForm(formElement.getLocatorValue());
                selectElement.setRequired(select.hasAttr("required"));
                
                // Extract options
                List<String> options = new ArrayList<>();
                Elements optElements = select.select("option");
                for (Element opt : optElements) {
                    options.add(opt.text());
                }
                selectElement.setOptions(options);
                
                elements.add(selectElement);
            }
            
            // Extract buttons within the form
            Elements buttons = form.select("button, input[type=submit], input[type=button]");
            for (Element button : buttons) {
                WebElement buttonElement = new WebElement();
                buttonElement.setType(button.tagName().equals("button") ? "button" : "input-" + button.attr("type"));
                buttonElement.setLocator("css");
                buttonElement.setLocatorValue(WebUtil.generateCssSelector(button));
                buttonElement.setId(button.id());
                buttonElement.setName(button.attr("name"));
                buttonElement.setText(button.text().isEmpty() ? button.attr("value") : button.text());
                buttonElement.setParentForm(formElement.getLocatorValue());
                
                elements.add(buttonElement);
            }
        }
    }
    
    private void extractLinks(Document doc, List<WebElement> elements, String baseUrl) {
        Elements links = doc.select("a[href]");
        for (Element link : links) {
            String href = link.attr("abs:href");
            
            // Skip empty links, javascript links, and anchor links
            if (href.isEmpty() || href.startsWith("javascript:") || href.startsWith("#")) {
                continue;
            }
            
            // Skip if it's an external link (optional - might want to test these too)
            if (!href.startsWith(baseUrl) && href.contains("://")) {
                continue;
            }
            
            WebElement linkElement = new WebElement();
            linkElement.setType("link");
            linkElement.setLocator("css");
            linkElement.setLocatorValue(WebUtil.generateCssSelector(link));
            linkElement.setId(link.id());
            linkElement.setName(link.attr("name"));
            linkElement.setText(link.text());
            linkElement.setHref(href);
            
            elements.add(linkElement);
        }
    }
    
    private void extractButtons(Document doc, List<WebElement> elements) {
        // Select buttons that are not already inside forms (those were handled in extractFormElements)
        Elements buttons = doc.select("body button:not(form button), body input[type=button]:not(form input[type=button]), body input[type=submit]:not(form input[type=submit])");
        for (Element button : buttons) {
            WebElement buttonElement = new WebElement();
            buttonElement.setType(button.tagName().equals("button") ? "button" : "input-" + button.attr("type"));
            buttonElement.setLocator("css");
            buttonElement.setLocatorValue(WebUtil.generateCssSelector(button));
            buttonElement.setId(button.id());
            buttonElement.setName(button.attr("name"));
            buttonElement.setText(button.text().isEmpty() ? button.attr("value") : button.text());
            
            elements.add(buttonElement);
        }
    }
    
    private void extractDropdowns(Document doc, List<WebElement> elements) {
        // Select select elements that are not already inside forms
        Elements selects = doc.select("body select:not(form select)");
        for (Element select : selects) {
            WebElement selectElement = new WebElement();
            selectElement.setType("select");
            selectElement.setLocator("css");
            selectElement.setLocatorValue(WebUtil.generateCssSelector(select));
            selectElement.setId(select.id());
            selectElement.setName(select.attr("name"));
            
            // Extract options
            List<String> options = new ArrayList<>();
            Elements optElements = select.select("option");
            for (Element opt : optElements) {
                options.add(opt.text());
            }
            selectElement.setOptions(options);
            
            elements.add(selectElement);
        }
    }
    
    private void extractOtherInteractiveElements(Document doc, List<WebElement> elements) {
        // Extract checkboxes and radio buttons not in forms
        Elements checkboxesAndRadios = doc.select("body input[type=checkbox]:not(form input[type=checkbox]), body input[type=radio]:not(form input[type=radio])");
        for (Element element : checkboxesAndRadios) {
            WebElement webElement = new WebElement();
            webElement.setType("input-" + element.attr("type"));
            webElement.setLocator("css");
            webElement.setLocatorValue(WebUtil.generateCssSelector(element));
            webElement.setId(element.id());
            webElement.setName(element.attr("name"));
            webElement.setChecked(element.hasAttr("checked"));
            
            elements.add(webElement);
        }
        
        // Extract other interactive elements like sliders, file uploads, etc.
        Elements otherInputs = doc.select("body input[type=range]:not(form input[type=range]), body input[type=file]:not(form input[type=file]), body input[type=color]:not(form input[type=color])");
        for (Element element : otherInputs) {
            WebElement webElement = new WebElement();
            webElement.setType("input-" + element.attr("type"));
            webElement.setLocator("css");
            webElement.setLocatorValue(WebUtil.generateCssSelector(element));
            webElement.setId(element.id());
            webElement.setName(element.attr("name"));
            
            // Add specific attributes for different types
            if (element.attr("type").equals("range")) {
                webElement.setAttribute("min", element.attr("min"));
                webElement.setAttribute("max", element.attr("max"));
                webElement.setAttribute("step", element.attr("step"));
            }
            
            elements.add(webElement);
        }
        
        // Extract div-based custom controls that might be interactive
        Elements customControls = doc.select("[role=button], [role=tab], [role=menuitem], [role=checkbox], [role=radio], [role=combobox], [role=slider]");
        for (Element element : customControls) {
            WebElement webElement = new WebElement();
            webElement.setType("custom-" + element.attr("role"));
            webElement.setLocator("css");
            webElement.setLocatorValue(WebUtil.generateCssSelector(element));
            webElement.setId(element.id());
            webElement.setName(element.attr("name"));
            webElement.setText(element.text());
            webElement.setAttribute("role", element.attr("role"));
            webElement.setAttribute("aria-label", element.attr("aria-label"));
            
            elements.add(webElement);
        }
    }
}
