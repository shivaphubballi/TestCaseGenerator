package com.testgen.model;

/**
 * Represents a web element in the TestGen library.
 * A web element contains information about an HTML element on a web page.
 */
public class WebElement {
    private String id;
    private String name;
    private String type;
    private String tagName;
    private String xpath;
    private String cssSelector;
    private String text;
    private String value;

    /**
     * Creates a new web element.
     */
    public WebElement() {
    }

    /**
     * Creates a new web element with the specified tag name.
     *
     * @param tagName The tag name of the element
     */
    public WebElement(String tagName) {
        this.tagName = tagName;
    }

    /**
     * Gets the ID of the element.
     *
     * @return The ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the ID of the element.
     *
     * @param id The ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the name attribute of the element.
     *
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name attribute of the element.
     *
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the type of the element (e.g., "text", "button", etc.).
     *
     * @return The type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the element.
     *
     * @param type The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the tag name of the element (e.g., "input", "button", etc.).
     *
     * @return The tag name
     */
    public String getTagName() {
        return tagName;
    }

    /**
     * Sets the tag name of the element.
     *
     * @param tagName The tag name
     */
    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    /**
     * Gets the XPath expression to locate the element.
     *
     * @return The XPath expression
     */
    public String getXpath() {
        return xpath;
    }

    /**
     * Sets the XPath expression to locate the element.
     *
     * @param xpath The XPath expression
     */
    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    /**
     * Gets the CSS selector to locate the element.
     *
     * @return The CSS selector
     */
    public String getCssSelector() {
        return cssSelector;
    }

    /**
     * Sets the CSS selector to locate the element.
     *
     * @param cssSelector The CSS selector
     */
    public void setCssSelector(String cssSelector) {
        this.cssSelector = cssSelector;
    }

    /**
     * Gets the text content of the element.
     *
     * @return The text content
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the text content of the element.
     *
     * @param text The text content
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Gets the value attribute of the element.
     *
     * @return The value
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value attribute of the element.
     *
     * @param value The value
     */
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "WebElement{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", tagName='" + tagName + '\'' +
                ", xpath='" + xpath + '\'' +
                ", cssSelector='" + cssSelector + '\'' +
                ", text='" + text + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
