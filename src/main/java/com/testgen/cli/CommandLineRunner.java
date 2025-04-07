package com.testgen.cli;

import com.testgen.TestGenLibrary;
import com.testgen.exception.TestGenException;

import java.io.PrintStream;
import java.util.Arrays;

/**
 * Command-line interface for the TestGen library.
 * Provides a simple way to use the library from the command line.
 */
public class CommandLineRunner {
    private final TestGenLibrary testGenLibrary;
    private final PrintStream outputStream;
    
    /**
     * Constructs a new CommandLineRunner with a default TestGenLibrary instance.
     */
    public CommandLineRunner() {
        this(new TestGenLibrary(), System.out);
    }
    
    /**
     * Constructs a new CommandLineRunner with the specified TestGenLibrary instance and output stream.
     *
     * @param testGenLibrary The TestGenLibrary instance to use
     * @param outputStream The output stream to write to
     */
    public CommandLineRunner(TestGenLibrary testGenLibrary, PrintStream outputStream) {
        this.testGenLibrary = testGenLibrary;
        this.outputStream = outputStream;
    }
    
    /**
     * Main entry point for the command-line interface.
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        CommandLineRunner runner = new CommandLineRunner();
        int exitCode = runner.run(args);
        System.exit(exitCode);
    }
    
    /**
     * Runs the command-line interface with the specified arguments.
     *
     * @param args Command-line arguments
     * @return Exit code (0 for success, non-zero for error)
     */
    public int run(String[] args) {
        if (args.length == 0) {
            printUsage();
            return 1;
        }
        
        String command = args[0].toLowerCase();
        
        try {
            switch (command) {
                case "selenium":
                    return handleSeleniumCommand(Arrays.copyOfRange(args, 1, args.length));
                case "restassured":
                    return handleRestAssuredCommand(Arrays.copyOfRange(args, 1, args.length));
                case "jira-web":
                    return handleJiraWebCommand(Arrays.copyOfRange(args, 1, args.length));
                case "jira-api":
                    return handleJiraApiCommand(Arrays.copyOfRange(args, 1, args.length));
                case "help":
                    printUsage();
                    return 0;
                default:
                    outputStream.println("Unknown command: " + command);
                    printUsage();
                    return 1;
            }
        } catch (TestGenException e) {
            outputStream.println("Error: " + e.getMessage());
            return 1;
        }
    }
    
    /**
     * Handles the 'selenium' command to generate Selenium tests.
     *
     * @param args Command-line arguments
     * @return Exit code (0 for success, non-zero for error)
     * @throws TestGenException If test generation fails
     */
    private int handleSeleniumCommand(String[] args) throws TestGenException {
        if (args.length < 1) {
            outputStream.println("Error: URL is required for selenium command");
            return 1;
        }
        
        String url = args[0];
        String outputDir = args.length > 1 ? args[1] : "output";
        String packageName = args.length > 2 ? args[2] : "com.example.tests";
        
        outputStream.println("Generating Selenium tests for URL: " + url);
        String outputPath = testGenLibrary.generateSeleniumTests(url, outputDir, packageName);
        outputStream.println("Selenium tests generated successfully: " + outputPath);
        
        return 0;
    }
    
    /**
     * Handles the 'restassured' command to generate REST Assured tests.
     *
     * @param args Command-line arguments
     * @return Exit code (0 for success, non-zero for error)
     * @throws TestGenException If test generation fails
     */
    private int handleRestAssuredCommand(String[] args) throws TestGenException {
        if (args.length < 1) {
            outputStream.println("Error: Postman collection path is required for restassured command");
            return 1;
        }
        
        String collectionPath = args[0];
        String outputDir = args.length > 1 ? args[1] : "output";
        String packageName = args.length > 2 ? args[2] : "com.example.tests";
        
        outputStream.println("Generating REST Assured tests for Postman collection: " + collectionPath);
        String outputPath = testGenLibrary.generateRestAssuredTests(collectionPath, outputDir, packageName);
        outputStream.println("REST Assured tests generated successfully: " + outputPath);
        
        return 0;
    }
    
    /**
     * Handles the 'jira-web' command to generate Jira test cases for a web page.
     *
     * @param args Command-line arguments
     * @return Exit code (0 for success, non-zero for error)
     * @throws TestGenException If test generation fails
     */
    private int handleJiraWebCommand(String[] args) throws TestGenException {
        if (args.length < 1) {
            outputStream.println("Error: URL is required for jira-web command");
            return 1;
        }
        
        String url = args[0];
        String outputDir = args.length > 1 ? args[1] : "output";
        String pageName = args.length > 2 ? args[2] : null;
        
        outputStream.println("Generating Jira test cases for URL: " + url);
        String outputPath = testGenLibrary.generateJiraWebTests(url, outputDir, pageName);
        outputStream.println("Jira test cases generated successfully: " + outputPath);
        
        return 0;
    }
    
    /**
     * Handles the 'jira-api' command to generate Jira test cases for a Postman collection.
     *
     * @param args Command-line arguments
     * @return Exit code (0 for success, non-zero for error)
     * @throws TestGenException If test generation fails
     */
    private int handleJiraApiCommand(String[] args) throws TestGenException {
        if (args.length < 1) {
            outputStream.println("Error: Postman collection path is required for jira-api command");
            return 1;
        }
        
        String collectionPath = args[0];
        String outputDir = args.length > 1 ? args[1] : "output";
        
        outputStream.println("Generating Jira test cases for Postman collection: " + collectionPath);
        String outputPath = testGenLibrary.generateJiraApiTests(collectionPath, outputDir);
        outputStream.println("Jira test cases generated successfully: " + outputPath);
        
        return 0;
    }
    
    /**
     * Prints usage information for the command-line interface.
     */
    private void printUsage() {
        outputStream.println("TestGen - Automatic test case generator");
        outputStream.println();
        outputStream.println("Usage:");
        outputStream.println("  java -jar testgen.jar <command> [options]");
        outputStream.println();
        outputStream.println("Commands:");
        outputStream.println("  selenium <url> [output-dir] [package-name]");
        outputStream.println("    Generate Selenium tests for a web page");
        outputStream.println("    url          URL of the web page to analyze");
        outputStream.println("    output-dir   Directory to save the generated tests (default: output)");
        outputStream.println("    package-name Package name for the generated tests (default: com.example.tests)");
        outputStream.println();
        outputStream.println("  restassured <collection-path> [output-dir] [package-name]");
        outputStream.println("    Generate REST Assured tests for a Postman collection");
        outputStream.println("    collection-path Path to the Postman collection file");
        outputStream.println("    output-dir      Directory to save the generated tests (default: output)");
        outputStream.println("    package-name    Package name for the generated tests (default: com.example.tests)");
        outputStream.println();
        outputStream.println("  jira-web <url> [output-dir] [page-name]");
        outputStream.println("    Generate Jira test cases for a web page");
        outputStream.println("    url          URL of the web page to analyze");
        outputStream.println("    output-dir   Directory to save the generated test cases (default: output)");
        outputStream.println("    page-name    Name of the page (optional, derived from URL if not provided)");
        outputStream.println();
        outputStream.println("  jira-api <collection-path> [output-dir]");
        outputStream.println("    Generate Jira test cases for a Postman collection");
        outputStream.println("    collection-path Path to the Postman collection file");
        outputStream.println("    output-dir      Directory to save the generated test cases (default: output)");
        outputStream.println();
        outputStream.println("  help");
        outputStream.println("    Display this help message");
        outputStream.println();
        outputStream.println("Examples:");
        outputStream.println("  java -jar testgen.jar selenium https://example.com output com.example.tests");
        outputStream.println("  java -jar testgen.jar restassured collection.json output com.example.tests");
        outputStream.println("  java -jar testgen.jar jira-web https://example.com output HomePage");
        outputStream.println("  java -jar testgen.jar jira-api collection.json output");
    }
}
