package com.testgen.generator;

import com.testgen.exception.TestGenException;
import com.testgen.model.TestCase;
import com.testgen.model.WebElement;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Generates Selenium WebDriver test code from TestCase objects.
 */
public class SeleniumTestGenerator {
    /**
     * Generates a Selenium test class from a list of test cases.
     *
     * @param testCases List of test cases
     * @param className Name of the test class
     * @param packageName Package for the test class
     * @param baseUrl Base URL for the tests
     * @return Generated Selenium test class source code
     * @throws TestGenException If code generation fails
     */
    public String generateTestClass(List<TestCase> testCases, String className, String packageName, String baseUrl) 
            throws TestGenException {
        if (testCases == null || testCases.isEmpty()) {
            throw new TestGenException("No test cases provided for Selenium test generation");
        }
        
        StringBuilder sb = new StringBuilder();
        
        // Add package declaration
        if (packageName != null && !packageName.isEmpty()) {
            sb.append("package ").append(packageName).append(";\n\n");
        }
        
        // Add imports
        sb.append("import org.junit.After;\n");
        sb.append("import org.junit.Before;\n");
        sb.append("import org.junit.Test;\n");
        sb.append("import org.openqa.selenium.By;\n");
        sb.append("import org.openqa.selenium.WebDriver;\n");
        sb.append("import org.openqa.selenium.WebElement;\n");
        sb.append("import org.openqa.selenium.chrome.ChromeDriver;\n");
        sb.append("import org.openqa.selenium.chrome.ChromeOptions;\n");
        sb.append("import org.openqa.selenium.support.ui.ExpectedConditions;\n");
        sb.append("import org.openqa.selenium.support.ui.Select;\n");
        sb.append("import org.openqa.selenium.support.ui.WebDriverWait;\n");
        sb.append("import org.openqa.selenium.JavascriptExecutor;\n");
        sb.append("import org.openqa.selenium.interactions.Actions;\n");
        sb.append("import org.openqa.selenium.support.ui.Wait;\n");
        sb.append("import org.openqa.selenium.support.ui.FluentWait;\n");
        sb.append("import org.openqa.selenium.NoSuchElementException;\n");
        sb.append("import java.time.Duration;\n");
        sb.append("import static org.junit.Assert.*;\n\n");
        
        // Class declaration
        sb.append("/**\n");
        sb.append(" * Selenium WebDriver tests generated for ").append(baseUrl).append("\n");
        sb.append(" */\n");
        sb.append("public class ").append(className).append(" {\n\n");
        
        // Add class-level fields
        sb.append("    private WebDriver driver;\n");
        sb.append("    private WebDriverWait wait;\n");
        sb.append("    private String baseUrl = \"").append(baseUrl).append("\";\n\n");
        
        // Setup method
        sb.append("    @Before\n");
        sb.append("    public void setUp() {\n");
        sb.append("        // Set up Chrome WebDriver\n");
        sb.append("        ChromeOptions options = new ChromeOptions();\n");
        sb.append("        options.addArguments(\"--headless\");\n");
        sb.append("        options.addArguments(\"--disable-gpu\");\n");
        sb.append("        options.addArguments(\"--window-size=1920,1080\");\n");
        sb.append("        options.addArguments(\"--start-maximized\");\n");
        sb.append("        options.addArguments(\"--disable-extensions\");\n");
        sb.append("        options.addArguments(\"--no-sandbox\");\n");
        sb.append("        \n");
        sb.append("        driver = new ChromeDriver(options);\n");
        sb.append("        wait = new WebDriverWait(driver, Duration.ofSeconds(10));\n");
        sb.append("        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));\n");
        sb.append("        driver.manage().window().maximize();\n");
        sb.append("    }\n\n");
        
        // Test methods
        for (TestCase testCase : testCases) {
            addTestMethod(sb, testCase);
        }
        
        // Helper methods
        addHelperMethods(sb);
        
        // Teardown method
        sb.append("    @After\n");
        sb.append("    public void tearDown() {\n");
        sb.append("        if (driver != null) {\n");
        sb.append("            driver.quit();\n");
        sb.append("        }\n");
        sb.append("    }\n");
        
        // Close class
        sb.append("}\n");
        
        return sb.toString();
    }
    
    /**
     * Generates a test method for a single test case.
     *
     * @param sb StringBuilder to append to
     * @param testCase The test case to generate code for
     */
    private void addTestMethod(StringBuilder sb, TestCase testCase) {
        String methodName = testCase.toMethodName();
        
        // Add method Javadoc
        sb.append("    /**\n");
        sb.append("     * ").append(testCase.getName()).append("\n");
        if (testCase.getDescription() != null && !testCase.getDescription().isEmpty()) {
            sb.append("     * \n");
            sb.append("     * ").append(testCase.getDescription()).append("\n");
        }
        sb.append("     */\n");
        
        // Add test annotation and method signature
        sb.append("    @Test\n");
        sb.append("    public void ").append(methodName).append("() {\n");
        
        // Add test steps
        sb.append("        // Open the application\n");
        sb.append("        driver.get(baseUrl);\n\n");
        
        for (TestCase.TestStep step : testCase.getSteps()) {
            sb.append("        // Step ").append(step.getStepNumber()).append(": ").append(step.getAction()).append("\n");
            generateStepCode(sb, step);
            sb.append("\n");
        }
        
        sb.append("    }\n\n");
    }
    
    /**
     * Generates code for a single test step.
     *
     * @param sb StringBuilder to append to
     * @param step The test step
     */
    private void generateStepCode(StringBuilder sb, TestCase.TestStep step) {
        String action = step.getAction().toLowerCase();
        Map<String, String> testData = step.getTestData();
        
        if (action.contains("navigate") || action.contains("go to") || action.contains("open")) {
            // Navigation
            String url = testData.getOrDefault("url", "baseUrl");
            if (!url.startsWith("baseUrl") && !url.startsWith("http") && !url.startsWith("\"")) {
                url = "\"" + url + "\"";
            }
            sb.append("        driver.get(").append(url).append(");\n");
            sb.append("        // Verify the page loaded\n");
            sb.append("        waitForPageLoad();\n");
        } else if (action.contains("click") || action.contains("select") || action.contains("choose")) {
            // Click or select action
            String locator = testData.getOrDefault("locator", "");
            String locatorType = testData.getOrDefault("locatorType", "css");
            
            if (!locator.isEmpty()) {
                sb.append("        // Find and interact with the element\n");
                sb.append("        wait.until(ExpectedConditions.elementToBeClickable(");
                appendLocator(sb, locatorType, locator);
                sb.append("));\n");
                
                if (action.contains("select") && testData.containsKey("value")) {
                    // Select from dropdown
                    sb.append("        WebElement selectElement = driver.findElement(");
                    appendLocator(sb, locatorType, locator);
                    sb.append(");\n");
                    sb.append("        Select dropdown = new Select(selectElement);\n");
                    
                    String value = testData.get("value");
                    if (testData.containsKey("selectBy") && testData.get("selectBy").equals("value")) {
                        sb.append("        dropdown.selectByValue(\"").append(value).append("\");\n");
                    } else if (testData.containsKey("selectBy") && testData.get("selectBy").equals("index")) {
                        sb.append("        dropdown.selectByIndex(").append(value).append(");\n");
                    } else {
                        sb.append("        dropdown.selectByVisibleText(\"").append(value).append("\");\n");
                    }
                } else {
                    // Regular click
                    sb.append("        driver.findElement(");
                    appendLocator(sb, locatorType, locator);
                    sb.append(").click();\n");
                }
                
                // Add wait after interaction
                sb.append("        waitForPageLoad();\n");
            }
        } else if (action.contains("type") || action.contains("enter") || action.contains("input") || action.contains("fill")) {
            // Input action
            String locator = testData.getOrDefault("locator", "");
            String locatorType = testData.getOrDefault("locatorType", "css");
            String value = testData.getOrDefault("value", "");
            
            if (!locator.isEmpty()) {
                sb.append("        // Find and input data into the element\n");
                sb.append("        wait.until(ExpectedConditions.visibilityOfElementLocated(");
                appendLocator(sb, locatorType, locator);
                sb.append("));\n");
                
                // Clear first if specified
                if (testData.getOrDefault("clear", "true").equalsIgnoreCase("true")) {
                    sb.append("        driver.findElement(");
                    appendLocator(sb, locatorType, locator);
                    sb.append(").clear();\n");
                }
                
                // Input the value
                sb.append("        driver.findElement(");
                appendLocator(sb, locatorType, locator);
                sb.append(").sendKeys(\"").append(value).append("\");\n");
            }
        } else if (action.contains("verify") || action.contains("assert") || action.contains("check") || action.contains("validate")) {
            // Verification/assertion
            String locator = testData.getOrDefault("locator", "");
            String locatorType = testData.getOrDefault("locatorType", "css");
            
            if (action.contains("title")) {
                // Title verification
                String expectedTitle = testData.getOrDefault("expected", "");
                sb.append("        // Verify page title\n");
                sb.append("        String actualTitle = driver.getTitle();\n");
                sb.append("        assertTrue(\"Expected title to contain '").append(expectedTitle)
                  .append("' but was '\" + actualTitle + \"'\", actualTitle.contains(\"").append(expectedTitle).append("\"));\n");
            } else if (action.contains("url")) {
                // URL verification
                String expectedUrl = testData.getOrDefault("expected", "");
                sb.append("        // Verify page URL\n");
                sb.append("        String actualUrl = driver.getCurrentUrl();\n");
                sb.append("        assertTrue(\"Expected URL to contain '").append(expectedUrl)
                  .append("' but was '\" + actualUrl + \"'\", actualUrl.contains(\"").append(expectedUrl).append("\"));\n");
            } else if (!locator.isEmpty()) {
                // Element verification
                sb.append("        // Verify element\n");
                sb.append("        wait.until(ExpectedConditions.visibilityOfElementLocated(");
                appendLocator(sb, locatorType, locator);
                sb.append("));\n");
                
                if (testData.containsKey("expected")) {
                    // Verify text content
                    String expected = testData.get("expected");
                    sb.append("        String actualText = driver.findElement(");
                    appendLocator(sb, locatorType, locator);
                    sb.append(").getText();\n");
                    sb.append("        assertTrue(\"Expected text to contain '").append(expected)
                      .append("' but was '\" + actualText + \"'\", actualText.contains(\"").append(expected).append("\"));\n");
                } else if (testData.containsKey("attribute") && testData.containsKey("value")) {
                    // Verify attribute value
                    String attribute = testData.get("attribute");
                    String value = testData.get("value");
                    sb.append("        String attrValue = driver.findElement(");
                    appendLocator(sb, locatorType, locator);
                    sb.append(").getAttribute(\"").append(attribute).append("\");\n");
                    sb.append("        assertEquals(\"").append(value).append("\", attrValue);\n");
                } else {
                    // Just verify element exists
                    sb.append("        assertTrue(\"Element should be displayed\", driver.findElement(");
                    appendLocator(sb, locatorType, locator);
                    sb.append(").isDisplayed());\n");
                }
            }
        } else if (action.contains("wait")) {
            // Wait action
            if (testData.containsKey("seconds")) {
                sb.append("        // Wait for specified time\n");
                sb.append("        try {\n");
                sb.append("            Thread.sleep(").append(Integer.parseInt(testData.get("seconds")) * 1000).append(");\n");
                sb.append("        } catch (InterruptedException e) {\n");
                sb.append("            Thread.currentThread().interrupt();\n");
                sb.append("        }\n");
            } else if (testData.containsKey("locator")) {
                // Wait for element
                String locator = testData.get("locator");
                String locatorType = testData.getOrDefault("locatorType", "css");
                
                sb.append("        // Wait for element\n");
                sb.append("        wait.until(ExpectedConditions.visibilityOfElementLocated(");
                appendLocator(sb, locatorType, locator);
                sb.append("));\n");
            } else {
                // Generic wait for page to load
                sb.append("        // Wait for page to load\n");
                sb.append("        waitForPageLoad();\n");
            }
        } else if (action.contains("submit") || action.contains("form")) {
            // Form submission
            String locator = testData.getOrDefault("locator", "");
            String locatorType = testData.getOrDefault("locatorType", "css");
            
            if (!locator.isEmpty()) {
                sb.append("        // Submit form\n");
                sb.append("        driver.findElement(");
                appendLocator(sb, locatorType, locator);
                sb.append(").submit();\n");
                sb.append("        waitForPageLoad();\n");
            }
        } else if (action.contains("scroll")) {
            // Scrolling action
            if (testData.containsKey("locator")) {
                // Scroll to element
                String locator = testData.get("locator");
                String locatorType = testData.getOrDefault("locatorType", "css");
                
                sb.append("        // Scroll to element\n");
                sb.append("        WebElement element = driver.findElement(");
                appendLocator(sb, locatorType, locator);
                sb.append(");\n");
                sb.append("        ((JavascriptExecutor) driver).executeScript(\"arguments[0].scrollIntoView(true);\", element);\n");
            } else {
                // Scroll by pixel amount
                String x = testData.getOrDefault("x", "0");
                String y = testData.getOrDefault("y", "500");
                
                sb.append("        // Scroll by pixel amount\n");
                sb.append("        ((JavascriptExecutor) driver).executeScript(\"window.scrollBy(")
                  .append(x).append(", ").append(y).append(")\");\n");
            }
        } else if (action.contains("mouse over") || action.contains("hover")) {
            // Mouse hover action
            String locator = testData.getOrDefault("locator", "");
            String locatorType = testData.getOrDefault("locatorType", "css");
            
            if (!locator.isEmpty()) {
                sb.append("        // Hover over element\n");
                sb.append("        WebElement element = driver.findElement(");
                appendLocator(sb, locatorType, locator);
                sb.append(");\n");
                sb.append("        Actions actions = new Actions(driver);\n");
                sb.append("        actions.moveToElement(element).perform();\n");
            }
        } else if (action.contains("checkbox") || action.contains("radio")) {
            // Checkbox/radio button action
            String locator = testData.getOrDefault("locator", "");
            String locatorType = testData.getOrDefault("locatorType", "css");
            String check = testData.getOrDefault("check", "true");
            
            if (!locator.isEmpty()) {
                sb.append("        // Interact with checkbox/radio button\n");
                sb.append("        WebElement element = driver.findElement(");
                appendLocator(sb, locatorType, locator);
                sb.append(");\n");
                sb.append("        boolean isSelected = element.isSelected();\n");
                
                if (check.equalsIgnoreCase("true")) {
                    sb.append("        if (!isSelected) {\n");
                    sb.append("            element.click();\n");
                    sb.append("        }\n");
                } else {
                    sb.append("        if (isSelected) {\n");
                    sb.append("            element.click();\n");
                    sb.append("        }\n");
                }
            }
        } else {
            // Default: just add a comment for unrecognized action
            sb.append("        // TODO: Implement action - ").append(step.getAction()).append("\n");
        }
    }
    
    /**
     * Appends a By locator to the StringBuilder.
     *
     * @param sb StringBuilder to append to
     * @param locatorType Type of locator (id, css, xpath, etc.)
     * @param locator Locator string
     */
    private void appendLocator(StringBuilder sb, String locatorType, String locator) {
        switch (locatorType.toLowerCase()) {
            case "id":
                sb.append("By.id(\"").append(locator).append("\")");
                break;
            case "name":
                sb.append("By.name(\"").append(locator).append("\")");
                break;
            case "css":
            case "cssselector":
                sb.append("By.cssSelector(\"").append(escapeStringForJava(locator)).append("\")");
                break;
            case "xpath":
                sb.append("By.xpath(\"").append(escapeStringForJava(locator)).append("\")");
                break;
            case "class":
            case "classname":
                sb.append("By.className(\"").append(locator).append("\")");
                break;
            case "linktext":
                sb.append("By.linkText(\"").append(locator).append("\")");
                break;
            case "partiallinktext":
                sb.append("By.partialLinkText(\"").append(locator).append("\")");
                break;
            case "tagname":
                sb.append("By.tagName(\"").append(locator).append("\")");
                break;
            default:
                sb.append("By.cssSelector(\"").append(escapeStringForJava(locator)).append("\")");
                break;
        }
    }
    
    /**
     * Adds helper methods to the test class.
     *
     * @param sb StringBuilder to append to
     */
    private void addHelperMethods(StringBuilder sb) {
        // Add waitForPageLoad method
        sb.append("    /**\n");
        sb.append("     * Waits for page to finish loading.\n");
        sb.append("     */\n");
        sb.append("    private void waitForPageLoad() {\n");
        sb.append("        Wait<WebDriver> wait = new FluentWait<>(driver)\n");
        sb.append("                .withTimeout(Duration.ofSeconds(30))\n");
        sb.append("                .pollingEvery(Duration.ofMillis(500))\n");
        sb.append("                .ignoring(NoSuchElementException.class);\n");
        sb.append("        \n");
        sb.append("        wait.until(driver -> {\n");
        sb.append("            return ((JavascriptExecutor) driver).executeScript(\"return document.readyState\").equals(\"complete\");\n");
        sb.append("        });\n");
        sb.append("    }\n\n");
        
        // Add isElementPresent method
        sb.append("    /**\n");
        sb.append("     * Checks if an element is present on the page.\n");
        sb.append("     * \n");
        sb.append("     * @param by The locator to check\n");
        sb.append("     * @return true if the element is present, false otherwise\n");
        sb.append("     */\n");
        sb.append("    private boolean isElementPresent(By by) {\n");
        sb.append("        try {\n");
        sb.append("            driver.findElement(by);\n");
        sb.append("            return true;\n");
        sb.append("        } catch (NoSuchElementException e) {\n");
        sb.append("            return false;\n");
        sb.append("        }\n");
        sb.append("    }\n\n");
    }
    
    /**
     * Escapes a string for use in Java string literals.
     *
     * @param input String to escape
     * @return Escaped string
     */
    private String escapeStringForJava(String input) {
        return input.replace("\\", "\\\\").replace("\"", "\\\"");
    }
    
    /**
     * Generates a Selenium test template from a list of WebElement objects.
     *
     * @param elements List of WebElement objects
     * @param pageUrl URL of the page
     * @param className Name of the test class
     * @param packageName Package for the test class
     * @return Generated Selenium test class source code
     * @throws TestGenException If code generation fails
     */
    public String generateTestClassFromElements(List<WebElement> elements, String pageUrl, 
                                              String className, String packageName) throws TestGenException {
        if (elements == null || elements.isEmpty()) {
            throw new TestGenException("No web elements provided for Selenium test generation");
        }
        
        StringBuilder sb = new StringBuilder();
        
        // Add package declaration
        if (packageName != null && !packageName.isEmpty()) {
            sb.append("package ").append(packageName).append(";\n\n");
        }
        
        // Add imports
        sb.append("import org.junit.After;\n");
        sb.append("import org.junit.Before;\n");
        sb.append("import org.junit.Test;\n");
        sb.append("import org.openqa.selenium.By;\n");
        sb.append("import org.openqa.selenium.WebDriver;\n");
        sb.append("import org.openqa.selenium.WebElement;\n");
        sb.append("import org.openqa.selenium.chrome.ChromeDriver;\n");
        sb.append("import org.openqa.selenium.chrome.ChromeOptions;\n");
        sb.append("import org.openqa.selenium.support.ui.ExpectedConditions;\n");
        sb.append("import org.openqa.selenium.support.ui.Select;\n");
        sb.append("import org.openqa.selenium.support.ui.WebDriverWait;\n");
        sb.append("import org.openqa.selenium.JavascriptExecutor;\n");
        sb.append("import static org.junit.Assert.*;\n");
        sb.append("import java.time.Duration;\n\n");
        
        // Class declaration
        sb.append("/**\n");
        sb.append(" * Selenium WebDriver tests generated for ").append(pageUrl).append("\n");
        sb.append(" */\n");
        sb.append("public class ").append(className).append(" {\n\n");
        
        // Add class-level fields
        sb.append("    private WebDriver driver;\n");
        sb.append("    private WebDriverWait wait;\n");
        sb.append("    private String baseUrl = \"").append(pageUrl).append("\";\n\n");
        
        // Setup method
        sb.append("    @Before\n");
        sb.append("    public void setUp() {\n");
        sb.append("        // Set up Chrome WebDriver\n");
        sb.append("        ChromeOptions options = new ChromeOptions();\n");
        sb.append("        options.addArguments(\"--headless\");\n");
        sb.append("        options.addArguments(\"--disable-gpu\");\n");
        sb.append("        options.addArguments(\"--window-size=1920,1080\");\n");
        
        sb.append("        driver = new ChromeDriver(options);\n");
        sb.append("        wait = new WebDriverWait(driver, Duration.ofSeconds(10));\n");
        sb.append("        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));\n");
        sb.append("        driver.manage().window().maximize();\n");
        sb.append("    }\n\n");
        
        // Generate test methods for each form
        List<WebElement> forms = elements.stream()
                .filter(e -> "form".equals(e.getType()))
                .collect(Collectors.toList());
        
        if (!forms.isEmpty()) {
            for (WebElement form : forms) {
                generateFormTest(sb, form, elements);
            }
        }
        
        // Generate tests for other interactive elements not in forms
        List<WebElement> interactiveElements = elements.stream()
                .filter(e -> !e.getType().equals("form") && e.getParentForm() == null)
                .collect(Collectors.toList());
        
        if (!interactiveElements.isEmpty()) {
            generateInteractiveElementsTest(sb, interactiveElements);
        }
        
        // Generate a navigation test for links
        List<WebElement> links = elements.stream()
                .filter(e -> "link".equals(e.getType()))
                .collect(Collectors.toList());
        
        if (!links.isEmpty()) {
            generateNavigationTest(sb, links);
        }
        
        // Generate a comprehensive test
        generateComprehensiveTest(sb, elements);
        
        // Helper methods
        addHelperMethods(sb);
        
        // Teardown method
        sb.append("    @After\n");
        sb.append("    public void tearDown() {\n");
        sb.append("        if (driver != null) {\n");
        sb.append("            driver.quit();\n");
        sb.append("        }\n");
        sb.append("    }\n");
        
        // Close class
        sb.append("}\n");
        
        return sb.toString();
    }
    
    /**
     * Generates a test method for a form.
     *
     * @param sb StringBuilder to append to
     * @param form The form element
     * @param allElements All elements on the page
     */
    private void generateFormTest(StringBuilder sb, WebElement form, List<WebElement> allElements) {
        String formId = form.getId();
        String formName = form.getName();
        String formIdentifier = !formId.isEmpty() ? formId : (!formName.isEmpty() ? formName : "unnamed_form");
        
        String methodName = "test" + capitalizeFirstLetter(form.getJavaIdentifierName()) + "Submission";
        
        // Get form elements
        List<WebElement> formElements = allElements.stream()
                .filter(e -> form.getLocatorValue().equals(e.getParentForm()))
                .collect(Collectors.toList());
        
        sb.append("    /**\n");
        sb.append("     * Tests the form: ").append(formIdentifier).append("\n");
        sb.append("     */\n");
        sb.append("    @Test\n");
        sb.append("    public void ").append(methodName).append("() {\n");
        sb.append("        // Navigate to the page\n");
        sb.append("        driver.get(baseUrl);\n");
        sb.append("        waitForPageLoad();\n\n");
        
        // Fill in form elements
        sb.append("        // Fill in the form\n");
        for (WebElement element : formElements) {
            if (element.getType().startsWith("input-") || element.getType().equals("textarea")) {
                if (element.getType().equals("input-checkbox") || element.getType().equals("input-radio")) {
                    sb.append("        // Interact with ").append(element.getType()).append(": ").append(element.getDescriptiveName()).append("\n");
                    sb.append("        WebElement ").append(element.getJavaIdentifierName()).append(" = driver.findElement(");
                    appendLocator(sb, "css", element.getLocatorValue());
                    sb.append(");\n");
                    sb.append("        if (!").append(element.getJavaIdentifierName()).append(".isSelected()) {\n");
                    sb.append("            ").append(element.getJavaIdentifierName()).append(".click();\n");
                    sb.append("        }\n\n");
                } else if (element.getType().equals("select")) {
                    sb.append("        // Select an option from dropdown: ").append(element.getDescriptiveName()).append("\n");
                    sb.append("        Select ").append(element.getJavaIdentifierName()).append(" = new Select(driver.findElement(");
                    appendLocator(sb, "css", element.getLocatorValue());
                    sb.append("));\n");
                    
                    if (!element.getOptions().isEmpty()) {
                        sb.append("        ").append(element.getJavaIdentifierName()).append(".selectByVisibleText(\"")
                          .append(element.getOptions().get(0)).append("\");\n\n");
                    } else {
                        sb.append("        // Select the first option\n");
                        sb.append("        ").append(element.getJavaIdentifierName()).append(".selectByIndex(0);\n\n");
                    }
                } else {
                    sb.append("        // Fill in ").append(element.getType()).append(": ").append(element.getDescriptiveName()).append("\n");
                    sb.append("        driver.findElement(");
                    appendLocator(sb, "css", element.getLocatorValue());
                    sb.append(").clear();\n");
                    sb.append("        driver.findElement(");
                    appendLocator(sb, "css", element.getLocatorValue());
                    sb.append(").sendKeys(\"").append(element.getSuggestedTestData()).append("\");\n\n");
                }
            }
        }
        
        // Submit the form
        sb.append("        // Submit the form\n");
        
        // Find a submit button
        WebElement submitButton = formElements.stream()
                .filter(e -> e.getType().equals("input-submit") || e.getType().equals("button"))
                .findFirst().orElse(null);
        
        if (submitButton != null) {
            sb.append("        driver.findElement(");
            appendLocator(sb, "css", submitButton.getLocatorValue());
            sb.append(").click();\n");
        } else {
            sb.append("        driver.findElement(");
            appendLocator(sb, "css", form.getLocatorValue());
            sb.append(").submit();\n");
        }
        
        sb.append("        waitForPageLoad();\n\n");
        
        // Add assertions based on form action if available
        String formAction = form.getAction();
        if (formAction != null && !formAction.isEmpty()) {
            if (formAction.startsWith("http")) {
                sb.append("        // Verify navigation to the form action URL\n");
                sb.append("        String currentUrl = driver.getCurrentUrl();\n");
                sb.append("        assertTrue(\"Expected URL to contain form action: " + formAction + "\", currentUrl.contains(\"")
                  .append(formAction.replace("\"", "\\\"")).append("\"));\n\n");
            } else {
                sb.append("        // Verify navigation to the form action path\n");
                sb.append("        String currentUrl = driver.getCurrentUrl();\n");
                sb.append("        assertTrue(\"Expected URL to end with form action: " + formAction + "\", currentUrl.endsWith(\"")
                  .append(formAction.replace("\"", "\\\"")).append("\"));\n\n");
            }
        } else {
            // Generic verification
            sb.append("        // Verify the form was submitted (add specific assertions based on expected behavior)\n");
            sb.append("        // For example, check for success message or redirect\n");
            sb.append("        // assertTrue(\"Success message should be displayed\", driver.findElement(By.id(\"success-message\")).isDisplayed());\n");
        }
        
        sb.append("    }\n\n");
    }
    
    /**
     * Generates a test method for interactive elements not in forms.
     *
     * @param sb StringBuilder to append to
     * @param elements List of interactive elements
     */
    private void generateInteractiveElementsTest(StringBuilder sb, List<WebElement> elements) {
        sb.append("    /**\n");
        sb.append("     * Tests interactive elements on the page.\n");
        sb.append("     */\n");
        sb.append("    @Test\n");
        sb.append("    public void testInteractiveElements() {\n");
        sb.append("        // Navigate to the page\n");
        sb.append("        driver.get(baseUrl);\n");
        sb.append("        waitForPageLoad();\n\n");
        
        for (WebElement element : elements) {
            String elementType = element.getType();
            
            // Skip links as they're covered in the navigation test
            if (elementType.equals("link")) {
                continue;
            }
            
            sb.append("        // Interact with ").append(elementType).append(": ").append(element.getDescriptiveName()).append("\n");
            
            if (elementType.equals("button") || elementType.startsWith("custom-")) {
                sb.append("        // Check if the element is displayed\n");
                sb.append("        assertTrue(\"Element should be displayed\", driver.findElement(");
                appendLocator(sb, "css", element.getLocatorValue());
                sb.append(").isDisplayed());\n\n");
                
                sb.append("        // Click the element\n");
                sb.append("        driver.findElement(");
                appendLocator(sb, "css", element.getLocatorValue());
                sb.append(").click();\n");
                sb.append("        waitForPageLoad();\n\n");
                
                // Add back navigation if needed
                sb.append("        // Navigate back to the original page if needed\n");
                sb.append("        if (!driver.getCurrentUrl().equals(baseUrl)) {\n");
                sb.append("            driver.get(baseUrl);\n");
                sb.append("            waitForPageLoad();\n");
                sb.append("        }\n\n");
            } else if (elementType.equals("input-text") || elementType.equals("input-email") || 
                     elementType.equals("input-password") || elementType.equals("textarea")) {
                sb.append("        // Enter text in the input field\n");
                sb.append("        driver.findElement(");
                appendLocator(sb, "css", element.getLocatorValue());
                sb.append(").clear();\n");
                sb.append("        driver.findElement(");
                appendLocator(sb, "css", element.getLocatorValue());
                sb.append(").sendKeys(\"").append(element.getSuggestedTestData()).append("\");\n\n");
            } else if (elementType.equals("input-checkbox") || elementType.equals("input-radio")) {
                sb.append("        // Toggle checkbox/radio button\n");
                sb.append("        WebElement checkElement = driver.findElement(");
                appendLocator(sb, "css", element.getLocatorValue());
                sb.append(");\n");
                sb.append("        if (!checkElement.isSelected()) {\n");
                sb.append("            checkElement.click();\n");
                sb.append("        }\n\n");
            } else if (elementType.equals("select")) {
                sb.append("        // Select an option from dropdown\n");
                sb.append("        Select dropdown = new Select(driver.findElement(");
                appendLocator(sb, "css", element.getLocatorValue());
                sb.append("));\n");
                
                if (!element.getOptions().isEmpty()) {
                    sb.append("        dropdown.selectByVisibleText(\"")
                      .append(element.getOptions().get(0)).append("\");\n\n");
                } else {
                    sb.append("        // Select the first option\n");
                    sb.append("        dropdown.selectByIndex(0);\n\n");
                }
            } else {
                sb.append("        // Verify element exists\n");
                sb.append("        assertTrue(\"Element should exist\", isElementPresent(");
                appendLocator(sb, "css", element.getLocatorValue());
                sb.append("));\n\n");
            }
        }
        
        sb.append("    }\n\n");
    }
    
    /**
     * Generates a test method for navigation links.
     *
     * @param sb StringBuilder to append to
     * @param links List of link elements
     */
    private void generateNavigationTest(StringBuilder sb, List<WebElement> links) {
        sb.append("    /**\n");
        sb.append("     * Tests navigation links on the page.\n");
        sb.append("     */\n");
        sb.append("    @Test\n");
        sb.append("    public void testNavigation() {\n");
        sb.append("        // Navigate to the page\n");
        sb.append("        driver.get(baseUrl);\n");
        sb.append("        waitForPageLoad();\n\n");
        
        // Limit to testing only the first 5 links to keep the test manageable
        int linkCount = Math.min(links.size(), 5);
        for (int i = 0; i < linkCount; i++) {
            WebElement link = links.get(i);
            String linkText = link.getText() != null && !link.getText().isEmpty() 
                ? link.getText() : "link " + link.getDescriptiveName();
            
            sb.append("        // Click on ").append(linkText).append("\n");
            sb.append("        WebElement link").append(i).append(" = driver.findElement(");
            appendLocator(sb, "css", link.getLocatorValue());
            sb.append(");\n");
            sb.append("        String href").append(i).append(" = link").append(i).append(".getAttribute(\"href\");\n");
            sb.append("        link").append(i).append(".click();\n");
            sb.append("        waitForPageLoad();\n\n");
            
            sb.append("        // Verify the navigation\n");
            sb.append("        String currentUrl").append(i).append(" = driver.getCurrentUrl();\n");
            sb.append("        assertTrue(\"URL should match or contain the href value\", currentUrl").append(i)
              .append(".contains(href").append(i).append(") || href").append(i).append(".contains(currentUrl").append(i).append("));\n\n");
            
            sb.append("        // Navigate back to the original page\n");
            sb.append("        driver.get(baseUrl);\n");
            sb.append("        waitForPageLoad();\n\n");
        }
        
        sb.append("    }\n\n");
    }
    
    /**
     * Generates a comprehensive test that tests multiple elements in sequence.
     *
     * @param sb StringBuilder to append to
     * @param elements All elements on the page
     */
    private void generateComprehensiveTest(StringBuilder sb, List<WebElement> elements) {
        sb.append("    /**\n");
        sb.append("     * A comprehensive test that interacts with multiple elements in sequence.\n");
        sb.append("     */\n");
        sb.append("    @Test\n");
        sb.append("    public void testComprehensive() {\n");
        sb.append("        // Navigate to the page\n");
        sb.append("        driver.get(baseUrl);\n");
        sb.append("        waitForPageLoad();\n\n");
        
        sb.append("        // Verify the page title\n");
        sb.append("        String pageTitle = driver.getTitle();\n");
        sb.append("        assertNotNull(\"Page title should not be null\", pageTitle);\n\n");
        
        // Select a few representative elements of different types
        List<WebElement> inputElements = elements.stream()
                .filter(e -> e.getType().startsWith("input-") || e.getType().equals("textarea"))
                .limit(3)
                .collect(Collectors.toList());
        
        List<WebElement> buttons = elements.stream()
                .filter(e -> e.getType().equals("button") || e.getType().equals("input-submit"))
                .limit(2)
                .collect(Collectors.toList());
        
        List<WebElement> selectElements = elements.stream()
                .filter(e -> e.getType().equals("select"))
                .limit(2)
                .collect(Collectors.toList());
        
        // Interact with input elements
        if (!inputElements.isEmpty()) {
            sb.append("        // Interact with input elements\n");
            for (WebElement input : inputElements) {
                if (input.getType().equals("input-checkbox") || input.getType().equals("input-radio")) {
                    sb.append("        // Toggle ").append(input.getType()).append("\n");
                    sb.append("        WebElement checkElement = driver.findElement(");
                    appendLocator(sb, "css", input.getLocatorValue());
                    sb.append(");\n");
                    sb.append("        if (!checkElement.isSelected()) {\n");
                    sb.append("            checkElement.click();\n");
                    sb.append("        }\n\n");
                } else {
                    sb.append("        // Fill in ").append(input.getType()).append("\n");
                    sb.append("        driver.findElement(");
                    appendLocator(sb, "css", input.getLocatorValue());
                    sb.append(").clear();\n");
                    sb.append("        driver.findElement(");
                    appendLocator(sb, "css", input.getLocatorValue());
                    sb.append(").sendKeys(\"").append(input.getSuggestedTestData()).append("\");\n\n");
                }
            }
        }
        
        // Interact with select elements
        if (!selectElements.isEmpty()) {
            sb.append("        // Interact with select elements\n");
            for (WebElement select : selectElements) {
                sb.append("        // Select from dropdown\n");
                sb.append("        Select dropdown = new Select(driver.findElement(");
                appendLocator(sb, "css", select.getLocatorValue());
                sb.append("));\n");
                
                if (!select.getOptions().isEmpty()) {
                    sb.append("        dropdown.selectByVisibleText(\"")
                      .append(select.getOptions().get(0)).append("\");\n\n");
                } else {
                    sb.append("        // Select the first option\n");
                    sb.append("        dropdown.selectByIndex(0);\n\n");
                }
            }
        }
        
        // Interact with buttons if available
        if (!buttons.isEmpty()) {
            sb.append("        // Interact with buttons\n");
            WebElement button = buttons.get(0);
            sb.append("        driver.findElement(");
            appendLocator(sb, "css", button.getLocatorValue());
            sb.append(").click();\n");
            sb.append("        waitForPageLoad();\n\n");
            
            // Add back navigation if needed
            sb.append("        // Navigate back to the original page if needed\n");
            sb.append("        if (!driver.getCurrentUrl().equals(baseUrl)) {\n");
            sb.append("            driver.get(baseUrl);\n");
            sb.append("            waitForPageLoad();\n");
            sb.append("        }\n\n");
        }
        
        sb.append("        // Add specific assertions for expected application behavior\n");
        sb.append("    }\n\n");
    }
    
    /**
     * Capitalizes the first letter of a string.
     *
     * @param input Input string
     * @return String with first letter capitalized
     */
    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
