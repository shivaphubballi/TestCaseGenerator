package com.testgen.cli;

import com.testgen.TestGenLibrary;
import com.testgen.exception.TestGenException;

import java.io.PrintStream;

/**
 * Command-line interface for the TestGen library.
 * Provides a simple way to use the library from the command line.
 */
public class CommandLineRunner {
    private final PrintStream outputStream;
    
    /**
     * Constructs a new CommandLineRunner.
     */
    public CommandLineRunner() {
        this(System.out);
    }
    
    /**
     * Constructs a new CommandLineRunner with the specified output stream.
     *
     * @param outputStream The output stream to write to
     */
    public CommandLineRunner(PrintStream outputStream) {
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
                case "help":
                    printUsage();
                    return 0;
                case "selenium":
                    return runSeleniumCommand(args);
                case "restassured":
                    return runRestAssuredCommand(args);
                case "jira-web":
                    return runJiraWebCommand(args);
                case "jira-api":
                    return runJiraApiCommand(args);
                case "security-web":
                    return runSecurityWebCommand(args);
                case "security-api":
                    return runSecurityApiCommand(args);
                case "accessibility":
                    return runAccessibilityCommand(args);
                case "analyze-web":
                    return runAnalyzeWebCommand(args);
                case "analyze-api":
                    return runAnalyzeApiCommand(args);
                default:
                    outputStream.println("Unknown command: " + command);
                    printUsage();
                    return 1;
            }
        } catch (Exception e) {
            outputStream.println("Error: " + e.getMessage());
            e.printStackTrace(outputStream);
            return 1;
        }
    }
    
    private int runSeleniumCommand(String[] args) throws TestGenException {
        if (args.length < 2) {
            outputStream.println("Error: URL is required for selenium command");
            return 1;
        }
        
        String url = args[1];
        String outputDir = args.length > 2 ? args[2] : "output";
        String packageName = args.length > 3 ? args[3] : "com.example.tests";
        boolean aiEnhanced = args.length > 4 && args[4].equalsIgnoreCase("--ai");
        
        outputStream.println("Generating Selenium tests for " + url);
        outputStream.println("Output directory: " + outputDir);
        outputStream.println("Package name: " + packageName);
        
        if (aiEnhanced) {
            outputStream.println("AI enhancement: Enabled");
        }
        
        TestGenLibrary testGen = new TestGenLibrary(aiEnhanced);
        testGen.generateSeleniumTests(url, outputDir, packageName);
        
        outputStream.println("Selenium tests generated successfully!");
        return 0;
    }
    
    private int runRestAssuredCommand(String[] args) throws TestGenException {
        if (args.length < 2) {
            outputStream.println("Error: Collection path is required for restassured command");
            return 1;
        }
        
        String collectionPath = args[1];
        String outputDir = args.length > 2 ? args[2] : "output";
        String packageName = args.length > 3 ? args[3] : "com.example.tests";
        boolean aiEnhanced = args.length > 4 && args[4].equalsIgnoreCase("--ai");
        
        outputStream.println("Generating REST Assured tests for " + collectionPath);
        outputStream.println("Output directory: " + outputDir);
        outputStream.println("Package name: " + packageName);
        
        if (aiEnhanced) {
            outputStream.println("AI enhancement: Enabled");
        }
        
        TestGenLibrary testGen = new TestGenLibrary(aiEnhanced);
        testGen.generateRestAssuredTests(collectionPath, outputDir, packageName);
        
        outputStream.println("REST Assured tests generated successfully!");
        return 0;
    }
    
    private int runJiraWebCommand(String[] args) throws TestGenException {
        if (args.length < 2) {
            outputStream.println("Error: URL is required for jira-web command");
            return 1;
        }
        
        String url = args[1];
        String outputDir = args.length > 2 ? args[2] : "output";
        String pageName = args.length > 3 ? args[3] : null;
        boolean aiEnhanced = args.length > 4 && args[4].equalsIgnoreCase("--ai");
        
        outputStream.println("Generating Jira test cases for web page " + url);
        outputStream.println("Output directory: " + outputDir);
        
        if (pageName != null) {
            outputStream.println("Page name: " + pageName);
        }
        
        if (aiEnhanced) {
            outputStream.println("AI enhancement: Enabled");
        }
        
        TestGenLibrary testGen = new TestGenLibrary(aiEnhanced);
        testGen.generateJiraTestCasesForWebPage(url, outputDir, pageName);
        
        outputStream.println("Jira test cases generated successfully!");
        return 0;
    }
    
    private int runJiraApiCommand(String[] args) throws TestGenException {
        if (args.length < 2) {
            outputStream.println("Error: Collection path is required for jira-api command");
            return 1;
        }
        
        String collectionPath = args[1];
        String outputDir = args.length > 2 ? args[2] : "output";
        boolean aiEnhanced = args.length > 3 && args[3].equalsIgnoreCase("--ai");
        
        outputStream.println("Generating Jira test cases for Postman collection " + collectionPath);
        outputStream.println("Output directory: " + outputDir);
        
        if (aiEnhanced) {
            outputStream.println("AI enhancement: Enabled");
        }
        
        TestGenLibrary testGen = new TestGenLibrary(aiEnhanced);
        testGen.generateJiraTestCasesForPostmanCollection(collectionPath, outputDir);
        
        outputStream.println("Jira test cases generated successfully!");
        return 0;
    }
    
    private int runSecurityWebCommand(String[] args) throws TestGenException {
        if (args.length < 2) {
            outputStream.println("Error: URL is required for security-web command");
            return 1;
        }
        
        String url = args[1];
        String outputDir = args.length > 2 ? args[2] : "output";
        String pageName = args.length > 3 ? args[3] : null;
        
        outputStream.println("Generating security test cases for web page " + url);
        outputStream.println("Output directory: " + outputDir);
        
        if (pageName != null) {
            outputStream.println("Page name: " + pageName);
        }
        
        TestGenLibrary testGen = new TestGenLibrary(true);
        testGen.generateSecurityTestCasesForWebPage(url, outputDir, pageName);
        
        outputStream.println("Security test cases generated successfully!");
        return 0;
    }
    
    private int runSecurityApiCommand(String[] args) throws TestGenException {
        if (args.length < 2) {
            outputStream.println("Error: Collection path is required for security-api command");
            return 1;
        }
        
        String collectionPath = args[1];
        String outputDir = args.length > 2 ? args[2] : "output";
        
        outputStream.println("Generating security test cases for Postman collection " + collectionPath);
        outputStream.println("Output directory: " + outputDir);
        
        TestGenLibrary testGen = new TestGenLibrary(true);
        testGen.generateSecurityTestCasesForPostmanCollection(collectionPath, outputDir);
        
        outputStream.println("Security test cases generated successfully!");
        return 0;
    }
    
    private int runAccessibilityCommand(String[] args) throws TestGenException {
        if (args.length < 2) {
            outputStream.println("Error: URL is required for accessibility command");
            return 1;
        }
        
        String url = args[1];
        String outputDir = args.length > 2 ? args[2] : "output";
        String pageName = args.length > 3 ? args[3] : null;
        
        outputStream.println("Generating accessibility test cases for web page " + url);
        outputStream.println("Output directory: " + outputDir);
        
        if (pageName != null) {
            outputStream.println("Page name: " + pageName);
        }
        
        TestGenLibrary testGen = new TestGenLibrary(true);
        testGen.generateAccessibilityTestCasesForWebPage(url, outputDir, pageName);
        
        outputStream.println("Accessibility test cases generated successfully!");
        return 0;
    }
    
    private int runAnalyzeWebCommand(String[] args) throws TestGenException {
        if (args.length < 2) {
            outputStream.println("Error: URL is required for analyze-web command");
            return 1;
        }
        
        String url = args[1];
        String pageName = args.length > 2 ? args[2] : null;
        
        outputStream.println("Analyzing test coverage for web page " + url);
        
        if (pageName != null) {
            outputStream.println("Page name: " + pageName);
        }
        
        TestGenLibrary testGen = new TestGenLibrary(true);
        String analysis = testGen.analyzeWebTestCoverage(url, pageName);
        
        outputStream.println("\nTest Coverage Analysis:");
        outputStream.println("=======================");
        outputStream.println(analysis);
        
        return 0;
    }
    
    private int runAnalyzeApiCommand(String[] args) throws TestGenException {
        if (args.length < 2) {
            outputStream.println("Error: Collection path is required for analyze-api command");
            return 1;
        }
        
        String collectionPath = args[1];
        
        outputStream.println("Analyzing test coverage for Postman collection " + collectionPath);
        
        TestGenLibrary testGen = new TestGenLibrary(true);
        String analysis = testGen.analyzeApiTestCoverage(collectionPath);
        
        outputStream.println("\nTest Coverage Analysis:");
        outputStream.println("=======================");
        outputStream.println(analysis);
        
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
        outputStream.println("  selenium <url> [output-dir] [package-name] [--ai]");
        outputStream.println("    Generate Selenium tests for a web page");
        outputStream.println("    url          URL of the web page to analyze");
        outputStream.println("    output-dir   Directory to save the generated tests (default: output)");
        outputStream.println("    package-name Package name for the generated tests (default: com.example.tests)");
        outputStream.println("    --ai         Enable AI enhancement for better test coverage (optional)");
        outputStream.println();
        outputStream.println("  restassured <collection-path> [output-dir] [package-name] [--ai]");
        outputStream.println("    Generate REST Assured tests for a Postman collection");
        outputStream.println("    collection-path Path to the Postman collection file");
        outputStream.println("    output-dir      Directory to save the generated tests (default: output)");
        outputStream.println("    package-name    Package name for the generated tests (default: com.example.tests)");
        outputStream.println("    --ai            Enable AI enhancement for better test coverage (optional)");
        outputStream.println();
        outputStream.println("  jira-web <url> [output-dir] [page-name] [--ai]");
        outputStream.println("    Generate Jira test cases for a web page");
        outputStream.println("    url          URL of the web page to analyze");
        outputStream.println("    output-dir   Directory to save the generated test cases (default: output)");
        outputStream.println("    page-name    Name of the page (optional, derived from URL if not provided)");
        outputStream.println("    --ai         Enable AI enhancement for better test coverage (optional)");
        outputStream.println();
        outputStream.println("  jira-api <collection-path> [output-dir] [--ai]");
        outputStream.println("    Generate Jira test cases for a Postman collection");
        outputStream.println("    collection-path Path to the Postman collection file");
        outputStream.println("    output-dir      Directory to save the generated test cases (default: output)");
        outputStream.println("    --ai            Enable AI enhancement for better test coverage (optional)");
        outputStream.println();
        outputStream.println("  security-web <url> [output-dir] [page-name]");
        outputStream.println("    Generate security-focused test cases for a web page");
        outputStream.println("    url          URL of the web page to analyze");
        outputStream.println("    output-dir   Directory to save the generated test cases (default: output)");
        outputStream.println("    page-name    Name of the page (optional, derived from URL if not provided)");
        outputStream.println();
        outputStream.println("  security-api <collection-path> [output-dir]");
        outputStream.println("    Generate security-focused test cases for a Postman collection");
        outputStream.println("    collection-path Path to the Postman collection file");
        outputStream.println("    output-dir      Directory to save the generated test cases (default: output)");
        outputStream.println();
        outputStream.println("  accessibility <url> [output-dir] [page-name]");
        outputStream.println("    Generate accessibility-focused test cases for a web page");
        outputStream.println("    url          URL of the web page to analyze");
        outputStream.println("    output-dir   Directory to save the generated test cases (default: output)");
        outputStream.println("    page-name    Name of the page (optional, derived from URL if not provided)");
        outputStream.println();
        outputStream.println("  analyze-web <url> [page-name]");
        outputStream.println("    Analyze test coverage for a web page and suggest improvements");
        outputStream.println("    url          URL of the web page to analyze");
        outputStream.println("    page-name    Name of the page (optional, derived from URL if not provided)");
        outputStream.println();
        outputStream.println("  analyze-api <collection-path>");
        outputStream.println("    Analyze test coverage for a Postman collection and suggest improvements");
        outputStream.println("    collection-path Path to the Postman collection file");
        outputStream.println();
        outputStream.println("  help");
        outputStream.println("    Display this help message");
        outputStream.println();
        outputStream.println("Examples:");
        outputStream.println("  java -jar testgen.jar selenium https://example.com output com.example.tests");
        outputStream.println("  java -jar testgen.jar selenium https://example.com output com.example.tests --ai");
        outputStream.println("  java -jar testgen.jar restassured collection.json output com.example.tests");
        outputStream.println("  java -jar testgen.jar jira-web https://example.com output HomePage");
        outputStream.println("  java -jar testgen.jar jira-api collection.json output");
        outputStream.println("  java -jar testgen.jar security-web https://example.com output");
        outputStream.println("  java -jar testgen.jar accessibility https://example.com output");
        outputStream.println("  java -jar testgen.jar analyze-web https://example.com");
    }
}
