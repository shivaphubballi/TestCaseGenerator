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
 * Test dynamic content loading via AJAX in the https://spa-example.com SPA
 */
public class HttpsspaexamplecomAJAXLoadingTestTest {

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
    public void httpsspaexamplecomAJAXLoadingTest() {
        // Navigate to https://spa-example.com
        // Expected: The https://spa-example.com page should load successfully
        driver.get("https://example.com");

        // Trigger AJAX call with route:products
        // Expected: The request to /api/products should complete successfully and update the UI
        // TODO: Implement step: Trigger AJAX call with route:products

        // Trigger AJAX call with click:#product-item
        // Expected: The request to /api/products/{id} should complete successfully and update the UI
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.id("example-id")));
        element.click();

        // Trigger AJAX call with submit:#contact-form
        // Expected: The request to /api/contact should complete successfully and update the UI
        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("submit")));
        submitButton.click();

    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
