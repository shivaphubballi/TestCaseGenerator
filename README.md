# TestGen - Test Case Generation Library

TestGen is a Java library that automatically generates Selenium, REST Assured, and Jira test cases from web URLs and Postman collections. It helps QA engineers and developers create comprehensive test suites quickly and consistently.

## Features

- **Web Application Analysis**: Automatically analyze web pages to identify interactive elements
- **Postman Collection Analysis**: Extract API endpoints and details from Postman collections
- **Test Case Generation**:
  - Generate Selenium WebDriver tests for web applications
  - Generate REST Assured tests for API endpoints
  - Generate structured Jira test cases for both web and API testing
- **Simple Command-Line Interface**: Easy to use from the command line

## Requirements

- Java 11 or higher
- Maven for dependency management

## Installation

### Maven

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.testgen</groupId>
    <artifactId>testgen</artifactId>
    <version>1.0.0</version>
</dependency>
