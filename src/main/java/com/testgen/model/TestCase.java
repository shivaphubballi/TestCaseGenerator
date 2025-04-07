package com.testgen.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a test case in the TestGen library.
 * A test case consists of a name, description, and a list of test steps.
 */
public class TestCase {
    /**
     * Enum representing the type of test case.
     */
    public enum TestType {
        WEB_UI,
        API,
        SECURITY,
        ACCESSIBILITY,
        PERFORMANCE
    }

    /**
     * Represents a step in a test case.
     */
    public static class TestStep {
        private String description;
        private String expectedResult;

        /**
         * Creates a new test step.
         *
         * @param description    The description of the step
         * @param expectedResult The expected result of the step
         */
        public TestStep(String description, String expectedResult) {
            this.description = description;
            this.expectedResult = expectedResult;
        }

        /**
         * Gets the description of the step.
         *
         * @return The description
         */
        public String getDescription() {
            return description;
        }

        /**
         * Sets the description of the step.
         *
         * @param description The description
         */
        public void setDescription(String description) {
            this.description = description;
        }

        /**
         * Gets the expected result of the step.
         *
         * @return The expected result
         */
        public String getExpectedResult() {
            return expectedResult;
        }

        /**
         * Sets the expected result of the step.
         *
         * @param expectedResult The expected result
         */
        public void setExpectedResult(String expectedResult) {
            this.expectedResult = expectedResult;
        }

        @Override
        public String toString() {
            return "TestStep{" +
                    "description='" + description + '\'' +
                    ", expectedResult='" + expectedResult + '\'' +
                    '}';
        }
    }

    private String name;
    private String description;
    private List<TestStep> steps;
    private TestType type;

    /**
     * Creates a new test case.
     *
     * @param name        The name of the test case
     * @param description The description of the test case
     * @param type        The type of the test case
     */
    /**
     * Creates a new test case with predefined steps.
     *
     * @param name        The name of the test case
     * @param description The description of the test case
     * @param steps       The steps of the test case
     * @param type        The type of the test case
     */
    public TestCase(String name, String description, List<TestStep> steps, TestType type) {
        this.name = name;
        this.description = description;
        this.steps = steps;
        this.type = type;
    }

    public TestCase(String name, String description, TestType type) {
        this.name = name;
        this.description = description;
        this.steps = new ArrayList<>();
        this.type = type;
    }

    /**
     * Gets the name of the test case.
     *
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the test case.
     *
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the description of the test case.
     *
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the test case.
     *
     * @param description The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the steps of the test case.
     *
     * @return The steps
     */
    public List<TestStep> getSteps() {
        return steps;
    }

    /**
     * Sets the steps of the test case.
     *
     * @param steps The steps
     */
    public void setSteps(List<TestStep> steps) {
        this.steps = steps;
    }

    /**
     * Adds a step to the test case.
     *
     * @param step The step to add
     */
    public void addStep(TestStep step) {
        this.steps.add(step);
    }

    /**
     * Gets the type of the test case.
     *
     * @return The type
     */
    public TestType getType() {
        return type;
    }

    /**
     * Sets the type of the test case.
     *
     * @param type The type
     */
    public void setType(TestType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "TestCase{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", steps=" + steps +
                ", type=" + type +
                '}';
    }
}
