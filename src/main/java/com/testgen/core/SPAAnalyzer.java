package com.testgen.core;

import com.testgen.model.WebElement;
import com.testgen.model.DynamicEvent;
import com.testgen.model.AjaxCall;
import com.testgen.model.RouteChange;
import com.testgen.exception.TestGenException;
import com.testgen.model.TestCase;
import com.testgen.model.TestCase.TestStep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Specialized analyzer for Single Page Applications (SPAs) that handles dynamic content,
 * AJAX calls, and client-side routing.
 */
public class SPAAnalyzer {
    // Default wait time in milliseconds for dynamic content to load
    private static final int DEFAULT_DYNAMIC_CONTENT_WAIT = 5000;
    // Default wait time for AJAX calls to complete
    private static final int DEFAULT_AJAX_TIMEOUT = 10000;
    // Default interval to check for content updates
    private static final int DEFAULT_POLLING_INTERVAL = 500;
    
    private int dynamicContentWaitTime;
    private int ajaxTimeout;
    private int pollingInterval;
    private boolean detectRouteChanges;
    private boolean captureNetworkCalls;
    
    /**
     * Creates a new SPAAnalyzer with default settings.
     */
    public SPAAnalyzer() {
        this.dynamicContentWaitTime = DEFAULT_DYNAMIC_CONTENT_WAIT;
        this.ajaxTimeout = DEFAULT_AJAX_TIMEOUT;
        this.pollingInterval = DEFAULT_POLLING_INTERVAL;
        this.detectRouteChanges = true;
        this.captureNetworkCalls = true;
    }
    
    /**
     * Creates a new SPAAnalyzer with custom settings.
     * 
     * @param dynamicContentWaitTime The time to wait for dynamic content to load (ms)
     * @param ajaxTimeout The timeout for AJAX calls (ms)
     * @param pollingInterval The interval to check for content updates (ms)
     * @param detectRouteChanges Whether to detect client-side route changes
     * @param captureNetworkCalls Whether to capture network calls
     */
    public SPAAnalyzer(
            int dynamicContentWaitTime, 
            int ajaxTimeout, 
            int pollingInterval,
            boolean detectRouteChanges,
            boolean captureNetworkCalls) {
        this.dynamicContentWaitTime = dynamicContentWaitTime;
        this.ajaxTimeout = ajaxTimeout;
        this.pollingInterval = pollingInterval;
        this.detectRouteChanges = detectRouteChanges;
        this.captureNetworkCalls = captureNetworkCalls;
    }
    
    /**
     * Analyzes a Single Page Application and extracts elements, including those 
     * that are loaded dynamically.
     * 
     * @param url The URL of the SPA
     * @return A list of web elements
     * @throws TestGenException If an error occurs during analysis
     */
    public List<WebElement> analyzeSPA(String url) throws TestGenException {
        // In a real implementation, this would use Selenium WebDriver to:
        // 1. Load the page
        // 2. Wait for initial content to load
        // 3. Extract all visible elements
        
        // This is a placeholder implementation
        List<WebElement> elements = new ArrayList<>();
        
        try {
            // Add simulated elements for a typical SPA
            WebElement appRoot = new WebElement("div");
            appRoot.setId("app-root");
            appRoot.setTagName("div");
            elements.add(appRoot);
            
            // Navigation component
            WebElement navBar = new WebElement("nav");
            navBar.setId("main-nav");
            navBar.setTagName("nav");
            navBar.setCssSelector("#main-nav");
            elements.add(navBar);
            
            // Add navigation links
            WebElement homeLink = new WebElement("a");
            homeLink.setId("nav-home");
            homeLink.setTagName("a");
            homeLink.setText("Home");
            homeLink.setHref("#/");
            homeLink.setCssSelector("#nav-home");
            elements.add(homeLink);
            
            WebElement productsLink = new WebElement("a");
            productsLink.setId("nav-products");
            productsLink.setTagName("a");
            productsLink.setText("Products");
            productsLink.setHref("#/products");
            productsLink.setCssSelector("#nav-products");
            elements.add(productsLink);
            
            WebElement contactLink = new WebElement("a");
            contactLink.setId("nav-contact");
            contactLink.setTagName("a");
            contactLink.setText("Contact");
            contactLink.setHref("#/contact");
            contactLink.setCssSelector("#nav-contact");
            elements.add(contactLink);
            
            // Main content area
            WebElement mainContent = new WebElement("div");
            mainContent.setId("main-content");
            mainContent.setTagName("div");
            mainContent.setCssSelector("#main-content");
            elements.add(mainContent);
            
            // Dynamic elements that would appear after route change
            WebElement productList = new WebElement("ul");
            productList.setId("product-list");
            productList.setTagName("ul");
            productList.setCssSelector("#product-list");
            productList.setDynamic(true);
            productList.setLoadTrigger("route:products");
            elements.add(productList);
            
            // Product items that would load via AJAX
            for (int i = 1; i <= 5; i++) {
                WebElement productItem = new WebElement("li");
                productItem.setId("product-" + i);
                productItem.setTagName("li");
                productItem.setText("Product " + i);
                productItem.setCssSelector("#product-list li:nth-child(" + i + ")");
                productItem.setDynamic(true);
                productItem.setLoadTrigger("ajax:products");
                elements.add(productItem);
            }
            
            // Contact form that appears on route change
            WebElement contactForm = new WebElement("form");
            contactForm.setId("contact-form");
            contactForm.setTagName("form");
            contactForm.setCssSelector("#contact-form");
            contactForm.setDynamic(true);
            contactForm.setLoadTrigger("route:contact");
            elements.add(contactForm);
            
            // Form fields
            WebElement nameField = new WebElement("input");
            nameField.setId("contact-name");
            nameField.setName("name");
            nameField.setTagName("input");
            nameField.setType("text");
            nameField.setCssSelector("#contact-name");
            nameField.setDynamic(true);
            nameField.setLoadTrigger("route:contact");
            elements.add(nameField);
            
            WebElement emailField = new WebElement("input");
            emailField.setId("contact-email");
            emailField.setName("email");
            emailField.setTagName("input");
            emailField.setType("email");
            emailField.setCssSelector("#contact-email");
            emailField.setDynamic(true);
            emailField.setLoadTrigger("route:contact");
            elements.add(emailField);
            
            WebElement messageField = new WebElement("textarea");
            messageField.setId("contact-message");
            messageField.setName("message");
            messageField.setTagName("textarea");
            messageField.setCssSelector("#contact-message");
            messageField.setDynamic(true);
            messageField.setLoadTrigger("route:contact");
            elements.add(messageField);
            
            WebElement submitButton = new WebElement("button");
            submitButton.setId("contact-submit");
            submitButton.setTagName("button");
            submitButton.setType("submit");
            submitButton.setText("Send Message");
            submitButton.setCssSelector("#contact-submit");
            submitButton.setDynamic(true);
            submitButton.setLoadTrigger("route:contact");
            elements.add(submitButton);
            
            // Feedback message that appears after form submission
            WebElement feedbackMessage = new WebElement("div");
            feedbackMessage.setId("submission-feedback");
            feedbackMessage.setTagName("div");
            feedbackMessage.setCssSelector("#submission-feedback");
            feedbackMessage.setDynamic(true);
            feedbackMessage.setLoadTrigger("event:submit");
            feedbackMessage.setDynamicState("hidden");
            elements.add(feedbackMessage);
            
        } catch (Exception e) {
            throw new TestGenException("Error analyzing SPA: " + e.getMessage(), e);
        }
        
        return elements;
    }
    
    /**
     * Detects all client-side routes in the SPA.
     * 
     * @param url The URL of the SPA
     * @return A list of detected routes
     * @throws TestGenException If an error occurs during analysis
     */
    public List<RouteChange> detectRoutes(String url) throws TestGenException {
        // In a real implementation, this would:
        // 1. Analyze the app structure
        // 2. Find clickable elements that trigger route changes
        // 3. Navigate through routes and record state changes
        
        // This is a placeholder implementation
        List<RouteChange> routes = new ArrayList<>();
        
        try {
            // Add simulated routes
            routes.add(new RouteChange("Home", "/", "#/", "#nav-home"));
            routes.add(new RouteChange("Products", "/products", "#/products", "#nav-products"));
            routes.add(new RouteChange("Contact", "/contact", "#/contact", "#nav-contact"));
            routes.add(new RouteChange("Product Detail", "/products/:id", "#/products/1", "#product-1"));
        } catch (Exception e) {
            throw new TestGenException("Error detecting routes: " + e.getMessage(), e);
        }
        
        return routes;
    }
    
    /**
     * Captures AJAX calls made by the SPA.
     * 
     * @param url The URL of the SPA
     * @return A list of detected AJAX calls
     * @throws TestGenException If an error occurs during analysis
     */
    public List<AjaxCall> captureAjaxCalls(String url) throws TestGenException {
        // In a real implementation, this would:
        // 1. Monitor network traffic
        // 2. Record XHR/fetch requests
        // 3. Record response patterns
        
        // This is a placeholder implementation
        List<AjaxCall> ajaxCalls = new ArrayList<>();
        
        try {
            // Add simulated AJAX calls
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            
            // GET products
            AjaxCall getProducts = new AjaxCall();
            getProducts.setUrl("/api/products");
            getProducts.setMethod("GET");
            getProducts.setHeaders(headers);
            getProducts.setTrigger("route:products");
            getProducts.setSuccessStatusCode(200);
            getProducts.setResponseType("application/json");
            ajaxCalls.add(getProducts);
            
            // GET product details
            AjaxCall getProductDetails = new AjaxCall();
            getProductDetails.setUrl("/api/products/{id}");
            getProductDetails.setMethod("GET");
            getProductDetails.setHeaders(headers);
            getProductDetails.setTrigger("click:#product-item");
            getProductDetails.setSuccessStatusCode(200);
            getProductDetails.setResponseType("application/json");
            ajaxCalls.add(getProductDetails);
            
            // POST contact form
            AjaxCall postContact = new AjaxCall();
            postContact.setUrl("/api/contact");
            postContact.setMethod("POST");
            postContact.setHeaders(headers);
            postContact.setTrigger("submit:#contact-form");
            postContact.setRequestBody("{\"name\":\"value\",\"email\":\"value\",\"message\":\"value\"}");
            postContact.setSuccessStatusCode(201);
            postContact.setResponseType("application/json");
            ajaxCalls.add(postContact);
            
        } catch (Exception e) {
            throw new TestGenException("Error capturing AJAX calls: " + e.getMessage(), e);
        }
        
        return ajaxCalls;
    }
    
    /**
     * Detects dynamic UI events in the SPA.
     * 
     * @param url The URL of the SPA
     * @return A list of detected dynamic events
     * @throws TestGenException If an error occurs during analysis
     */
    public List<DynamicEvent> detectDynamicEvents(String url) throws TestGenException {
        // In a real implementation, this would:
        // 1. Monitor DOM changes
        // 2. Record user interactions that cause UI updates
        // 3. Detect loading states, animations, etc.
        
        // This is a placeholder implementation
        List<DynamicEvent> events = new ArrayList<>();
        
        try {
            // Add simulated dynamic events
            
            // Modal dialog
            DynamicEvent modalEvent = new DynamicEvent();
            modalEvent.setEventType("modal");
            modalEvent.setTriggerSelector("#show-modal-button");
            modalEvent.setTargetSelector("#modal-dialog");
            modalEvent.setInitialState("hidden");
            modalEvent.setFinalState("visible");
            events.add(modalEvent);
            
            // Dropdown menu
            DynamicEvent dropdownEvent = new DynamicEvent();
            dropdownEvent.setEventType("dropdown");
            dropdownEvent.setTriggerSelector("#user-menu-button");
            dropdownEvent.setTargetSelector("#user-dropdown");
            dropdownEvent.setInitialState("hidden");
            dropdownEvent.setFinalState("visible");
            events.add(dropdownEvent);
            
            // Loading spinner
            DynamicEvent spinnerEvent = new DynamicEvent();
            spinnerEvent.setEventType("loading");
            spinnerEvent.setTriggerSelector("#load-more-button");
            spinnerEvent.setTargetSelector("#loading-spinner");
            spinnerEvent.setInitialState("hidden");
            spinnerEvent.setFinalState("visible");
            spinnerEvent.setTimeout(3000); // Spinner disappears after 3 seconds
            events.add(spinnerEvent);
            
            // Tab switching
            DynamicEvent tabEvent = new DynamicEvent();
            tabEvent.setEventType("tab");
            tabEvent.setTriggerSelector(".tab-button");
            tabEvent.setTargetSelector(".tab-content");
            tabEvent.setDependsOnAttribute("data-tab-id");
            events.add(tabEvent);
            
            // Form validation feedback
            DynamicEvent validationEvent = new DynamicEvent();
            validationEvent.setEventType("validation");
            validationEvent.setTriggerSelector("input, textarea");
            validationEvent.setTargetSelector(".validation-message");
            validationEvent.setEventName("blur");
            events.add(validationEvent);
            
        } catch (Exception e) {
            throw new TestGenException("Error detecting dynamic events: " + e.getMessage(), e);
        }
        
        return events;
    }
    
    /**
     * Generates SPA-specific test cases for a list of web elements and dynamic events.
     *
     * @param elements The web elements
     * @param routes The detected routes
     * @param ajaxCalls The detected AJAX calls
     * @param dynamicEvents The detected dynamic events
     * @param pageName The name of the page
     * @return A list of test cases
     */
    public List<TestCase> generateSPATestCases(
            List<WebElement> elements, 
            List<RouteChange> routes,
            List<AjaxCall> ajaxCalls,
            List<DynamicEvent> dynamicEvents,
            String pageName) {
        
        List<TestCase> testCases = new ArrayList<>();
        
        // Generate route navigation tests
        TestCase routeTest = new TestCase(
                pageName + " - Route Navigation Test",
                "Test navigation between different routes in the " + pageName + " SPA",
                TestCase.TestType.WEB_UI
        );
        
        routeTest.addStep(new TestStep(
                "Navigate to " + pageName,
                "The " + pageName + " page should load successfully with the home route active"
        ));
        
        for (RouteChange route : routes) {
            routeTest.addStep(new TestStep(
                    "Click on the " + route.getRouteName() + " navigation link",
                    "URL should change to " + route.getClientPath() + " and corresponding content should be displayed"
            ));
        }
        
        testCases.add(routeTest);
        
        // Generate AJAX loading tests
        TestCase ajaxTest = new TestCase(
                pageName + " - AJAX Loading Test",
                "Test dynamic content loading via AJAX in the " + pageName + " SPA",
                TestCase.TestType.WEB_UI
        );
        
        ajaxTest.addStep(new TestStep(
                "Navigate to " + pageName,
                "The " + pageName + " page should load successfully"
        ));
        
        for (AjaxCall ajaxCall : ajaxCalls) {
            ajaxTest.addStep(new TestStep(
                    "Trigger AJAX call with " + ajaxCall.getTrigger(),
                    "The request to " + ajaxCall.getUrl() + " should complete successfully and update the UI"
            ));
        }
        
        testCases.add(ajaxTest);
        
        // Generate dynamic UI interaction tests
        TestCase dynamicUiTest = new TestCase(
                pageName + " - Dynamic UI Interaction Test",
                "Test dynamic UI elements and interactions in the " + pageName + " SPA",
                TestCase.TestType.WEB_UI
        );
        
        dynamicUiTest.addStep(new TestStep(
                "Navigate to " + pageName,
                "The " + pageName + " page should load successfully"
        ));
        
        for (DynamicEvent event : dynamicEvents) {
            dynamicUiTest.addStep(new TestStep(
                    "Trigger the " + event.getEventType() + " event by " + event.getEventName() + " on " + event.getTriggerSelector(),
                    "The target element " + event.getTargetSelector() + " should change from " + 
                            event.getInitialState() + " to " + event.getFinalState()
            ));
        }
        
        testCases.add(dynamicUiTest);
        
        // Generate form submission test if contact form is present
        boolean hasContactForm = elements.stream()
                .anyMatch(e -> e.getId() != null && e.getId().equals("contact-form"));
        
        if (hasContactForm) {
            TestCase formTest = new TestCase(
                    pageName + " - Form Submission Test",
                    "Test form submission in the " + pageName + " SPA",
                    TestCase.TestType.WEB_UI
            );
            
            formTest.addStep(new TestStep(
                    "Navigate to " + pageName + " contact page",
                    "The contact form should be displayed"
            ));
            
            formTest.addStep(new TestStep(
                    "Fill in the name field",
                    "Text should be entered in the field"
            ));
            
            formTest.addStep(new TestStep(
                    "Fill in the email field",
                    "Text should be entered in the field"
            ));
            
            formTest.addStep(new TestStep(
                    "Fill in the message field",
                    "Text should be entered in the field"
            ));
            
            formTest.addStep(new TestStep(
                    "Click the submit button",
                    "Form should be submitted via AJAX and success message should be displayed"
            ));
            
            testCases.add(formTest);
        }
        
        return testCases;
    }
    
    /**
     * Sets the wait time for dynamic content to load.
     * 
     * @param waitTime The wait time in milliseconds
     */
    public void setDynamicContentWaitTime(int waitTime) {
        this.dynamicContentWaitTime = waitTime;
    }
    
    /**
     * Sets the timeout for AJAX calls.
     * 
     * @param timeout The timeout in milliseconds
     */
    public void setAjaxTimeout(int timeout) {
        this.ajaxTimeout = timeout;
    }
    
    /**
     * Sets the polling interval for content updates.
     * 
     * @param interval The polling interval in milliseconds
     */
    public void setPollingInterval(int interval) {
        this.pollingInterval = interval;
    }
    
    /**
     * Sets whether to detect client-side route changes.
     * 
     * @param detect Whether to detect route changes
     */
    public void setDetectRouteChanges(boolean detect) {
        this.detectRouteChanges = detect;
    }
    
    /**
     * Sets whether to capture network calls.
     * 
     * @param capture Whether to capture network calls
     */
    public void setCaptureNetworkCalls(boolean capture) {
        this.captureNetworkCalls = capture;
    }
}
