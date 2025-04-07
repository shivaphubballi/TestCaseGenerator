package com.testgen;

import com.testgen.model.ApiEndpoint;
import com.testgen.model.TestCase;
import com.testgen.model.WebElement;

/**
 * Main class for the TestGen application.
 * This class provides a simple demonstration of the TestGen library.
 */
public class Main {
    /**
     * Main entry point for the application.
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        // Create a sample WebElement
        WebElement element = new WebElement("input");
        element.setId("username");
        element.setName("username");
        element.setType("text");
        element.setCssSelector("#username");
        element.setXpath("//input[@id='username']");
        
        // Create a sample ApiEndpoint
        ApiEndpoint endpoint = new ApiEndpoint("https://api.example.com/users", "GET");
        endpoint.addHeader("Content-Type", "application/json");
        endpoint.setExpectedStatusCode(200);
        
        // Create a sample TestCase
        TestCase testCase = new TestCase("Login Test", "Test the login functionality", TestCase.TestType.WEB_UI);
        testCase.addStep(new TestCase.TestStep("Enter username", "Username field should accept input"));
        testCase.addStep(new TestCase.TestStep("Enter password", "Password field should accept input"));
        testCase.addStep(new TestCase.TestStep("Click login button", "User should be redirected to dashboard"));
        
        // Print information about the samples
        System.out.println("Sample WebElement:");
        System.out.println(element);
        
        System.out.println("\nSample ApiEndpoint:");
        System.out.println(endpoint);
        
        System.out.println("\nSample TestCase:");
        System.out.println(testCase);
        
        // Print a message about the TestGen library
        System.out.println("\nTestGen library is initialized and ready to use!");
        System.out.println("Run 'java -cp bin com.testgen.cli.CommandLineRunner help' for usage information.");
    }
}
