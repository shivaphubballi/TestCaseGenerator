package com.testgen.core;

import com.testgen.exception.TestGenException;
import com.testgen.model.WebElement;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class WebAnalyzerTest {

    private WebAnalyzer webAnalyzer;

    @Before
    public void setUp() {
        webAnalyzer = new WebAnalyzer();
    }

    @Test(expected = TestGenException.class)
    public void testAnalyzeWebPageWithNullUrl() throws TestGenException {
        webAnalyzer.analyzeWebPage(null);
    }

    @Test(expected = TestGenException.class)
    public void testAnalyzeWebPageWithEmptyUrl() throws TestGenException {
        webAnalyzer.analyzeWebPage("  ");
    }

    @Test
    public void testExtractInteractiveElements() throws Exception {
        // Create a test HTML document
        String html = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head><title>Test Page</title></head>\n" +
                "<body>\n" +
                "  <form id=\"testForm\" action=\"/submit\" method=\"POST\">\n" +
                "    <input type=\"text\" id=\"name\" name=\"name\" placeholder=\"Your name\" required>\n" +
                "    <input type=\"email\" id=\"email\" name=\"email\" placeholder=\"Your email\">\n" +
                "    <select id=\"country\" name=\"country\">\n" +
                "      <option value=\"us\">United States</option>\n" +
                "      <option value=\"ca\">Canada</option>\n" +
                "    </select>\n" +
                "    <input type=\"checkbox\" id=\"terms\" name=\"terms\">\n" +
                "    <button type=\"submit\">Submit</button>\n" +
                "  </form>\n" +
                "  <a href=\"https://example.com\">Example Link</a>\n" +
                "  <button id=\"testButton\">Click Me</button>\n" +
                "</body>\n" +
                "</html>";
        
        Document doc = Jsoup.parse(html);
        
        // Use reflection to access private method
        java.lang.reflect.Method extractMethod = WebAnalyzer.class.getDeclaredMethod(
                "extractInteractiveElements", Document.class, String.class);
        extractMethod.setAccessible(true);
        
        List<WebElement> elements = (List<WebElement>) extractMethod.invoke(webAnalyzer, doc, "https://example.com");
        
        // Verify results
        assertNotNull(elements);
        assertFalse(elements.isEmpty());
        
        // Check for form
        boolean foundForm = false;
        for (WebElement element : elements) {
            if ("form".equals(element.getType()) && "testForm".equals(element.getId())) {
                foundForm = true;
                assertEquals("/submit", element.getAction());
                assertEquals("POST", element.getMethod());
                break;
            }
        }
        assertTrue("Form element should be found", foundForm);
        
        // Check for input
        boolean foundNameInput = false;
        for (WebElement element : elements) {
            if ("input-text".equals(element.getType()) && "name".equals(element.getId())) {
                foundNameInput = true;
                assertEquals("Your name", element.getPlaceholder());
                assertTrue(element.isRequired());
                break;
            }
        }
        assertTrue("Name input element should be found", foundNameInput);
        
        // Check for link
        boolean foundLink = false;
        for (WebElement element : elements) {
            if ("link".equals(element.getType()) && element.getHref() != null && 
                element.getHref().contains("example.com")) {
                foundLink = true;
                break;
            }
        }
        assertTrue("Link element should be found", foundLink);
    }

    @Test
    public void testAnalyzeWebPageWithMockDocument() throws Exception {
        // Create a mock using a spy on the real WebAnalyzer
        WebAnalyzer spyAnalyzer = Mockito.spy(webAnalyzer);
        
        // Create a simple HTML document to return
        String html = "<!DOCTYPE html><html><head><title>Test</title></head><body>" +
                "<form id=\"testForm\"><input type=\"text\" id=\"testInput\"></form>" +
                "</body></html>";
        Document mockDoc = Jsoup.parse(html);
        
        // Make extractInteractiveElements return a known result
        doReturn(mockDoc).when(spyAnalyzer).analyzeWebPage(anyString());
        
        // Test the method with our mock
        List<WebElement> elements = spyAnalyzer.analyzeWebPage("https://example.com");
        
        // Verify results
        assertNotNull(elements);
    }
}
