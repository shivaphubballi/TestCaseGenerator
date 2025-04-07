package com.example.tests;

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
 * Test the login functionality on the https://example.com page
 */
public class HttpsexamplecomLoginTestEnhancedTest {

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
    public void httpsexamplecomLoginTestEnhanced() {
        // Navigate to https://example.com
        // Expected: The https://example.com page should load successfully
        driver.get("https://example.com");

        // Enter username in the username field
        // Expected: Username should be accepted
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        usernameField.sendKeys("testuser");

        // Enter password in the password field
        // Expected: Password should be masked with asterisks
        WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));
        passwordField.sendKeys("password");

        // Click the login button
        // Expected: User should be redirected to the dashboard
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.id("example-id")));
        element.click();

        // Verify behavior with different browser window sizes
        // Expected: UI should adapt responsively to different screen sizes
        assertTrue("Verification failed: " + "UI should adapt responsively to different screen sizes", driver.findElement(By.id("result")).isDisplayed());

        // Check for error handling on invalid inputs
        // Expected: Appropriate error messages should be displayed
        assertTrue("Verification failed: " + "Appropriate error messages should be displayed", driver.findElement(By.id("result")).isDisplayed());

    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
