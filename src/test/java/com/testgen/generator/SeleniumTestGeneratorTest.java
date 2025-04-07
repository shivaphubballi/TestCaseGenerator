package com.testgen.generator;

import com.testgen.exception.TestGenException;
import com.testgen.model.TestCase;
import com.testgen.model.WebElement;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class SeleniumTestGeneratorTest {

    private SeleniumTestGenerator generator;

    @Before
    public void setUp() {
        generator = new SeleniumTestGenerator();
    }

    @Test(expected = TestGenException.class)
    public void testGenerateTestClassWithNullTestCases() throws TestGenException {
        generator.generateTestClass(null, "TestClass", "com.example", "https://example.com");
    }

    @Test(expected = TestGenException.class)
    public void testGenerateTestClassWithEmptyTestCases() throws TestGenException {
        generator.generateTestClass(new ArrayList<>(), "TestClass", "com.example", "https://example.com");
    }

    @Test
    public void testGenerateTestClass() throws TestGenException {
        // Create a sample test case
        TestCase testCase = new TestCase();
        testCase.setName("Login Test");
        testCase.setType(TestCase.TestType.WEB_UI);
        testCase.setSourceUrl("https://example.com");
        
        // Add steps
        TestCase.TestStep step1 = new TestCase.TestStep(1, "Navigate to login page", "Login page is displayed");
        step1.addTestData("url", "https://example.com/login");
        testCase.addStep(step1);
        
        TestCase.TestStep step2 = new TestCase.TestStep(2, "Enter username", "Username is entered");
        step2.addTestData("locator", "#username");
        step2.addTestData("locatorType", "css");
        step2.addTestData("value", "testuser");
        testCase.addStep(step2);
        
        TestCase.TestStep step3 = new TestCase.TestStep(3, "Enter password", "Password is entered");
        step3.addTestData("locator", "#password");
        step3.addTestData("locatorType", "css");
        step3.addTestData("value", "testpass");
        testCase.addStep(step3);
        
        TestCase.TestStep step4 = new TestCase.TestStep(4, "Click login button", "User is logged in");
        step4.addTestData("locator", "#loginButton");
        step4.addTestData("locatorType", "css");
        testCase.addStep(step4);
        
        // Generate test class
        String testCode = generator.generateTestClass(List.of(testCase), "LoginTest", "com.example.tests", "https://example.com");
        
        // Verify results
        assertNotNull(testCode);
        assertTrue(testCode.contains("package com.example.tests;"));
        assertTrue(testCode.contains("public class LoginTest {"));
        assertTrue(testCode.contains("driver.get(\"https://example.com/login\");"));
        assertTrue(testCode.contains("driver.findElement(By.cssSelector(\"#username\")).sendKeys(\"testuser\");"));
        assertTrue(testCode.contains("driver.findElement(By.cssSelector(\"#password\")).sendKeys(\"testpass\");"));
        assertTrue(testCode.contains("driver.findElement(By.cssSelector(\"#loginButton\")).click();"));
    }

    @Test(expected = TestGenException.class)
    public void testGenerateTestClassFromElementsWithNullElements() throws TestGenException {
        generator.generateTestClassFromElements(null, "https://example.com", "TestClass", "com.example");
    }

    @Test(expected = TestGenException.class)
    public void testGenerateTestClassFromElementsWithEmptyElements() throws TestGenException {
        generator.generateTestClassFromElements(new ArrayList<>(), "https://example.com", "TestClass", "com.example");
    }

    @Test
    public void testGenerateTestClassFromElements() throws TestGenException {
        // Create sample web elements
        List<WebElement> elements = new ArrayList<>();
        
        // Create a form element
        WebElement form = new WebElement();
        form.setType("form");
        form.setId("loginForm");
        form.setLocator("css");
        form.setLocatorValue("#loginForm");
        form.setAction("/dashboard");
        form.setMethod("POST");
        elements.add(form);
        
        // Create form input elements
        WebElement usernameInput = new WebElement();
        usernameInput.setType("input-text");
        usernameInput.setId("username");
        usernameInput.setName("username");
        usernameInput.setLocator("css");
        usernameInput.setLocatorValue("#username");
        usernameInput.setParentForm(form.getLocatorValue());
        usernameInput.setPlaceholder("Enter username");
        elements.add(usernameInput);
        
        WebElement passwordInput = new WebElement();
        passwordInput.setType("input-password");
        passwordInput.setId("password");
        passwordInput.setName("password");
        passwordInput.setLocator("css");
        passwordInput.setLocatorValue("#password");
        passwordInput.setParentForm(form.getLocatorValue());
        passwordInput.setPlaceholder("Enter password");
        elements.add(passwordInput);
        
        WebElement submitButton = new WebElement();
        submitButton.setType("input-submit");
        submitButton.setId("loginButton");
        submitButton.setLocator("css");
        submitButton.setLocatorValue("#loginButton");
        submitButton.setParentForm(form.getLocatorValue());
        elements.add(submitButton);
        
        // Create a link element
        WebElement link = new WebElement();
        link.setType("link");
        link.setId("signupLink");
        link.setLocator("css");
        link.setLocatorValue("#signupLink");
        link.setText("Sign Up");
        link.setHref("https://example.com/signup");
        elements.add(link);
        
        // Generate test class
        String testCode = generator.generateTestClassFromElements(elements, "https://example.com", "LoginPageTest", "com.example.tests");
        
        // Verify results
        assertNotNull(testCode);
        assertTrue(testCode.contains("package com.example.tests;"));
        assertTrue(testCode.contains("public class LoginPageTest {"));
        assertTrue(testCode.contains("private String baseUrl = \"https://example.com\";"));
        assertTrue(testCode.contains("driver.findElement(By.cssSelector(\"#username\")).sendKeys("));
        assertTrue(testCode.contains("driver.findElement(By.cssSelector(\"#password\")).sendKeys("));
        assertTrue(testCode.contains("driver.findElement(By.cssSelector(\"#loginButton\")).click();"));
        assertTrue(testCode.contains("public void testFormSubmission() {"));
        assertTrue(testCode.contains("public void testNavigation() {"));
    }
}
