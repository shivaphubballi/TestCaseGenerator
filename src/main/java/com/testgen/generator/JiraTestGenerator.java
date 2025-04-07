package com.testgen.generator;

import com.testgen.exception.TestGenException;
import com.testgen.model.ApiEndpoint;
import com.testgen.model.TestCase;
import com.testgen.model.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Generates Jira-compatible test cases from web elements and API endpoints.
 */
public class JiraTestGenerator {
    
    /**
     * Generates Jira test cases from web elements found on a page.
     *
     * @param elements List of web elements
     * @param pageUrl URL of the web page
     * @param pageName Name of the web page (optional, derived from URL if null)
     * @return List of TestCase objects formatted for Jira
     * @throws TestGenException If test generation fails
     */
    public List<TestCase> generateFromWebElements(List<WebElement> elements, String pageUrl, String pageName) 
            throws TestGenException {
        if (elements == null || elements.isEmpty()) {
            throw new TestGenException("No web elements provided for Jira test case generation");
        }
        
        List<TestCase> testCases = new ArrayList<>();
        
        // Derive page name from URL if not provided
        if (pageName == null || pageName.isEmpty()) {
            pageName = extractPageName(pageUrl);
        }
        
        // Generate test cases for form submissions
        List<WebElement> forms = new ArrayList<>();
        for (WebElement element : elements) {
            if ("form".equals(element.getType())) {
                forms.add(element);
            }
        }
        
        if (!forms.isEmpty()) {
            for (WebElement form : forms) {
                TestCase formTest = generateFormTestCase(form, elements, pageUrl, pageName);
                testCases.add(formTest);
            }
        }
        
        // Generate a navigation test for all links
        List<WebElement> links = new ArrayList<>();
        for (WebElement element : elements) {
            if ("link".equals(element.getType())) {
                links.add(element);
            }
        }
        
        if (!links.isEmpty()) {
            TestCase navigationTest = generateNavigationTestCase(links, pageUrl, pageName);
            testCases.add(navigationTest);
        }
        
        // Generate test for interactive elements (not in forms)
        List<WebElement> interactiveElements = new ArrayList<>();
        for (WebElement element : elements) {
            if (!element.getType().equals("form") && element.getParentForm() == null &&
                (element.getType().startsWith("input-") || 
                 element.getType().equals("button") || 
                 element.getType().equals("select"))) {
                interactiveElements.add(element);
            }
        }
        
        if (!interactiveElements.isEmpty()) {
            TestCase interactiveTest = generateInteractiveElementsTestCase(interactiveElements, pageUrl, pageName);
            testCases.add(interactiveTest);
        }
        
        // Generate comprehensive test
        TestCase comprehensiveTest = generateComprehensiveTestCase(elements, pageUrl, pageName);
        testCases.add(comprehensiveTest);
        
        return testCases;
    }
    
    /**
     * Generates a form submission test case for Jira.
     *
     * @param form The form element
     * @param allElements All elements on the page
     * @param pageUrl URL of the page
     * @param pageName Name of the page
     * @return A TestCase object formatted for Jira
     */
    private TestCase generateFormTestCase(WebElement form, List<WebElement> allElements, String pageUrl, String pageName) {
        String formId = form.getId();
        String formName = form.getName();
        String formIdentifier = !formId.isEmpty() ? formId : (!formName.isEmpty() ? formName : "unnamed_form");
        
        TestCase testCase = new TestCase();
        testCase.setId(generateTestId());
        testCase.setName("Form Submission: " + formIdentifier + " on " + pageName);
        testCase.setSummary("Test the " + formIdentifier + " form submission on " + pageName);
        testCase.setDescription("This test case verifies that the " + formIdentifier + " form on " + pageName + " can be submitted correctly.");
        testCase.setType(TestCase.TestType.WEB_UI);
        testCase.setSourceUrl(pageUrl);
        
        // Add preconditions
        testCase.setPreconditions("1. Web browser is open\n2. User has access to " + pageUrl);
        
        // Add tags
        testCase.addTag("web");
        testCase.addTag("form");
        testCase.addTag(pageName.replaceAll("[^a-zA-Z0-9]", "_").toLowerCase());
        
        // Add metadata
        testCase.addMetadata("pageUrl", pageUrl);
        testCase.addMetadata("formId", formId);
        testCase.addMetadata("formName", formName);
        testCase.addMetadata("formLocator", form.getLocatorValue());
        testCase.addMetadata("formMethod", form.getMethod());
        testCase.addMetadata("formAction", form.getAction());
        
        // Step 1: Navigate to the page
        TestCase.TestStep step1 = new TestCase.TestStep();
        step1.setStepNumber(1);
        step1.setAction("Navigate to " + pageName + " page");
        step1.setExpectedResult("The " + pageName + " page is loaded successfully");
        step1.addTestData("url", pageUrl);
        testCase.addStep(step1);
        
        // Add steps for filling form fields
        int stepNum = 2;
        List<WebElement> formElements = new ArrayList<>();
        for (WebElement element : allElements) {
            if (form.getLocatorValue().equals(element.getParentForm())) {
                formElements.add(element);
            }
        }
        
        for (WebElement element : formElements) {
            if (element.getType().startsWith("input-") || element.getType().equals("textarea") || element.getType().equals("select")) {
                TestCase.TestStep step = new TestCase.TestStep();
                step.setStepNumber(stepNum++);
                
                if (element.getType().equals("input-checkbox") || element.getType().equals("input-radio")) {
                    step.setAction("Select the " + element.getDescriptiveName() + " " + element.getType().replace("input-", ""));
                    step.setExpectedResult("The " + element.getType().replace("input-", "") + " is selected");
                } else if (element.getType().equals("select")) {
                    String optionText = "first option";
                    if (!element.getOptions().isEmpty()) {
                        optionText = element.getOptions().get(0);
                    }
                    step.setAction("Select '" + optionText + "' from the " + element.getDescriptiveName() + " dropdown");
                    step.setExpectedResult("The option '" + optionText + "' is selected");
                    step.addTestData("value", optionText);
                } else {
                    String testData = element.getSuggestedTestData();
                    step.setAction("Enter '" + testData + "' in the " + element.getDescriptiveName() + " field");
                    step.setExpectedResult("The text is entered in the field");
                    step.addTestData("value", testData);
                }
                
                step.addTestData("locator", element.getLocatorValue());
                step.addTestData("locatorType", "css");
                step.addTestData("elementType", element.getType());
                testCase.addStep(step);
            }
        }
        
        // Add step for form submission
        TestCase.TestStep submitStep = new TestCase.TestStep();
        submitStep.setStepNumber(stepNum++);
        
        // Find a submit button
        WebElement submitButton = null;
        for (WebElement element : formElements) {
            if (element.getType().equals("input-submit") || 
                (element.getType().equals("button") && element.getText() != null && 
                (element.getText().toLowerCase().contains("submit") || 
                 element.getText().toLowerCase().contains("save") || 
                 element.getText().toLowerCase().contains("send")))) {
                submitButton = element;
                break;
            }
        }
        
        if (submitButton != null) {
            submitStep.setAction("Click the " + submitButton.getDescriptiveName() + " button");
            submitStep.addTestData("locator", submitButton.getLocatorValue());
            submitStep.addTestData("locatorType", "css");
        } else {
            submitStep.setAction("Submit the form");
            submitStep.addTestData("locator", form.getLocatorValue());
            submitStep.addTestData("locatorType", "css");
        }
        
        submitStep.setExpectedResult("The form is submitted successfully");
        testCase.addStep(submitStep);
        
        // Add verification step
        TestCase.TestStep verifyStep = new TestCase.TestStep();
        verifyStep.setStepNumber(stepNum);
        
        String formAction = form.getAction();
        if (formAction != null && !formAction.isEmpty()) {
            if (formAction.startsWith("http")) {
                verifyStep.setAction("Verify the browser navigates to the correct page");
                verifyStep.setExpectedResult("The browser navigates to " + formAction);
                verifyStep.addTestData("expected", formAction);
            } else {
                verifyStep.setAction("Verify the browser navigates to the correct page");
                verifyStep.setExpectedResult("The browser navigates to a page with path " + formAction);
                verifyStep.addTestData("expected", formAction);
            }
        } else {
            verifyStep.setAction("Verify the form submission result");
            verifyStep.setExpectedResult("The form submission is successful (e.g., success message is displayed, data is saved, etc.)");
        }
        
        testCase.addStep(verifyStep);
        
        return testCase;
    }
    
    /**
     * Generates a navigation test case for Jira.
     *
     * @param links List of link elements
     * @param pageUrl URL of the page
     * @param pageName Name of the page
     * @return A TestCase object formatted for Jira
     */
    private TestCase generateNavigationTestCase(List<WebElement> links, String pageUrl, String pageName) {
        TestCase testCase = new TestCase();
        testCase.setId(generateTestId());
        testCase.setName("Navigation: " + pageName + " Links");
        testCase.setSummary("Test navigation links on " + pageName);
        testCase.setDescription("This test case verifies that navigation links on " + pageName + " work correctly.");
        testCase.setType(TestCase.TestType.WEB_UI);
        testCase.setSourceUrl(pageUrl);
        
        // Add preconditions
        testCase.setPreconditions("1. Web browser is open\n2. User has access to " + pageUrl);
        
        // Add tags
        testCase.addTag("web");
        testCase.addTag("navigation");
        testCase.addTag(pageName.replaceAll("[^a-zA-Z0-9]", "_").toLowerCase());
        
        // Add metadata
        testCase.addMetadata("pageUrl", pageUrl);
        testCase.addMetadata("linkCount", String.valueOf(links.size()));
        
        // Step 1: Navigate to the page
        TestCase.TestStep step1 = new TestCase.TestStep();
        step1.setStepNumber(1);
        step1.setAction("Navigate to " + pageName + " page");
        step1.setExpectedResult("The " + pageName + " page is loaded successfully");
        step1.addTestData("url", pageUrl);
        testCase.addStep(step1);
        
        // Add steps for clicking links (limit to 5 to keep it manageable)
        int maxLinks = Math.min(links.size(), 5);
        for (int i = 0; i < maxLinks; i++) {
            WebElement link = links.get(i);
            String linkText = link.getText() != null && !link.getText().isEmpty() 
                ? link.getText() : "link " + link.getDescriptiveName();
            
            TestCase.TestStep linkStep = new TestCase.TestStep();
            linkStep.setStepNumber(i + 2);
            linkStep.setAction("Click on the '" + linkText + "' link");
            
            if (link.getHref() != null && !link.getHref().isEmpty()) {
                linkStep.setExpectedResult("Browser navigates to " + link.getHref());
                linkStep.addTestData("expected", link.getHref());
            } else {
                linkStep.setExpectedResult("Browser navigates to the correct page");
            }
            
            linkStep.addTestData("locator", link.getLocatorValue());
            linkStep.addTestData("locatorType", "css");
            linkStep.addTestData("href", link.getHref());
            testCase.addStep(linkStep);
            
            // Add step to navigate back
            TestCase.TestStep backStep = new TestCase.TestStep();
            backStep.setStepNumber(i + 2 + maxLinks);
            backStep.setAction("Navigate back to " + pageName + " page");
            backStep.setExpectedResult("The " + pageName + " page is loaded successfully");
            backStep.addTestData("url", pageUrl);
            testCase.addStep(backStep);
        }
        
        return testCase;
    }
    
    /**
     * Generates a test case for interactive elements not in forms.
     *
     * @param elements List of interactive elements
     * @param pageUrl URL of the page
     * @param pageName Name of the page
     * @return A TestCase object formatted for Jira
     */
    private TestCase generateInteractiveElementsTestCase(List<WebElement> elements, String pageUrl, String pageName) {
        TestCase testCase = new TestCase();
        testCase.setId(generateTestId());
        testCase.setName("Interactive Elements: " + pageName);
        testCase.setSummary("Test interactive elements on " + pageName);
        testCase.setDescription("This test case verifies that interactive elements on " + pageName + " function correctly.");
        testCase.setType(TestCase.TestType.WEB_UI);
        testCase.setSourceUrl(pageUrl);
        
        // Add preconditions
        testCase.setPreconditions("1. Web browser is open\n2. User has access to " + pageUrl);
        
        // Add tags
        testCase.addTag("web");
        testCase.addTag("interactive");
        testCase.addTag(pageName.replaceAll("[^a-zA-Z0-9]", "_").toLowerCase());
        
        // Add metadata
        testCase.addMetadata("pageUrl", pageUrl);
        testCase.addMetadata("elementCount", String.valueOf(elements.size()));
        
        // Step 1: Navigate to the page
        TestCase.TestStep step1 = new TestCase.TestStep();
        step1.setStepNumber(1);
        step1.setAction("Navigate to " + pageName + " page");
        step1.setExpectedResult("The " + pageName + " page is loaded successfully");
        step1.addTestData("url", pageUrl);
        testCase.addStep(step1);
        
        // Add steps for interacting with elements
        int stepNum = 2;
        for (WebElement element : elements) {
            String elementType = element.getType();
            
            TestCase.TestStep step = new TestCase.TestStep();
            step.setStepNumber(stepNum++);
            
            if (elementType.equals("button") || elementType.startsWith("custom-")) {
                step.setAction("Click on the " + element.getDescriptiveName() + " button");
                step.setExpectedResult("The button responds correctly to the click (e.g., action is performed, page updates, etc.)");
            } else if (elementType.equals("input-text") || elementType.equals("input-email") || 
                     elementType.equals("input-password") || elementType.equals("textarea")) {
                String testData = element.getSuggestedTestData();
                step.setAction("Enter '" + testData + "' in the " + element.getDescriptiveName() + " field");
                step.setExpectedResult("The text is entered in the field");
                step.addTestData("value", testData);
            } else if (elementType.equals("input-checkbox") || elementType.equals("input-radio")) {
                step.setAction("Toggle the " + element.getDescriptiveName() + " " + elementType.replace("input-", ""));
                step.setExpectedResult("The " + elementType.replace("input-", "") + " state is toggled");
            } else if (elementType.equals("select")) {
                String optionText = "first option";
                if (!element.getOptions().isEmpty()) {
                    optionText = element.getOptions().get(0);
                }
                step.setAction("Select '" + optionText + "' from the " + element.getDescriptiveName() + " dropdown");
                step.setExpectedResult("The option '" + optionText + "' is selected");
                step.addTestData("value", optionText);
            } else {
                step.setAction("Verify the " + element.getDescriptiveName() + " element is displayed");
                step.setExpectedResult("The element is visible on the page");
            }
            
            step.addTestData("locator", element.getLocatorValue());
            step.addTestData("locatorType", "css");
            step.addTestData("elementType", element.getType());
            testCase.addStep(step);
        }
        
        return testCase;
    }
    
    /**
     * Generates a comprehensive test case for Jira that covers multiple aspects of the page.
     *
     * @param elements All elements on the page
     * @param pageUrl URL of the page
     * @param pageName Name of the page
     * @return A TestCase object formatted for Jira
     */
    private TestCase generateComprehensiveTestCase(List<WebElement> elements, String pageUrl, String pageName) {
        TestCase testCase = new TestCase();
        testCase.setId(generateTestId());
        testCase.setName("Comprehensive Test: " + pageName);
        testCase.setSummary("Comprehensive test for " + pageName);
        testCase.setDescription("This test case performs a comprehensive verification of " + pageName + " functionality.");
        testCase.setType(TestCase.TestType.WEB_UI);
        testCase.setSourceUrl(pageUrl);
        
        // Add preconditions
        testCase.setPreconditions("1. Web browser is open\n2. User has access to " + pageUrl);
        
        // Add tags
        testCase.addTag("web");
        testCase.addTag("comprehensive");
        testCase.addTag(pageName.replaceAll("[^a-zA-Z0-9]", "_").toLowerCase());
        
        // Add metadata
        testCase.addMetadata("pageUrl", pageUrl);
        testCase.addMetadata("elementCount", String.valueOf(elements.size()));
        
        // Step 1: Navigate to the page
        TestCase.TestStep step1 = new TestCase.TestStep();
        step1.setStepNumber(1);
        step1.setAction("Navigate to " + pageName + " page");
        step1.setExpectedResult("The " + pageName + " page is loaded successfully");
        step1.addTestData("url", pageUrl);
        testCase.addStep(step1);
        
        // Step 2: Verify page title
        TestCase.TestStep step2 = new TestCase.TestStep();
        step2.setStepNumber(2);
        step2.setAction("Verify the page title");
        step2.setExpectedResult("The page title contains or matches the expected value");
        testCase.addStep(step2);
        
        // Select representative elements for interaction
        List<WebElement> inputElements = new ArrayList<>();
        List<WebElement> buttons = new ArrayList<>();
        List<WebElement> selectElements = new ArrayList<>();
        
        int inputCount = 0;
        int buttonCount = 0;
        int selectCount = 0;
        
        for (WebElement element : elements) {
            if ((element.getType().startsWith("input-") || element.getType().equals("textarea")) && inputCount < 3) {
                inputElements.add(element);
                inputCount++;
            } else if ((element.getType().equals("button") || element.getType().equals("input-submit")) && buttonCount < 2) {
                buttons.add(element);
                buttonCount++;
            } else if (element.getType().equals("select") && selectCount < 2) {
                selectElements.add(element);
                selectCount++;
            }
        }
        
        int stepNum = 3;
        
        // Add steps for input elements
        for (WebElement input : inputElements) {
            TestCase.TestStep step = new TestCase.TestStep();
            step.setStepNumber(stepNum++);
            
            if (input.getType().equals("input-checkbox") || input.getType().equals("input-radio")) {
                step.setAction("Toggle the " + input.getDescriptiveName() + " " + input.getType().replace("input-", ""));
                step.setExpectedResult("The " + input.getType().replace("input-", "") + " state is toggled");
            } else {
                String testData = input.getSuggestedTestData();
                step.setAction("Enter '" + testData + "' in the " + input.getDescriptiveName() + " field");
                step.setExpectedResult("The text is entered in the field");
                step.addTestData("value", testData);
            }
            
            step.addTestData("locator", input.getLocatorValue());
            step.addTestData("locatorType", "css");
            step.addTestData("elementType", input.getType());
            testCase.addStep(step);
        }
        
        // Add steps for select elements
        for (WebElement select : selectElements) {
            TestCase.TestStep step = new TestCase.TestStep();
            step.setStepNumber(stepNum++);
            
            String optionText = "first option";
            if (!select.getOptions().isEmpty()) {
                optionText = select.getOptions().get(0);
            }
            
            step.setAction("Select '" + optionText + "' from the " + select.getDescriptiveName() + " dropdown");
            step.setExpectedResult("The option '" + optionText + "' is selected");
            step.addTestData("value", optionText);
            step.addTestData("locator", select.getLocatorValue());
            step.addTestData("locatorType", "css");
            step.addTestData("elementType", select.getType());
            testCase.addStep(step);
        }
        
        // Add button click step
        if (!buttons.isEmpty()) {
            WebElement button = buttons.get(0);
            TestCase.TestStep step = new TestCase.TestStep();
            step.setStepNumber(stepNum++);
            step.setAction("Click on the " + button.getDescriptiveName() + " button");
            step.setExpectedResult("The button responds correctly to the click (e.g., action is performed, page updates, etc.)");
            step.addTestData("locator", button.getLocatorValue());
            step.addTestData("locatorType", "css");
            step.addTestData("elementType", button.getType());
            testCase.addStep(step);
        }
        
        // Add final verification step
        TestCase.TestStep finalStep = new TestCase.TestStep();
        finalStep.setStepNumber(stepNum);
        finalStep.setAction("Verify the overall page state after interactions");
        finalStep.setExpectedResult("The page state reflects all the interactions performed");
        testCase.addStep(finalStep);
        
        return testCase;
    }
    
    /**
     * Generates Jira test cases from API endpoints found in a Postman collection.
     *
     * @param endpoints List of API endpoints
     * @param collectionName Name of the Postman collection
     * @return List of TestCase objects formatted for Jira
     * @throws TestGenException If test generation fails
     */
    public List<TestCase> generateFromApiEndpoints(List<ApiEndpoint> endpoints, String collectionName) 
            throws TestGenException {
        if (endpoints == null || endpoints.isEmpty()) {
            throw new TestGenException("No API endpoints provided for Jira test case generation");
        }
        
        List<TestCase> testCases = new ArrayList<>();
        
        for (ApiEndpoint endpoint : endpoints) {
            TestCase endpointTest = generateApiEndpointTestCase(endpoint, collectionName);
            testCases.add(endpointTest);
        }
        
        return testCases;
    }
    
    /**
     * Generates a Jira test case for a single API endpoint.
     *
     * @param endpoint The API endpoint
     * @param collectionName Name of the Postman collection
     * @return A TestCase object formatted for Jira
     */
    private TestCase generateApiEndpointTestCase(ApiEndpoint endpoint, String collectionName) {
        TestCase testCase = new TestCase();
        testCase.setId(generateTestId());
        testCase.setName("API Test: " + endpoint.getName());
        testCase.setSummary(endpoint.getSummary());
        
        StringBuilder descBuilder = new StringBuilder();
        descBuilder.append("Test the API endpoint: ").append(endpoint.getMethod()).append(" ").append(endpoint.getUrl());
        
        if (endpoint.getDescription() != null && !endpoint.getDescription().isEmpty()) {
            descBuilder.append("\n\nDescription: ").append(endpoint.getDescription());
        }
        
        testCase.setDescription(descBuilder.toString());
        testCase.setType(TestCase.TestType.API);
        testCase.setSourceUrl(endpoint.getUrl());
        
        // Add preconditions
        StringBuilder preCondBuilder = new StringBuilder();
        preCondBuilder.append("1. API service is available\n");
        preCondBuilder.append("2. Required authentication tokens/credentials are available (if needed)\n");
        
        // Add any specific preconditions for this endpoint
        if (endpoint.getHeaders().containsKey("Authorization")) {
            preCondBuilder.append("3. User is authenticated with valid credentials\n");
        }
        
        testCase.setPreconditions(preCondBuilder.toString());
        
        // Add tags
        testCase.addTag("api");
        testCase.addTag(endpoint.getMethod().toLowerCase());
        if (collectionName != null && !collectionName.isEmpty()) {
            testCase.addTag(collectionName.replaceAll("[^a-zA-Z0-9]", "_").toLowerCase());
        }
        if (endpoint.getFolderPath() != null && !endpoint.getFolderPath().isEmpty()) {
            testCase.addTag(endpoint.getFolderPath().replaceAll("[^a-zA-Z0-9]", "_").toLowerCase());
        }
        
        // Add metadata
        testCase.addMetadata("method", endpoint.getMethod());
        testCase.addMetadata("url", endpoint.getUrl());
        testCase.addMetadata("path", endpoint.getPath());
        testCase.addMetadata("host", endpoint.getHost());
        testCase.addMetadata("collectionName", collectionName);
        testCase.addMetadata("folderPath", endpoint.getFolderPath());
        testCase.addMetadata("expectedStatus", String.valueOf(endpoint.getExpectedStatusCode()));
        testCase.addMetadata("contentType", endpoint.getHeaders().getOrDefault("Content-Type", ""));
        
        // Step 1: Prepare request
        TestCase.TestStep step1 = new TestCase.TestStep();
        step1.setStepNumber(1);
        step1.setAction("Prepare API request for " + endpoint.getMethod() + " " + endpoint.getPath());
        
        StringBuilder requestDetails = new StringBuilder();
        requestDetails.append("Request is prepared with the following details:\n");
        requestDetails.append("- Method: ").append(endpoint.getMethod()).append("\n");
        requestDetails.append("- URL: ").append(endpoint.getUrl()).append("\n");
        
        // Headers
        if (!endpoint.getHeaders().isEmpty()) {
            requestDetails.append("- Headers:\n");
            for (Map.Entry<String, String> header : endpoint.getHeaders().entrySet()) {
                requestDetails.append("  - ").append(header.getKey()).append(": ");
                
                // Mask sensitive headers
                if (header.getKey().toLowerCase().contains("auth") || 
                    header.getKey().toLowerCase().contains("key") || 
                    header.getKey().toLowerCase().contains("token") || 
                    header.getKey().toLowerCase().contains("secret")) {
                    requestDetails.append("********\n");
                } else {
                    requestDetails.append(header.getValue()).append("\n");
                }
                
                step1.addTestData(header.getKey(), header.getValue());
            }
        }
        
        // Query parameters
        if (!endpoint.getQueryParams().isEmpty()) {
            requestDetails.append("- Query Parameters:\n");
            for (Map.Entry<String, String> param : endpoint.getQueryParams().entrySet()) {
                requestDetails.append("  - ").append(param.getKey()).append(": ").append(param.getValue()).append("\n");
                step1.addTestData("query_" + param.getKey(), param.getValue());
            }
        }
        
        step1.setExpectedResult(requestDetails.toString());
        testCase.addStep(step1);
        
        // Step 2: Add request body (if applicable)
        if ((endpoint.getMethod().equalsIgnoreCase("POST") || 
             endpoint.getMethod().equalsIgnoreCase("PUT") || 
             endpoint.getMethod().equalsIgnoreCase("PATCH")) &&
            (endpoint.getRequestBody() != null && !endpoint.getRequestBody().isEmpty() || 
             !endpoint.getFormData().isEmpty())) {
            
            TestCase.TestStep step2 = new TestCase.TestStep();
            step2.setStepNumber(2);
            
            if (endpoint.getRequestBodyType().equals("formdata") || endpoint.getRequestBodyType().equals("urlencoded")) {
                step2.setAction("Set form data parameters");
                
                StringBuilder formDataDetails = new StringBuilder();
                formDataDetails.append("Form data is set with the following parameters:\n");
                
                for (Map.Entry<String, String> entry : endpoint.getFormData().entrySet()) {
                    formDataDetails.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                    step2.addTestData(entry.getKey(), entry.getValue());
                }
                
                step2.setExpectedResult(formDataDetails.toString());
            } else {
                step2.setAction("Set request body");
                step2.setExpectedResult("Request body is set to:\n" + endpoint.getRequestBody());
                step2.addTestData("body", endpoint.getRequestBody());
                step2.addTestData("bodyType", endpoint.getRequestBodyType());
            }
            
            testCase.addStep(step2);
        }
        
        // Step 3: Send request
        int sendStepNum = testCase.getSteps().size() + 1;
        TestCase.TestStep sendStep = new TestCase.TestStep();
        sendStep.setStepNumber(sendStepNum);
        sendStep.setAction("Send the " + endpoint.getMethod() + " request to " + endpoint.getPath());
        sendStep.setExpectedResult("Request is sent successfully");
        testCase.addStep(sendStep);
        
        // Step 4: Verify response status
        int verifyStepNum = sendStepNum + 1;
        TestCase.TestStep verifyStep = new TestCase.TestStep();
        verifyStep.setStepNumber(verifyStepNum);
        verifyStep.setAction("Verify response status code");
        verifyStep.setExpectedResult("Response has status code " + endpoint.getExpectedStatusCode());
        verifyStep.addTestData("expectedStatus", String.valueOf(endpoint.getExpectedStatusCode()));
        testCase.addStep(verifyStep);
        
        // Step 5: Verify response body (if applicable)
        boolean hasResponseVerification = false;
        
        if (!endpoint.getExampleResponses().isEmpty()) {
            for (Map.Entry<String, Object> exampleResponse : endpoint.getExampleResponses().get(0).entrySet()) {
                if (exampleResponse.getKey().equals("jsonBody") && exampleResponse.getValue() != null) {
                    hasResponseVerification = true;
                    break;
                } else if (exampleResponse.getKey().equals("body") && exampleResponse.getValue() != null) {
                    hasResponseVerification = true;
                    break;
                }
            }
        }
        
        if (hasResponseVerification) {
            int responseStepNum = verifyStepNum + 1;
            TestCase.TestStep responseStep = new TestCase.TestStep();
            responseStep.setStepNumber(responseStepNum);
            responseStep.setAction("Verify response body");
            responseStep.setExpectedResult("Response body matches the expected format and contains expected data");
            
            // Add expected response details from example if available
            if (!endpoint.getExampleResponses().isEmpty()) {
                Map<String, Object> exampleResponse = endpoint.getExampleResponses().get(0);
                if (exampleResponse.containsKey("body")) {
                    String body = exampleResponse.get("body").toString();
                    if (body.length() > 500) {
                        body = body.substring(0, 500) + "... (truncated)";
                    }
                    responseStep.addTestData("expectedResponseBody", body);
                }
            }
            
            testCase.addStep(responseStep);
        }
        
        return testCase;
    }
    
    /**
     * Extracts a page name from a URL.
     *
     * @param url The URL
     * @return A simple page name
     */
    private String extractPageName(String url) {
        if (url == null || url.isEmpty()) {
            return "Unknown Page";
        }
        
        // Remove protocol
        String noProtocol = url.replaceFirst("https?://", "");
        
        // Remove query params
        int queryIndex = noProtocol.indexOf('?');
        if (queryIndex > 0) {
            noProtocol = noProtocol.substring(0, queryIndex);
        }
        
        // Remove fragment
        int fragmentIndex = noProtocol.indexOf('#');
        if (fragmentIndex > 0) {
            noProtocol = noProtocol.substring(0, fragmentIndex);
        }
        
        // Split by path separator
        String[] parts = noProtocol.split("/");
        
        // Get the last non-empty part
        for (int i = parts.length - 1; i >= 0; i--) {
            if (!parts[i].isEmpty()) {
                // Convert to title case and replace hyphens/underscores with spaces
                String pageName = parts[i].replaceAll("[_-]", " ");
                pageName = pageName.substring(0, 1).toUpperCase() + pageName.substring(1);
                return pageName;
            }
        }
        
        // If we get here, use the domain
        return parts[0];
    }
    
    /**
     * Generates a random test ID.
     *
     * @return A test ID
     */
    private String generateTestId() {
        return "TC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    /**
     * Converts the provided test cases to Jira-compatible format.
     *
     * @param testCases List of test cases
     * @return List of Jira-formatted test cases as strings
     */
    public List<String> convertToJiraFormat(List<TestCase> testCases) {
        List<String> jiraTestCases = new ArrayList<>();
        
        for (TestCase testCase : testCases) {
            jiraTestCases.add(testCase.toJiraFormat());
        }
        
        return jiraTestCases;
    }
}
