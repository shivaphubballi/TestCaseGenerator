package com.testgen.generator;

import com.testgen.model.TestCase;
import com.testgen.model.TestCase.TestStep;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Generates Selenium test cases based on test case models.
 */
public class SeleniumTestGenerator {
    
    /**
     * Generates Selenium test cases.
     *
     * @param testCases The test cases to generate
     * @param outputDir The output directory
     * @param packageName The package name for the generated tests
     * @throws IOException If an error occurs during generation
     */
    public void generate(List<TestCase> testCases, String outputDir, String packageName) throws IOException {
        // Create output directory with package structure
        String packageDir = outputDir + File.separator + packageName.replace('.', File.separatorChar);
        Files.createDirectories(Paths.get(packageDir));
        
        // Generate a test class for each test case
        for (TestCase testCase : testCases) {
            String className = toClassName(testCase.getName()) + "Test";
            File outputFile = new File(packageDir, className + ".java");
            
            try (FileWriter writer = new FileWriter(outputFile)) {
                // Write package declaration
                writer.write("package " + packageName + ";\n\n");
                
                // Write imports
                writer.write("import org.openqa.selenium.WebDriver;\n");
                writer.write("import org.openqa.selenium.WebElement;\n");
                writer.write("import org.openqa.selenium.By;\n");
                writer.write("import org.openqa.selenium.chrome.ChromeDriver;\n");
                writer.write("import org.openqa.selenium.support.ui.ExpectedConditions;\n");
                writer.write("import org.openqa.selenium.support.ui.WebDriverWait;\n");
                writer.write("import java.time.Duration;\n");
                writer.write("import org.junit.After;\n");
                writer.write("import org.junit.Before;\n");
                writer.write("import org.junit.Test;\n");
                writer.write("import static org.junit.Assert.*;\n\n");
                
                // Write class declaration
                writer.write("/**\n");
                writer.write(" * " + testCase.getDescription() + "\n");
                writer.write(" */\n");
                writer.write("public class " + className + " {\n\n");
                
                // Write fields
                writer.write("    private WebDriver driver;\n");
                writer.write("    private WebDriverWait wait;\n\n");
                
                // Write setup method
                writer.write("    @Before\n");
                writer.write("    public void setUp() {\n");
                writer.write("        // Set up ChromeDriver\n");
                writer.write("        driver = new ChromeDriver();\n");
                writer.write("        wait = new WebDriverWait(driver, Duration.ofSeconds(10));\n");
                writer.write("        driver.manage().window().maximize();\n");
                writer.write("    }\n\n");
                
                // Write test method
                writer.write("    @Test\n");
                writer.write("    public void " + toMethodName(testCase.getName()) + "() {\n");
                
                // Add test steps
                for (TestStep step : testCase.getSteps()) {
                    writer.write("        // " + step.getDescription() + "\n");
                    writer.write("        // Expected: " + step.getExpectedResult() + "\n");
                    
                    // Generate code based on step description
                    String code = generateSeleniumCode(step);
                    writer.write("        " + code + "\n\n");
                }
                
                writer.write("    }\n\n");
                
                // Write teardown method
                writer.write("    @After\n");
                writer.write("    public void tearDown() {\n");
                writer.write("        if (driver != null) {\n");
                writer.write("            driver.quit();\n");
                writer.write("        }\n");
                writer.write("    }\n");
                
                // Close class
                writer.write("}\n");
            }
            
            System.out.println("Generated Selenium test: " + outputFile.getAbsolutePath());
        }
    }
    
    /**
     * Generates Selenium code for a test step.
     *
     * @param step The test step
     * @return The generated code
     */
    private String generateSeleniumCode(TestStep step) {
        String description = step.getDescription().toLowerCase();
        
        if (description.contains("navigate")) {
            return "driver.get(\"https://example.com\");";
        } else if (description.contains("click")) {
            return "WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.id(\"example-id\")));\n" +
                   "        element.click();";
        } else if (description.contains("enter") && description.contains("username")) {
            return "WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(\"username\")));\n" +
                   "        usernameField.sendKeys(\"testuser\");";
        } else if (description.contains("enter") && description.contains("password")) {
            return "WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(\"password\")));\n" +
                   "        passwordField.sendKeys(\"password\");";
        } else if (description.contains("submit") || description.contains("login")) {
            return "WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.id(\"submit\")));\n" +
                   "        submitButton.click();";
        } else if (description.contains("verify") || description.contains("check")) {
            return "assertTrue(\"Verification failed: \" + \"" + step.getExpectedResult() + "\", " +
                   "driver.findElement(By.id(\"result\")).isDisplayed());";
        } else if (description.contains("wait")) {
            return "Thread.sleep(2000); // Wait for 2 seconds";
        } else {
            return "// TODO: Implement step: " + step.getDescription();
        }
    }
    
    /**
     * Converts a test case name to a Java class name.
     *
     * @param name The test case name
     * @return The Java class name
     */
    private String toClassName(String name) {
        // Remove non-alphanumeric characters and split by whitespace
        String[] words = name.replaceAll("[^a-zA-Z0-9\\s]", "").split("\\s+");
        
        // Capitalize each word and join
        StringBuilder className = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                className.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    className.append(word.substring(1));
                }
            }
        }
        
        return className.toString();
    }
    
    /**
     * Converts a test case name to a Java method name.
     *
     * @param name The test case name
     * @return The Java method name
     */
    private String toMethodName(String name) {
        // Convert to class name first
        String className = toClassName(name);
        
        // Convert first character to lowercase
        if (!className.isEmpty()) {
            return Character.toLowerCase(className.charAt(0)) + 
                   (className.length() > 1 ? className.substring(1) : "");
        }
        
        return "test";
    }
}
