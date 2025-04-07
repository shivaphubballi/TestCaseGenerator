package com.testgen.generator;

import com.testgen.model.TestCase;
import com.testgen.model.TestCase.TestStep;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Generates REST Assured test cases based on test case models.
 */
public class RestAssuredTestGenerator {
    
    /**
     * Generates REST Assured test cases.
     *
     * @param testCases The test cases to generate
     * @param outputDir The output directory
     * @param packageName The package name for the generated tests
     * @throws IOException If an error occurs during generation
     */
    public void generate(List<TestCase> testCases, String outputDir, String packageName) throws IOException {
        // Create output directory with package structure
        String packageDir = outputDir + File.separator + packageName.replace('.', File.separatorChar);
        Files.createDirectories(Paths.get(packageDir));
        
        // Generate a test class for each test case
        for (TestCase testCase : testCases) {
            String className = toClassName(testCase.getName()) + "Test";
            File outputFile = new File(packageDir, className + ".java");
            
            try (FileWriter writer = new FileWriter(outputFile)) {
                // Write package declaration
                writer.write("package " + packageName + ";\n\n");
                
                // Write imports
                writer.write("import io.restassured.RestAssured;\n");
                writer.write("import io.restassured.http.ContentType;\n");
                writer.write("import io.restassured.response.Response;\n");
                writer.write("import org.junit.Before;\n");
                writer.write("import org.junit.Test;\n");
                writer.write("import static io.restassured.RestAssured.*;\n");
                writer.write("import static org.hamcrest.Matchers.*;\n");
                writer.write("import org.json.JSONObject;\n\n");
                
                // Write class declaration
                writer.write("/**\n");
                writer.write(" * " + testCase.getDescription() + "\n");
                writer.write(" */\n");
                writer.write("public class " + className + " {\n\n");
                
                // Write setup method
                writer.write("    @Before\n");
                writer.write("    public void setUp() {\n");
                writer.write("        // Set base URI\n");
                writer.write("        RestAssured.baseURI = \"https://api.example.com\";\n");
                writer.write("    }\n\n");
                
                // Write test method
                writer.write("    @Test\n");
                writer.write("    public void " + toMethodName(testCase.getName()) + "() {\n");
                
                // Add test steps
                for (TestStep step : testCase.getSteps()) {
                    writer.write("        // " + step.getDescription() + "\n");
                    writer.write("        // Expected: " + step.getExpectedResult() + "\n");
                    
                    // Generate code based on step description
                    String code = generateRestAssuredCode(step, testCase);
                    writer.write("        " + code + "\n\n");
                }
                
                writer.write("    }\n");
                
                // Close class
                writer.write("}\n");
            }
            
            System.out.println("Generated REST Assured test: " + outputFile.getAbsolutePath());
        }
    }
    
    /**
     * Generates REST Assured code for a test step.
     *
     * @param step The test step
     * @param testCase The parent test case
     * @return The generated code
     */
    private String generateRestAssuredCode(TestStep step, TestCase testCase) {
        String description = step.getDescription().toLowerCase();
        
        if (description.contains("get") && description.contains("request")) {
            return "Response response = given()\n" +
                   "            .header(\"Content-Type\", \"application/json\")\n" +
                   "        .when()\n" +
                   "            .get(\"/users\")\n" +
                   "        .then()\n" +
                   "            .statusCode(200)\n" +
                   "            .contentType(ContentType.JSON)\n" +
                   "            .extract().response();";
        } else if (description.contains("post") && description.contains("request")) {
            return "JSONObject requestParams = new JSONObject();\n" +
                   "        requestParams.put(\"name\", \"John Doe\");\n" +
                   "        requestParams.put(\"job\", \"Developer\");\n" +
                   "        \n" +
                   "        Response response = given()\n" +
                   "            .header(\"Content-Type\", \"application/json\")\n" +
                   "            .body(requestParams.toString())\n" +
                   "        .when()\n" +
                   "            .post(\"/users\")\n" +
                   "        .then()\n" +
                   "            .statusCode(201)\n" +
                   "            .contentType(ContentType.JSON)\n" +
                   "            .extract().response();";
        } else if (description.contains("put") && description.contains("request")) {
            return "JSONObject requestParams = new JSONObject();\n" +
                   "        requestParams.put(\"name\", \"Jane Doe\");\n" +
                   "        requestParams.put(\"job\", \"Developer\");\n" +
                   "        \n" +
                   "        Response response = given()\n" +
                   "            .header(\"Content-Type\", \"application/json\")\n" +
                   "            .body(requestParams.toString())\n" +
                   "        .when()\n" +
                   "            .put(\"/users/1\")\n" +
                   "        .then()\n" +
                   "            .statusCode(200)\n" +
                   "            .contentType(ContentType.JSON)\n" +
                   "            .extract().response();";
        } else if (description.contains("delete") && description.contains("request")) {
            return "Response response = given()\n" +
                   "            .header(\"Content-Type\", \"application/json\")\n" +
                   "        .when()\n" +
                   "            .delete(\"/users/1\")\n" +
                   "        .then()\n" +
                   "            .statusCode(204)\n" +
                   "            .extract().response();";
        } else if (description.contains("verify") && description.contains("response")) {
            return "// Verify response\n" +
                   "        org.junit.Assert.assertEquals(\"Verification failed: \" + \"" + 
                   step.getExpectedResult() + "\", 200, response.getStatusCode());";
        } else {
            return "// TODO: Implement step: " + step.getDescription();
        }
    }
    
    /**
     * Converts a test case name to a Java class name.
     *
     * @param name The test case name
     * @return The Java class name
     */
    private String toClassName(String name) {
        // Remove non-alphanumeric characters and split by whitespace
        String[] words = name.replaceAll("[^a-zA-Z0-9\\s]", "").split("\\s+");
        
        // Capitalize each word and join
        StringBuilder className = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                className.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    className.append(word.substring(1));
                }
            }
        }
        
        return className.toString();
    }
    
    /**
     * Converts a test case name to a Java method name.
     *
     * @param name The test case name
     * @return The Java method name
     */
    private String toMethodName(String name) {
        // Convert to class name first
        String className = toClassName(name);
        
        // Convert first character to lowercase
        if (!className.isEmpty()) {
            return Character.toLowerCase(className.charAt(0)) + 
                   (className.length() > 1 ? className.substring(1) : "");
        }
        
        return "test";
    }
}
