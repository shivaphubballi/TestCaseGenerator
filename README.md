# TestGen Library

A Java library for automatically generating Selenium, REST Assured, and Jira test cases from web URLs and Postman collections.

## Overview

TestGen is a testing automation utility that helps testers and developers generate comprehensive test cases with minimal effort. It analyzes either web applications or API collections and produces ready-to-use test cases for popular testing frameworks.

## Features

### Web Analysis
- Scans web pages to identify interactive elements (forms, buttons, links, etc.)
- Generates Selenium WebDriver test scripts
- Creates detailed Jira test cases for manual testing

### API Analysis
- Parses Postman collections to understand endpoints and request structures
- Generates REST Assured test scripts
- Creates detailed Jira test cases for API testing

## Getting Started

### Prerequisites

- Java 11 or later
- Maven 3.6 or later

### Installation

Clone the repository and build the project:

```bash
git clone https://github.com/yourusername/testgen.git
cd testgen
mvn clean install
```

### Usage

You can use TestGen as a command-line tool or integrate it into your Java application.

#### Command-Line Usage

```bash
# Generate Selenium tests for a web page
java -jar testgen.jar selenium https://example.com output com.example.tests

# Generate REST Assured tests for a Postman collection
java -jar testgen.jar restassured collection.json output com.example.tests

# Generate Jira test cases for a web page
java -jar testgen.jar jira-web https://example.com output HomePage

# Generate Jira test cases for a Postman collection
java -jar testgen.jar jira-api collection.json output
```

#### Java API Usage

```java
import com.testgen.TestGenLibrary;

public class MyApp {
    public static void main(String[] args) {
        TestGenLibrary testGen = new TestGenLibrary();
        
        // Generate Selenium tests
        testGen.generateSeleniumTests(
            "https://example.com", 
            "output", 
            "com.example.tests"
        );
        
        // Generate REST Assured tests
        testGen.generateRestAssuredTests(
            "collection.json", 
            "output", 
            "com.example.tests"
        );
        
        // Generate Jira test cases for a web page
        testGen.generateJiraTestCasesForWebPage(
            "https://example.com", 
            "output", 
            "HomePage"
        );
        
        // Generate Jira test cases for a Postman collection
        testGen.generateJiraTestCasesForPostmanCollection(
            "collection.json", 
            "output"
        );
    }
}
```

## Architecture

The library follows a modular architecture:

1. **Analysis Components**
   - `WebAnalyzer`: Analyzes web pages and extracts elements
   - `PostmanCollectionAnalyzer`: Analyzes Postman collections and extracts endpoints

2. **Model Classes**
   - `WebElement`: Represents an element on a web page
   - `ApiEndpoint`: Represents an API endpoint
   - `TestCase`: Represents a test case with steps and expected results

3. **Test Generators**
   - `SeleniumTestGenerator`: Generates Selenium tests
   - `RestAssuredTestGenerator`: Generates REST Assured tests
   - `JiraTestGenerator`: Generates Jira test cases

4. **Utility Classes**
   - `JsonUtil`: Utility methods for JSON operations
   - `WebUtil`: Utility methods for web operations

## Dependencies

- JSoup: HTML parsing for web analysis
- Jackson: JSON parsing for Postman collections
- Selenium WebDriver: Used in generated Selenium tests
- REST Assured: Used in generated API tests
- JUnit: Testing framework
- Mockito: Mocking framework for tests

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Roadmap

- Support for generating TestNG tests
- Support for generating Cucumber feature files
- Enhanced web element detection with AI
- Integration with CI/CD pipelines
