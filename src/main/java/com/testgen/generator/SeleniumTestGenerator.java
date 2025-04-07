package com.testgen.generator;

import com.testgen.model.WebElement;
import com.testgen.model.TestCase;
import com.testgen.model.TestCase.TestStep;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Generates Selenium tests from web elements.
 */
public class SeleniumTestGenerator {
    /**
     * Generates Selenium tests for a list of web elements.
     *
     * @param elements    The web elements
     * @param outputDir   The directory to save the generated tests
     * @param packageName The package name for the generated tests
     * @throws IOException If an error occurs during generation
     */
    public void generate(List<WebElement> elements, String outputDir, String packageName) throws IOException {
        // Create default test cases based on elements
        List<TestCase> defaultTestCases = createDefaultTestCases(elements);
        generate(elements, defaultTestCases, outputDir, packageName);
    }
    
    /**
     * Generates Selenium tests for a list of web elements and test cases.
     *
     * @param elements    The web elements
     * @param testCases   The test cases to generate
     * @param outputDir   The directory to save the generated tests
     * @param packageName The package name for the generated tests
     * @throws IOException If an error occurs during generation
     */
    public void generate(List<WebElement> elements, List<TestCase> testCases, String outputDir, String packageName) throws IOException {
        // Create output directory if it doesn't exist
        File outputDirFile = new File(outputDir);
        if (!outputDirFile.exists()) {
            outputDirFile.mkdirs();
        }
        
        // Create subdirectory for Selenium tests
        File seleniumDir = new File(outputDirFile, "selenium");
        if (!seleniumDir.exists()) {
            seleniumDir.mkdirs();
        }
        
        // Generate base test class
        generateBaseTestClass(seleniumDir, packageName);
        
        // Generate a test class for each test case
        for (TestCase testCase : testCases) {
            if (testCase.getType() == TestCase.TestType.WEB_UI) {
                generateTestClass(seleniumDir, packageName, testCase, elements);
            }
        }
    }
    
    /**
     * Generates the base test class.
     *
     * @param outputDir   The directory to save the generated class
     * @param packageName The package name for the generated class
     * @throws IOException If an error occurs during generation
     */
    private void generateBaseTestClass(File outputDir, String packageName) throws IOException {
        String className = "BaseSeleniumTest";
        File outputFile = new File(outputDir, className + ".java");
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            // Package declaration
            writer.println("package " + packageName + ";");
            writer.println();
            
            // Import statements
            writer.println("import org.junit.After;");
            writer.println("import org.junit.Before;");
            writer.println("import org.openqa.selenium.WebDriver;");
            writer.println("import org.openqa.selenium.chrome.ChromeDriver;");
            writer.println("import org.openqa.selenium.chrome.ChromeOptions;");
            writer.println("import org.openqa.selenium.support.ui.WebDriverWait;");
            writer.println("import java.time.Duration;");
            writer.println();
            
            // Class declaration
            writer.println("/**");
            writer.println(" * Base class for generated Selenium tests.");
            writer.println(" */");
            writer.println("public abstract class " + className + " {");
            writer.println("    protected WebDriver driver;");
            writer.println("    protected WebDriverWait wait;");
            writer.println();
            
            // Before method
            writer.println("    @Before");
            writer.println("    public void setUp() {");
            writer.println("        // Set up Chrome options");
            writer.println("        ChromeOptions options = new ChromeOptions();");
            writer.println("        options.addArguments(\"--headless\");");
            writer.println("        options.addArguments(\"--disable-gpu\");");
            writer.println("        options.addArguments(\"--no-sandbox\");");
            writer.println("        options.addArguments(\"--disable-dev-shm-usage\");");
            writer.println();
            writer.println("        // Set up the WebDriver");
            writer.println("        driver = new ChromeDriver(options);");
            writer.println("        driver.manage().window().maximize();");
            writer.println("        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));");
            writer.println("        wait = new WebDriverWait(driver, Duration.ofSeconds(10));");
            writer.println("    }");
            writer.println();
            
            // After method
            writer.println("    @After");
            writer.println("    public void tearDown() {");
            writer.println("        // Close the browser");
            writer.println("        if (driver != null) {");
            writer.println("            driver.quit();");
            writer.println("        }");
            writer.println("    }");
            writer.println("}");
        }
    }
    
    /**
     * Generates a test class for a test case.
     *
     * @param outputDir   The directory to save the generated class
     * @param packageName The package name for the generated class
     * @param testCase    The test case
     * @param elements    The web elements
     * @throws IOException If an error occurs during generation
     */
    private void generateTestClass(File outputDir, String packageName, TestCase testCase, List<WebElement> elements) throws IOException {
        String className = formatClassName(testCase.getName()) + "Test";
        File outputFile = new File(outputDir, className + ".java");
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            // Package declaration
            writer.println("package " + packageName + ";");
            writer.println();
            
            // Import statements
            writer.println("import org.junit.Test;");
            writer.println("import org.openqa.selenium.By;");
            writer.println("import org.openqa.selenium.WebElement;");
            writer.println("import org.openqa.selenium.support.ui.ExpectedConditions;");
            writer.println("import static org.junit.Assert.*;");
            writer.println();
            
            // Class declaration
            writer.println("/**");
            writer.println(" * " + testCase.getDescription());
            writer.println(" */");
            writer.println("public class " + className + " extends BaseSeleniumTest {");
            
            // Test method
            writer.println("    @Test");
            writer.println("    public void " + formatMethodName(testCase.getName()) + "() {");
            
            // Generate test steps
            for (TestStep step : testCase.getSteps()) {
                writer.println("        // " + step.getDescription());
                writer.println("        // Expected: " + step.getExpectedResult());
                
                // Generate step implementation based on the description
                String description = step.getDescription().toLowerCase();
                
                if (description.contains("navigate") || description.contains("open")) {
                    writer.println("        driver.get(\"https://example.com\");");
                } else if (description.contains("enter") || description.contains("input") || description.contains("type")) {
                    // Find a relevant input element from the elements list
                    WebElement inputElement = findRelevantElement(elements, description, "input");
                    if (inputElement != null) {
                        String locator = getLocator(inputElement);
                        String value = extractInputValue(description);
                        writer.println("        WebElement input = driver.findElement(" + locator + ");");
                        writer.println("        input.clear();");
                        writer.println("        input.sendKeys(\"" + value + "\");");
                    }
                } else if (description.contains("click") || description.contains("select") || description.contains("choose")) {
                    // Find a relevant clickable element from the elements list
                    WebElement clickableElement = findRelevantElement(elements, description, "button", "a", "input");
                    if (clickableElement != null) {
                        String locator = getLocator(clickableElement);
                        writer.println("        WebElement element = driver.findElement(" + locator + ");");
                        writer.println("        element.click();");
                    }
                } else if (description.contains("verify") || description.contains("check") || description.contains("validate")) {
                    writer.println("        // Add verification logic here");
                    writer.println("        // Example: assertEquals(\"Expected text\", driver.findElement(By.id(\"result\")).getText());");
                } else if (description.contains("wait")) {
                    writer.println("        try {");
                    writer.println("            Thread.sleep(1000); // Wait for 1 second");
                    writer.println("        } catch (InterruptedException e) {");
                    writer.println("            e.printStackTrace();");
                    writer.println("        }");
                }
                
                writer.println();
            }
            
            writer.println("    }");
            writer.println("}");
        }
    }
    
    /**
     * Finds a relevant element based on a description.
     *
     * @param elements    The web elements
     * @param description The description
     * @param types       The element types to consider
     * @return The relevant element, or null if not found
     */
    private WebElement findRelevantElement(List<WebElement> elements, String description, String... types) {
        for (WebElement element : elements) {
            for (String type : types) {
                if (element.getTagName().equalsIgnoreCase(type)) {
                    if ((element.getName() != null && description.contains(element.getName().toLowerCase())) ||
                        (element.getId() != null && description.contains(element.getId().toLowerCase())) ||
                        (element.getValue() != null && description.contains(element.getValue().toLowerCase())) ||
                        (element.getText() != null && description.contains(element.getText().toLowerCase()))) {
                        return element;
                    }
                }
            }
        }
        
        // Return the first element of the specified type
        for (WebElement element : elements) {
            for (String type : types) {
                if (element.getTagName().equalsIgnoreCase(type)) {
                    return element;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Extracts an input value from a description.
     *
     * @param description The description
     * @return The input value
     */
    private String extractInputValue(String description) {
        if (description.contains("username") || description.contains("user name") || description.contains("login")) {
            return "testuser";
        } else if (description.contains("password") || description.contains("pwd")) {
            return "password123";
        } else if (description.contains("email")) {
            return "test@example.com";
        } else if (description.contains("name")) {
            return "Test User";
        } else if (description.contains("address")) {
            return "123 Test Street";
        } else if (description.contains("phone")) {
            return "123-456-7890";
        } else if (description.contains("date")) {
            return "01/01/2023";
        } else if (description.contains("number")) {
            return "123";
        } else {
            return "test";
        }
    }
    
    /**
     * Creates default test cases based on web elements.
     *
     * @param elements The web elements
     * @return The default test cases
     */
    private List<TestCase> createDefaultTestCases(List<WebElement> elements) {
        List<TestCase> testCases = new java.util.ArrayList<>();
        
        // Create a basic test case
        TestCase testCase = new TestCase(
            "Basic Page Test",
            "Basic test for the page",
            TestCase.TestType.WEB_UI
        );
        
        testCase.addStep(new TestStep("Navigate to the page", "Page should load successfully"));
        
        // Add steps for form elements
        boolean hasForm = false;
        for (WebElement element : elements) {
            if (element.getTagName().equalsIgnoreCase("input") ||
                element.getTagName().equalsIgnoreCase("textarea") ||
                element.getTagName().equalsIgnoreCase("select")) {
                
                hasForm = true;
                String elementName = element.getName() != null ? element.getName() : 
                                   (element.getId() != null ? element.getId() : element.getTagName());
                
                testCase.addStep(new TestStep(
                    "Enter test data in the " + elementName + " field",
                    "Data should be entered correctly"
                ));
            }
        }
        
        // Add submit step if the page has a form
        if (hasForm) {
            testCase.addStep(new TestStep("Submit the form", "Form should be submitted successfully"));
        }
        
        testCases.add(testCase);
        return testCases;
    }
    
    /**
     * Gets the locator for a web element.
     *
     * @param element The web element
     * @return The locator
     */
    private String getLocator(WebElement element) {
        if (element.getId() != null && !element.getId().isEmpty()) {
            return "By.id(\"" + element.getId() + "\")";
        } else if (element.getName() != null && !element.getName().isEmpty()) {
            return "By.name(\"" + element.getName() + "\")";
        } else if (element.getXpath() != null && !element.getXpath().isEmpty()) {
            return "By.xpath(\"" + element.getXpath() + "\")";
        } else if (element.getCssSelector() != null && !element.getCssSelector().isEmpty()) {
            return "By.cssSelector(\"" + element.getCssSelector() + "\")";
        } else {
            return "By.tagName(\"" + element.getTagName() + "\")";
        }
    }
    
    /**
     * Formats a name as a class name.
     *
     * @param name The name
     * @return The formatted class name
     */
    private String formatClassName(String name) {
        // Remove special characters and spaces
        String className = name.replaceAll("[^a-zA-Z0-9]", " ");
        
        // Split by spaces
        String[] parts = className.split("\\s+");
        
        // Capitalize each part
        StringBuilder formattedName = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                formattedName.append(Character.toUpperCase(part.charAt(0)));
                if (part.length() > 1) {
                    formattedName.append(part.substring(1).toLowerCase());
                }
            }
        }
        
        return formattedName.toString();
    }
    
    /**
     * Formats a name as a method name.
     *
     * @param name The name
     * @return The formatted method name
     */
    private String formatMethodName(String name) {
        // First format as class name
        String className = formatClassName(name);
        
        // Convert to camel case
        if (!className.isEmpty()) {
            return Character.toLowerCase(className.charAt(0)) + className.substring(1);
        } else {
            return "test";
        }
    }
}
