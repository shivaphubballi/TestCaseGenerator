package com.testgen.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a test case that can be exported to various formats (Selenium, REST Assured, Jira).
 */
public class TestCase {
    private String id;
    private String name;
    private String summary;
    private String description;
    private TestType type;
    private String sourceUrl;
    private List<TestStep> steps;
    private Map<String, String> metadata;
    private String preconditions;
    private List<String> tags;
    
    /**
     * Test case types.
     */
    public enum TestType {
        WEB_UI,
        API,
        BOTH
    }
    
    /**
     * Represents a single test step.
     */
    public static class TestStep {
        private int stepNumber;
        private String action;
        private String expectedResult;
        private Map<String, String> testData;
        
        public TestStep() {
            this.testData = new LinkedHashMap<>();
        }
        
        public TestStep(int stepNumber, String action, String expectedResult) {
            this();
            this.stepNumber = stepNumber;
            this.action = action;
            this.expectedResult = expectedResult;
        }
        
        public int getStepNumber() {
            return stepNumber;
        }
        
        public void setStepNumber(int stepNumber) {
            this.stepNumber = stepNumber;
        }
        
        public String getAction() {
            return action;
        }
        
        public void setAction(String action) {
            this.action = action;
        }
        
        public String getExpectedResult() {
            return expectedResult;
        }
        
        public void setExpectedResult(String expectedResult) {
            this.expectedResult = expectedResult;
        }
        
        public Map<String, String> getTestData() {
            return testData;
        }
        
        public void setTestData(Map<String, String> testData) {
            this.testData = testData;
        }
        
        public void addTestData(String key, String value) {
            this.testData.put(key, value);
        }
    }
    
    public TestCase() {
        this.steps = new ArrayList<>();
        this.metadata = new LinkedHashMap<>();
        this.tags = new ArrayList<>();
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getSummary() {
        return summary;
    }
    
    public void setSummary(String summary) {
        this.summary = summary;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public TestType getType() {
        return type;
    }
    
    public void setType(TestType type) {
        this.type = type;
    }
    
    public String getSourceUrl() {
        return sourceUrl;
    }
    
    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }
    
    public List<TestStep> getSteps() {
        return steps;
    }
    
    public void setSteps(List<TestStep> steps) {
        this.steps = steps;
    }
    
    public void addStep(TestStep step) {
        // Set step number if not already set
        if (step.getStepNumber() <= 0) {
            step.setStepNumber(steps.size() + 1);
        }
        this.steps.add(step);
    }
    
    public void addStep(String action, String expectedResult) {
        TestStep step = new TestStep(steps.size() + 1, action, expectedResult);
        this.steps.add(step);
    }
    
    public Map<String, String> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
    
    public void addMetadata(String key, String value) {
        this.metadata.put(key, value);
    }
    
    public String getPreconditions() {
        return preconditions;
    }
    
    public void setPreconditions(String preconditions) {
        this.preconditions = preconditions;
    }
    
    public List<String> getTags() {
        return tags;
    }
    
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    
    public void addTag(String tag) {
        if (this.tags == null) {
            this.tags = new ArrayList<>();
        }
        this.tags.add(tag);
    }
    
    /**
     * Formats the test case as a Jira-compatible string.
     *
     * @return Jira-formatted test case
     */
    public String toJiraFormat() {
        StringBuilder sb = new StringBuilder();
        
        // Summary
        sb.append("h1. ").append(name).append("\n\n");
        
        // Description
        if (description != null && !description.isEmpty()) {
            sb.append("h2. Description\n").append(description).append("\n\n");
        }
        
        // Preconditions
        if (preconditions != null && !preconditions.isEmpty()) {
            sb.append("h2. Preconditions\n").append(preconditions).append("\n\n");
        }
        
        // Test Steps
        sb.append("h2. Test Steps\n");
        sb.append("||Step||Action||Expected Result||\n");
        
        for (TestStep step : steps) {
            sb.append("|").append(step.getStepNumber()).append("|")
              .append(step.getAction()).append("|")
              .append(step.getExpectedResult()).append("|\n");
        }
        
        // Metadata
        if (!metadata.isEmpty()) {
            sb.append("\nh2. Metadata\n");
            for (Map.Entry<String, String> entry : metadata.entrySet()) {
                sb.append("* ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }
        
        // Tags
        if (!tags.isEmpty()) {
            sb.append("\nh2. Tags\n");
            for (String tag : tags) {
                sb.append("* ").append(tag).append("\n");
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Create a Java method name from the test case name.
     *
     * @return A valid Java method name
     */
    public String toMethodName() {
        // Convert test case name to a valid Java method name
        String methodName = name.toLowerCase()
                .replaceAll("[^a-zA-Z0-9]", "_") // Replace non-alphanumeric with underscore
                .replaceAll("_+", "_")           // Replace multiple underscores with a single one
                .replaceAll("^_|_$", "");        // Remove leading/trailing underscores
        
        // If the result is empty or starts with a digit, prepend with "test"
        if (methodName.isEmpty() || Character.isDigit(methodName.charAt(0))) {
            methodName = "test_" + methodName;
        }
        
        return methodName;
    }
    
    @Override
    public String toString() {
        return "TestCase{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", steps=" + steps.size() +
                '}';
    }
}
