package com.testgen.cli;

import com.testgen.TestGenLibrary;
import com.testgen.exception.TestGenException;
import com.testgen.model.TestCase;

import java.util.List;

/**
 * Command-line runner for the TestGen library.
 */
public class CommandLineRunner {
    private static final String VERSION = "1.0.0";
    
    /**
     * The main method that runs the command-line interface.
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        if (args.length == 0 || args[0].equals("help")) {
            printHelp();
            return;
        }
        
        String command = args[0];
        TestGenLibrary testGen = new TestGenLibrary();
        
        try {
            switch (command) {
                case "selenium":
                    runSeleniumCommand(args, testGen);
                    break;
                case "restassured":
                    runRestAssuredCommand(args, testGen);
                    break;
                case "jira-web":
                    runJiraWebCommand(args, testGen);
                    break;
                case "jira-api":
                    runJiraApiCommand(args, testGen);
                    break;
                case "security-web":
                    runSecurityWebCommand(args, testGen);
                    break;
                case "accessibility":
                    runAccessibilityCommand(args, testGen);
                    break;
                case "performance":
                    runPerformanceCommand(args, testGen);
                    break;
                case "analyze-web":
                    runAnalyzeWebCommand(args, testGen);
                    break;
                case "version":
                    System.out.println("TestGen version " + VERSION);
                    break;
                case "spa":
                    runSPACommand(args, testGen);
                    break;
                default:
                    System.err.println("Unknown command: " + command);
                    printHelp();
                    System.exit(1);
            }
        } catch (TestGenException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Error: Missing required arguments for command " + command);
            printHelp();
            System.exit(1);
        }
    }
    
    /**
     * Runs the 'selenium' command to generate Selenium tests.
     *
     * @param args The command-line arguments
     * @param testGen The TestGen library instance
     * @throws TestGenException If an error occurs during test generation
     */
    private static void runSeleniumCommand(String[] args, TestGenLibrary testGen) throws TestGenException {
        if (args.length < 4) {
            System.err.println("Error: Missing required arguments for 'selenium' command");
            System.err.println("Usage: selenium <url> <outputDir> <packageName> [--ai]");
            System.exit(1);
        }
        
        String url = args[1];
        String outputDir = args[2];
        String packageName = args[3];
        boolean useAI = args.length > 4 && args[4].equals("--ai");
        
        List<TestCase> testCases;
        if (useAI) {
            System.out.println("Generating AI-enhanced Selenium tests for " + url);
            testCases = testGen.generateEnhancedTests(url, outputDir, packageName);
        } else {
            System.out.println("Generating Selenium tests for " + url);
            testCases = testGen.generateSeleniumTests(url, outputDir, packageName);
        }
        
        System.out.println("Generated " + testCases.size() + " test cases in " + outputDir);
    }
    
    /**
     * Runs the 'restassured' command to generate REST Assured tests.
     *
     * @param args The command-line arguments
     * @param testGen The TestGen library instance
     * @throws TestGenException If an error occurs during test generation
     */
    private static void runRestAssuredCommand(String[] args, TestGenLibrary testGen) throws TestGenException {
        if (args.length < 4) {
            System.err.println("Error: Missing required arguments for 'restassured' command");
            System.err.println("Usage: restassured <collectionPath> <outputDir> <packageName> [--ai]");
            System.exit(1);
        }
        
        String collectionPath = args[1];
        String outputDir = args[2];
        String packageName = args[3];
        
        System.out.println("Generating REST Assured tests for " + collectionPath);
        List<TestCase> testCases = testGen.generateRestAssuredTests(collectionPath, outputDir, packageName);
        System.out.println("Generated " + testCases.size() + " test cases in " + outputDir);
    }
    
    /**
     * Runs the 'jira-web' command to generate Jira test cases for a web page.
     *
     * @param args The command-line arguments
     * @param testGen The TestGen library instance
     * @throws TestGenException If an error occurs during test generation
     */
    private static void runJiraWebCommand(String[] args, TestGenLibrary testGen) throws TestGenException {
        if (args.length < 4) {
            System.err.println("Error: Missing required arguments for 'jira-web' command");
            System.err.println("Usage: jira-web <url> <outputDir> <pageName>");
            System.exit(1);
        }
        
        String url = args[1];
        String outputDir = args[2];
        String pageName = args[3];
        
        System.out.println("Generating Jira test cases for " + url);
        List<TestCase> testCases = testGen.generateJiraWebTests(url, outputDir, pageName);
        System.out.println("Generated " + testCases.size() + " test cases in " + outputDir);
    }
    
    /**
     * Runs the 'jira-api' command to generate Jira test cases for a Postman collection.
     *
     * @param args The command-line arguments
     * @param testGen The TestGen library instance
     * @throws TestGenException If an error occurs during test generation
     */
    private static void runJiraApiCommand(String[] args, TestGenLibrary testGen) throws TestGenException {
        if (args.length < 4) {
            System.err.println("Error: Missing required arguments for 'jira-api' command");
            System.err.println("Usage: jira-api <collectionPath> <outputDir> <projectKey>");
            System.exit(1);
        }
        
        String collectionPath = args[1];
        String outputDir = args[2];
        String projectKey = args[3];
        
        System.out.println("Generating Jira test cases for " + collectionPath);
        List<TestCase> testCases = testGen.generateJiraApiTests(collectionPath, outputDir, projectKey);
        System.out.println("Generated " + testCases.size() + " test cases in " + outputDir);
    }
    
    /**
     * Runs the 'security-web' command to generate security-focused test cases.
     *
     * @param args The command-line arguments
     * @param testGen The TestGen library instance
     * @throws TestGenException If an error occurs during test generation
     */
    private static void runSecurityWebCommand(String[] args, TestGenLibrary testGen) throws TestGenException {
        if (args.length < 3) {
            System.err.println("Error: Missing required arguments for 'security-web' command");
            System.err.println("Usage: security-web <url> <outputDir>");
            System.exit(1);
        }
        
        String url = args[1];
        String outputDir = args[2];
        
        System.out.println("Generating security-focused test cases for " + url);
        List<TestCase> testCases = testGen.generateSecurityTests(url, outputDir);
        System.out.println("Generated " + testCases.size() + " test cases in " + outputDir);
    }
    
    /**
     * Runs the 'accessibility' command to generate accessibility-focused test cases.
     *
     * @param args The command-line arguments
     * @param testGen The TestGen library instance
     * @throws TestGenException If an error occurs during test generation
     */
    private static void runAccessibilityCommand(String[] args, TestGenLibrary testGen) throws TestGenException {
        if (args.length < 3) {
            System.err.println("Error: Missing required arguments for 'accessibility' command");
            System.err.println("Usage: accessibility <url> <outputDir>");
            System.exit(1);
        }
        
        String url = args[1];
        String outputDir = args[2];
        
        System.out.println("Generating accessibility-focused test cases for " + url);
        List<TestCase> testCases = testGen.generateAccessibilityTests(url, outputDir);
        System.out.println("Generated " + testCases.size() + " test cases in " + outputDir);
    }
    
    /**
     * Runs the 'performance' command to generate performance-focused test cases.
     *
     * @param args The command-line arguments
     * @param testGen The TestGen library instance
     * @throws TestGenException If an error occurs during test generation
     */
    private static void runPerformanceCommand(String[] args, TestGenLibrary testGen) throws TestGenException {
        if (args.length < 3) {
            System.err.println("Error: Missing required arguments for 'performance' command");
            System.err.println("Usage: performance <url> <outputDir>");
            System.exit(1);
        }
        
        String url = args[1];
        String outputDir = args[2];
        
        System.out.println("Generating performance-focused test cases for " + url);
        List<TestCase> testCases = testGen.generatePerformanceTests(url, outputDir);
        System.out.println("Generated " + testCases.size() + " test cases in " + outputDir);
    }
    
    /**
     * Runs the 'analyze-web' command to analyze test coverage for a web page.
     *
     * @param args The command-line arguments
     * @param testGen The TestGen library instance
     * @throws TestGenException If an error occurs during analysis
     */
    private static void runAnalyzeWebCommand(String[] args, TestGenLibrary testGen) throws TestGenException {
        if (args.length < 2) {
            System.err.println("Error: Missing required arguments for 'analyze-web' command");
            System.err.println("Usage: analyze-web <url>");
            System.exit(1);
        }
        
        String url = args[1];
        
        System.out.println("Analyzing test coverage for " + url);
        String report = testGen.analyzeWebCoverage(url);
        System.out.println(report);
    }
    
    /**
     * Runs the 'spa' command to generate tests for a Single Page Application.
     *
     * @param args The command-line arguments
     * @param testGen The TestGen library instance
     * @throws TestGenException If an error occurs during test generation
     */
    private static void runSPACommand(String[] args, TestGenLibrary testGen) throws TestGenException {
        if (args.length < 4) {
            System.err.println("Error: Missing required arguments for 'spa' command");
            System.err.println("Usage: spa <url> <outputDir> <packageName> [--wait=<ms>] [--ajax-timeout=<ms>]");
            System.exit(1);
        }
        
        String url = args[1];
        String outputDir = args[2];
        String packageName = args[3];
        
        // Parse any optional parameters
        int waitTime = 5000; // Default wait time
        int ajaxTimeout = 10000; // Default AJAX timeout
        
        for (int i = 4; i < args.length; i++) {
            if (args[i].startsWith("--wait=")) {
                waitTime = Integer.parseInt(args[i].substring(7));
            } else if (args[i].startsWith("--ajax-timeout=")) {
                ajaxTimeout = Integer.parseInt(args[i].substring(15));
            }
        }
        
        System.out.println("Generating tests for SPA at " + url);
        System.out.println("Using wait time: " + waitTime + "ms, AJAX timeout: " + ajaxTimeout + "ms");
        
        // Configure special SPA settings
        testGen.getWebAnalyzer().setSPA(true);
        testGen.getWebAnalyzer().getSpaAnalyzer().setDynamicContentWaitTime(waitTime);
        testGen.getWebAnalyzer().getSpaAnalyzer().setAjaxTimeout(ajaxTimeout);
        
        List<TestCase> testCases = testGen.generateSeleniumTests(url, outputDir, packageName);
        System.out.println("Generated " + testCases.size() + " SPA test cases in " + outputDir);
    }
    
    /**
     * Prints the help message.
     */
    private static void printHelp() {
        System.out.println("TestGen - Test Case Generation Tool");
        System.out.println("==================================");
        System.out.println();
        System.out.println("Commands:");
        System.out.println("  selenium <url> <outputDir> <packageName> [--ai]");
        System.out.println("    Generate Selenium tests for a web page");
        System.out.println();
        System.out.println("  restassured <collectionPath> <outputDir> <packageName> [--ai]");
        System.out.println("    Generate REST Assured tests for a Postman collection");
        System.out.println();
        System.out.println("  jira-web <url> <outputDir> <pageName>");
        System.out.println("    Generate Jira test cases for a web page");
        System.out.println();
        System.out.println("  jira-api <collectionPath> <outputDir> <projectKey>");
        System.out.println("    Generate Jira test cases for a Postman collection");
        System.out.println();
        System.out.println("  security-web <url> <outputDir>");
        System.out.println("    Generate security-focused test cases for a web page");
        System.out.println();
        System.out.println("  accessibility <url> <outputDir>");
        System.out.println("    Generate accessibility-focused test cases for a web page");
        System.out.println();
        System.out.println("  performance <url> <outputDir>");
        System.out.println("    Generate performance-focused test cases for a web page");
        System.out.println();
        System.out.println("  analyze-web <url>");
        System.out.println("    Analyze test coverage for a web page");
        System.out.println();
        System.out.println("  spa <url> <outputDir> <packageName> [--wait=<ms>] [--ajax-timeout=<ms>]");
        System.out.println("    Generate tests specifically for a Single Page Application");
        System.out.println();
        System.out.println("  version");
        System.out.println("    Display version information");
        System.out.println();
        System.out.println("  help");
        System.out.println("    Display this help message");
    }
}
