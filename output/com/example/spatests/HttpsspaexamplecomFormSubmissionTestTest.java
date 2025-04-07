package com.example.spatests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test form submission in the https://spa-example.com SPA
 */
public class HttpsspaexamplecomFormSubmissionTestTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @Before
    public void setUp() {
        // Set up ChromeDriver
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    @Test
    public void httpsspaexamplecomFormSubmissionTest() {
        // Navigate to https://spa-example.com contact page
        // Expected: The contact form should be displayed
        driver.get("https://example.com");

        // Fill in the name field
        // Expected: Text should be entered in the field
        // TODO: Implement step: Fill in the name field

        // Fill in the email field
        // Expected: Text should be entered in the field
        // TODO: Implement step: Fill in the email field

        // Fill in the message field
        // Expected: Text should be entered in the field
        // TODO: Implement step: Fill in the message field

        // Click the submit button
        // Expected: Form should be submitted via AJAX and success message should be displayed
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.id("example-id")));
        element.click();

    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
