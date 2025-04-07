package com.testgen;

import com.testgen.core.PostmanCollectionAnalyzer;
import com.testgen.core.WebAnalyzer;
import com.testgen.exception.TestGenException;
import com.testgen.generator.JiraTestGenerator;
import com.testgen.generator.RestAssuredTestGenerator;
import com.testgen.generator.SeleniumTestGenerator;
import com.testgen.model.ApiEndpoint;
import com.testgen.model.TestCase;
import com.testgen.model.WebElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TestGenLibraryTest {
    
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    
    private WebAnalyzer mockWebAnalyzer;
    private PostmanCollectionAnalyzer mockPostmanAnalyzer;
    private SeleniumTestGenerator mockSeleniumGenerator;
    private RestAssuredTestGenerator mockRestAssuredGenerator;
    private JiraTestGenerator mockJiraGenerator;
    private TestGenLibrary testGenLibrary;
    
    @Before
    public void setUp() {
        mockWebAnalyzer = Mockito.mock(WebAnalyzer.class);
        mockPostmanAnalyzer = Mockito.mock(PostmanCollectionAnalyzer.class);
        mockSeleniumGenerator = Mockito.mock(SeleniumTestGenerator.class);
        mockRestAssuredGenerator = Mockito.mock(RestAssuredTestGenerator.class);
        mockJiraGenerator = Mockito.mock(JiraTestGenerator.class);
        
        testGenLibrary = new TestGenLibrary(
            mockWebAnalyzer,
            mockPostmanAnalyzer,
            mockSeleniumGenerator,
            mockRestAssuredGenerator,
            mockJiraGenerator
        );
    }
    
    @Test
    public void testConstructorWithoutParameters() {
        TestGenLibrary library = new TestGenLibrary();
        assertNotNull(library.getWebAnalyzer());
        assertNotNull(library.getPostmanAnalyzer());
        assertNotNull(library.getSeleniumGenerator());
        assertNotNull(library.getRestAssuredGenerator());
        assertNotNull(library.getJiraGenerator());
    }
    
    @Test(expected = TestGenException.class)
    public void testGenerateSeleniumTestsWithNullUrl() throws TestGenException {
        testGenLibrary.generateSeleniumTests(null, "output", "com.example");
    }
    
    @Test(expected = TestGenException.class)
    public void testGenerateSeleniumTestsWithEmptyUrl() throws TestGenException {
        testGenLibrary.generateSeleniumTests("  ", "output", "com.example");
    }
    
    @Test
    public void testGenerateSeleniumTests() throws TestGenException, IOException {
        // Setup
        String url = "https://example.com";
        String outputDir = tempFolder.newFolder("selenium-tests").getPath();
        String packageName = "com.example.tests";
        
        List<WebElement> mockElements = new ArrayList<>();
        WebElement element = new WebElement();
        element.setType("input-text");
        element.setId("username");
        mockElements.add(element);
        
        when(mockWebAnalyzer.analyzeWebPage(url)).thenReturn(mockElements);
        when(mockSeleniumGenerator.generateTestClassFromElements(
            eq(mockElements), eq(url), anyString(), eq(packageName)))
            .thenReturn("public class ExampleTest {}");
        
        // Execute
        String outputPath = testGenLibrary.generateSeleniumTests(url, outputDir, packageName);
        
        // Verify
        assertNotNull(outputPath);
        assertTrue(outputPath.endsWith(".java"));
        assertTrue(new File(outputPath).exists());
        
        verify(mockWebAnalyzer).analyzeWebPage(url);
        verify(mockSeleniumGenerator).generateTestClassFromElements(
            eq(mockElements), eq(url), anyString(), eq(packageName));
    }
    
    @Test(expected = TestGenException.class)
    public void testGenerateRestAssuredTestsWithNullPath() throws TestGenException {
        testGenLibrary.generateRestAssuredTests(null, "output", "com.example");
    }
    
    @Test(expected = TestGenException.class)
    public void testGenerateRestAssuredTestsWithEmptyPath() throws TestGenException {
        testGenLibrary.generateRestAssuredTests("", "output", "com.example");
    }
    
    @Test
    public void testGenerateRestAssuredTests() throws TestGenException, IOException {
        // Setup
        String collectionPath = tempFolder.newFile("collection.json").getPath();
        String outputDir = tempFolder.newFolder("restassured-tests").getPath();
        String packageName = "com.example.tests";
        
        List<ApiEndpoint> mockEndpoints = new ArrayList<>();
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setName("Get Users");
        endpoint.setMethod("GET");
        endpoint.setUrl("https://api.example.com/users");
        mockEndpoints.add(endpoint);
        
        when(mockPostmanAnalyzer.analyzeCollection(collectionPath)).thenReturn(mockEndpoints);
        when(mockRestAssuredGenerator.generateTestClass(
            eq(mockEndpoints), anyString(), eq(packageName)))
            .thenReturn("public class ApiTest {}");
        
        // Execute
        String outputPath = testGenLibrary.generateRestAssuredTests(collectionPath, outputDir, packageName);
        
        // Verify
        assertNotNull(outputPath);
        assertTrue(outputPath.endsWith(".java"));
        assertTrue(new File(outputPath).exists());
        
        verify(mockPostmanAnalyzer).analyzeCollection(collectionPath);
        verify(mockRestAssuredGenerator).generateTestClass(
            eq(mockEndpoints), anyString(), eq(packageName));
    }
    
    @Test(expected = TestGenException.class)
    public void testGenerateRestAssuredTestsFromJsonWithNullJson() throws TestGenException {
        testGenLibrary.generateRestAssuredTestsFromJson(null, "output", "com.example", "TestCollection");
    }
    
    @Test(expected = TestGenException.class)
    public void testGenerateRestAssuredTestsFromJsonWithEmptyJson() throws TestGenException {
        testGenLibrary.generateRestAssuredTestsFromJson("", "output", "com.example", "TestCollection");
    }
    
    @Test
    public void testGenerateRestAssuredTestsFromJson() throws TestGenException, IOException {
        // Setup
        String collectionJson = "{\"info\": {\"name\": \"Test Collection\"}}";
        String outputDir = tempFolder.newFolder("restassured-json-tests").getPath();
        String packageName = "com.example.tests";
        String collectionName = "TestCollection";
        
        List<ApiEndpoint> mockEndpoints = new ArrayList<>();
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setName("Get Users");
        endpoint.setMethod("GET");
        endpoint.setUrl("https://api.example.com/users");
        mockEndpoints.add(endpoint);
        
        when(mockPostmanAnalyzer.analyzeCollectionFromJson(collectionJson)).thenReturn(mockEndpoints);
        when(mockRestAssuredGenerator.generateTestClass(
            eq(mockEndpoints), anyString(), eq(packageName)))
            .thenReturn("public class ApiTest {}");
        
        // Execute
        String outputPath = testGenLibrary.generateRestAssuredTestsFromJson(
            collectionJson, outputDir, packageName, collectionName);
        
        // Verify
        assertNotNull(outputPath);
        assertTrue(outputPath.endsWith(".java"));
        assertTrue(new File(outputPath).exists());
        
        verify(mockPostmanAnalyzer).analyzeCollectionFromJson(collectionJson);
        verify(mockRestAssuredGenerator).generateTestClass(
            eq(mockEndpoints), anyString(), eq(packageName));
    }
    
    @Test(expected = TestGenException.class)
    public void testGenerateJiraWebTestsWithNullUrl() throws TestGenException {
        testGenLibrary.generateJiraWebTests(null, "output", "Page Name");
    }
    
    @Test(expected = TestGenException.class)
    public void testGenerateJiraWebTestsWithEmptyUrl() throws TestGenException {
        testGenLibrary.generateJiraWebTests("", "output", "Page Name");
    }
    
    @Test
    public void testGenerateJiraWebTests() throws TestGenException, IOException {
        // Setup
        String url = "https://example.com";
        String outputDir = tempFolder.newFolder("jira-web-tests").getPath();
        String pageName = "Example Page";
        
        List<WebElement> mockElements = new ArrayList<>();
        WebElement element = new WebElement();
        element.setType("input-text");
        element.setId("username");
        mockElements.add(element);
        
        List<TestCase> mockTestCases = new ArrayList<>();
        TestCase testCase = new TestCase();
        testCase.setName("Login Test");
        mockTestCases.add(testCase);
        
        List<String> mockJiraTestCases = List.of("h1. Login Test\n\nTest steps...");
        
        when(mockWebAnalyzer.analyzeWebPage(url)).thenReturn(mockElements);
        when(mockJiraGenerator.generateFromWebElements(mockElements, url, pageName)).thenReturn(mockTestCases);
        when(mockJiraGenerator.convertToJiraFormat(mockTestCases)).thenReturn(mockJiraTestCases);
        
        // Execute
        String outputPath = testGenLibrary.generateJiraWebTests(url, outputDir, pageName);
        
        // Verify
        assertNotNull(outputPath);
        assertTrue(outputPath.endsWith(".txt"));
        assertTrue(new File(outputPath).exists());
        
        // Read file content to verify
        String fileContent = Files.readString(Path.of(outputPath));
        assertTrue(fileContent.contains("h1. Login Test"));
        
        verify(mockWebAnalyzer).analyzeWebPage(url);
        verify(mockJiraGenerator).generateFromWebElements(mockElements, url, pageName);
        verify(mockJiraGenerator).convertToJiraFormat(mockTestCases);
    }
    
    @Test(expected = TestGenException.class)
    public void testGenerateJiraApiTestsWithNullPath() throws TestGenException {
        testGenLibrary.generateJiraApiTests(null, "output");
    }
    
    @Test(expected = TestGenException.class)
    public void testGenerateJiraApiTestsWithEmptyPath() throws TestGenException {
        testGenLibrary.generateJiraApiTests("", "output");
    }
    
    @Test
    public void testGenerateJiraApiTests() throws TestGenException, IOException {
        // Setup
        String collectionPath = tempFolder.newFile("collection.json").getPath();
        String outputDir = tempFolder.newFolder("jira-api-tests").getPath();
        
        List<ApiEndpoint> mockEndpoints = new ArrayList<>();
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setName("Get Users");
        endpoint.setMethod("GET");
        endpoint.setUrl("https://api.example.com/users");
        mockEndpoints.add(endpoint);
        
        List<TestCase> mockTestCases = new ArrayList<>();
        TestCase testCase = new TestCase();
        testCase.setName("API Test: Get Users");
        mockTestCases.add(testCase);
        
        List<String> mockJiraTestCases = List.of("h1. API Test: Get Users\n\nTest steps...");
        
        when(mockPostmanAnalyzer.analyzeCollection(collectionPath)).thenReturn(mockEndpoints);
        when(mockJiraGenerator.generateFromApiEndpoints(eq(mockEndpoints), anyString())).thenReturn(mockTestCases);
        when(mockJiraGenerator.convertToJiraFormat(mockTestCases)).thenReturn(mockJiraTestCases);
        
        // Execute
        String outputPath = testGenLibrary.generateJiraApiTests(collectionPath, outputDir);
        
        // Verify
        assertNotNull(outputPath);
        assertTrue(outputPath.endsWith(".txt"));
        assertTrue(new File(outputPath).exists());
        
        // Read file content to verify
        String fileContent = Files.readString(Path.of(outputPath));
        assertTrue(fileContent.contains("h1. API Test: Get Users"));
        
        verify(mockPostmanAnalyzer).analyzeCollection(collectionPath);
        verify(mockJiraGenerator).generateFromApiEndpoints(eq(mockEndpoints), anyString());
        verify(mockJiraGenerator).convertToJiraFormat(mockTestCases);
    }
    
    @Test
    public void testAnalyzeWebPage() throws TestGenException {
        // Setup
        String url = "https://example.com";
        List<WebElement> mockElements = new ArrayList<>();
        
        when(mockWebAnalyzer.analyzeWebPage(url)).thenReturn(mockElements);
        
        // Execute
        List<WebElement> result = testGenLibrary.analyzeWebPage(url);
        
        // Verify
        assertSame(mockElements, result);
        verify(mockWebAnalyzer).analyzeWebPage(url);
    }
    
    @Test
    public void testAnalyzePostmanCollection() throws TestGenException {
        // Setup
        String collectionPath = "collection.json";
        List<ApiEndpoint> mockEndpoints = new ArrayList<>();
        
        when(mockPostmanAnalyzer.analyzeCollection(collectionPath)).thenReturn(mockEndpoints);
        
        // Execute
        List<ApiEndpoint> result = testGenLibrary.analyzePostmanCollection(collectionPath);
        
        // Verify
        assertSame(mockEndpoints, result);
        verify(mockPostmanAnalyzer).analyzeCollection(collectionPath);
    }
    
    @Test
    public void testAnalyzePostmanCollectionFromJson() throws TestGenException {
        // Setup
        String collectionJson = "{\"info\": {\"name\": \"Test Collection\"}}";
        List<ApiEndpoint> mockEndpoints = new ArrayList<>();
        
        when(mockPostmanAnalyzer.analyzeCollectionFromJson(collectionJson)).thenReturn(mockEndpoints);
        
        // Execute
        List<ApiEndpoint> result = testGenLibrary.analyzePostmanCollectionFromJson(collectionJson);
        
        // Verify
        assertSame(mockEndpoints, result);
        verify(mockPostmanAnalyzer).analyzeCollectionFromJson(collectionJson);
    }
}
