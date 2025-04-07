# TestGen Library

A comprehensive Java library for automatically generating and managing test cases across multiple frameworks including Selenium, REST Assured, and Jira.

## Features

- **Automatic Test Case Generation**: Generate test cases from web URLs and Postman collections
- **Multiple Framework Support**: Generate test code for Selenium (UI) and REST Assured (API)
- **Jira Integration**: Generate formatted Jira test cases
- **AI-Enhanced Testing**: Improve test coverage with AI-generated test cases for:
  - Functional testing
  - Security testing
  - Accessibility testing
  - Performance testing
- **Command-Line Interface**: Easy-to-use CLI with multiple commands
- **Test Coverage Analysis**: Analyze and improve test coverage

## Project Structure

```
src/main/java/com/testgen/
├── ai                      # AI-related components
│   ├── AIService.java      # Interface for AI services
│   ├── AIServiceFactory.java # Factory for creating AI services
│   └── DefaultAIService.java # Default implementation
├── cli                     # Command-line interface
│   └── CommandLineRunner.java # CLI entry point
├── core                    # Core analyzers
│   ├── PostmanCollectionAnalyzer.java # Analyzes Postman collections
│   └── WebAnalyzer.java    # Analyzes web pages
├── exception               # Custom exceptions
│   └── TestGenException.java
├── generator               # Test generators
│   ├── JiraTestGenerator.java # Generates Jira test cases
│   ├── RestAssuredTestGenerator.java # Generates REST Assured tests
│   └── SeleniumTestGenerator.java # Generates Selenium tests
├── model                   # Data models
│   ├── ApiEndpoint.java    # Represents an API endpoint
│   ├── TestCase.java       # Represents a test case
│   └── WebElement.java     # Represents a web element
├── util                    # Utility classes
│   ├── JsonUtil.java       # JSON utilities
│   └── WebUtil.java        # Web utilities
├── DemoRunner.java         # Demo showcase
├── Main.java               # Main entry point
└── TestGenLibrary.java     # Main library class
```

## Usage

### Quick Start

1. Download the JAR files: `testgen.jar` (library) and `testgen-cli.jar` (CLI)
2. Make sure you have Java 11+ installed
3. Run the CLI with: `java -jar testgen-cli.jar help`

### Command-Line Interface (CLI)

The library provides a command-line interface for generating tests:

```bash
# Using the convenience script
./testgen help
./testgen selenium https://example.com output com.example.tests
./testgen restassured collection.json output com.example.tests

# Or directly with Java
java -jar testgen-cli.jar selenium https://example.com output com.example.tests
java -jar testgen-cli.jar restassured collection.json output com.example.tests --ai

# Generate Selenium tests for a web page
./testgen selenium https://example.com output com.example.tests

# Enable AI enhancement
./testgen selenium https://example.com output com.example.tests --ai

# Generate REST Assured tests for a Postman collection
./testgen restassured collection.json output com.example.tests

# Generate Jira test cases for a web page
./testgen jira-web https://example.com output HomePage

# Generate security-focused test cases
./testgen security-web https://example.com output

# Generate accessibility-focused test cases
./testgen accessibility https://example.com output

# Analyze test coverage
./testgen analyze-web https://example.com
```

### Programmatic Usage

```java
import com.testgen.TestGenLibrary;
import com.testgen.model.TestCase;

// Initialize the library
TestGenLibrary testGen = new TestGenLibrary();

// Generate Selenium tests
List<TestCase> seleniumTests = testGen.generateSeleniumTests("https://example.com", "output", "com.example.tests");

// Generate REST Assured tests
List<TestCase> restAssuredTests = testGen.generateRestAssuredTests("collection.json", "output", "com.example.tests");

// Generate with AI enhancement
List<TestCase> enhancedTests = testGen.generateEnhancedTests("https://example.com", "output", "com.example.tests");
```

## Supported Test Types

1. **Web UI Tests (Selenium)**
   - Page interaction tests
   - Form submission tests
   - Navigation tests

2. **API Tests (REST Assured)**
   - Endpoint testing
   - Request/response validation
   - Status code verification

3. **Security Tests**
   - XSS vulnerability tests
   - SQL injection tests
   - Authentication tests

4. **Accessibility Tests**
   - Keyboard navigation tests
   - Screen reader compatibility tests
   - WCAG compliance tests

5. **Performance Tests**
   - Page load time tests
   - Resource loading tests
   - Response time tests

## Requirements

- Java 11 or higher
- Jackson library (for JSON processing)
- JSoup library (for HTML parsing)

## Future Development

Planned enhancements for future versions:

1. **Integration with Cloud Testing Platforms**:
   - Integration with Selenium Grid
   - Integration with BrowserStack and Sauce Labs

2. **Advanced AI Capabilities**:
   - Integration with OpenAI for smarter test generation
   - Learning from test execution results

3. **Expanded Test Types**:
   - Mobile testing through Appium
   - Contract testing through Pact

4. **Enhanced Reporting**:
   - Visual report dashboards
   - Integration with CI/CD pipelines

5. **Test Data Generation**:
   - Smart test data generation
   - Data-driven test support

## Building From Source

```bash
# Clone the repository
git clone https://github.com/yourusername/testgen.git
cd testgen

# Build the project
./build_and_run.sh

# Create the JAR files
mkdir -p META-INF
echo "Manifest-Version: 1.0
Main-Class: com.testgen.Main
Class-Path: lib/jackson-annotations-2.15.2.jar lib/jackson-core-2.15.2.jar lib/jackson-databind-2.15.2.jar lib/jsoup-1.15.4.jar" > META-INF/MANIFEST.MF

cd bin
jar cfm ../testgen.jar ../META-INF/MANIFEST.MF com

# Create the CLI JAR
echo "Manifest-Version: 1.0
Main-Class: com.testgen.cli.CommandLineRunner
Class-Path: lib/jackson-annotations-2.15.2.jar lib/jackson-core-2.15.2.jar lib/jackson-databind-2.15.2.jar lib/jsoup-1.15.4.jar" > ../META-INF/MANIFEST.MF

jar cfm ../testgen-cli.jar ../META-INF/MANIFEST.MF com
```

## License

MIT License
