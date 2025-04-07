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
 * Test navigation between different routes in the https://spa-example.com SPA
 */
public class HttpsspaexamplecomRouteNavigationTestTest {

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
    public void httpsspaexamplecomRouteNavigationTest() {
        // Navigate to https://spa-example.com
        // Expected: The https://spa-example.com page should load successfully with the home route active
        driver.get("https://example.com");

        // Click on the Home navigation link
        // Expected: URL should change to #/ and corresponding content should be displayed
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.id("example-id")));
        element.click();

        // Click on the Products navigation link
        // Expected: URL should change to #/products and corresponding content should be displayed
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.id("example-id")));
        element.click();

        // Click on the Contact navigation link
        // Expected: URL should change to #/contact and corresponding content should be displayed
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.id("example-id")));
        element.click();

        // Click on the Product Detail navigation link
        // Expected: URL should change to #/products/1 and corresponding content should be displayed
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
