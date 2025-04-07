package com.testgen.model;

/**
 * Represents a web element on a page.
 */
public class WebElement {
    private String tagName;
    private String id;
    private String name;
    private String className;
    private String type;
    private String text;
    private String value;
    private String href;
    private String cssSelector;
    private String xpath;
    private boolean isDynamic;
    private String loadTrigger;
    private String dynamicState;
    private int loadDelay;
    
    /**
     * Creates a new web element.
     *
     * @param tagName The tag name of the element
     */
    public WebElement(String tagName) {
        this.tagName = tagName;
        this.isDynamic = false;
        this.loadDelay = 0;
    }
    
    /**
     * Gets the tag name of the element.
     */
    public String getTagName() {
        return tagName;
    }
    
    /**
     * Sets the tag name of the element.
     */
    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
    
    /**
     * Gets the ID of the element.
     */
    public String getId() {
        return id;
    }
    
    /**
     * Sets the ID of the element.
     */
    public void setId(String id) {
        this.id = id;
    }
    
    /**
     * Gets the name of the element.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the name of the element.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Gets the class name of the element.
     */
    public String getClassName() {
        return className;
    }
    
    /**
     * Sets the class name of the element.
     */
    public void setClassName(String className) {
        this.className = className;
    }
    
    /**
     * Gets the type of the element.
     */
    public String getType() {
        return type;
    }
    
    /**
     * Sets the type of the element.
     */
    public void setType(String type) {
        this.type = type;
    }
    
    /**
     * Gets the text of the element.
     */
    public String getText() {
        return text;
    }
    
    /**
     * Sets the text of the element.
     */
    public void setText(String text) {
        this.text = text;
    }
    
    /**
     * Gets the value of the element.
     */
    public String getValue() {
        return value;
    }
    
    /**
     * Sets the value of the element.
     */
    public void setValue(String value) {
        this.value = value;
    }
    
    /**
     * Gets the href attribute of the element.
     */
    public String getHref() {
        return href;
    }
    
    /**
     * Sets the href attribute of the element.
     */
    public void setHref(String href) {
        this.href = href;
    }
    
    /**
     * Gets the CSS selector for the element.
     */
    public String getCssSelector() {
        return cssSelector;
    }
    
    /**
     * Sets the CSS selector for the element.
     */
    public void setCssSelector(String cssSelector) {
        this.cssSelector = cssSelector;
    }
    
    /**
     * Gets the XPath for the element.
     */
    public String getXpath() {
        return xpath;
    }
    
    /**
     * Sets the XPath for the element.
     */
    public void setXpath(String xpath) {
        this.xpath = xpath;
    }
    
    /**
     * Indicates whether this element is loaded dynamically
     * (not present in the initial page load).
     */
    public boolean isDynamic() {
        return isDynamic;
    }
    
    /**
     * Sets whether this element is loaded dynamically.
     */
    public void setDynamic(boolean isDynamic) {
        this.isDynamic = isDynamic;
    }
    
    /**
     * Gets the trigger that causes this element to load.
     * This could be a route change, an AJAX call, or a user event.
     * Format examples: "route:products", "ajax:getUsers", "event:click"
     */
    public String getLoadTrigger() {
        return loadTrigger;
    }
    
    /**
     * Sets the trigger that causes this element to load.
     */
    public void setLoadTrigger(String loadTrigger) {
        this.loadTrigger = loadTrigger;
    }
    
    /**
     * Gets the dynamic state of the element
     * (e.g., "hidden", "visible", "loading", "loaded").
     */
    public String getDynamicState() {
        return dynamicState;
    }
    
    /**
     * Sets the dynamic state of the element.
     */
    public void setDynamicState(String dynamicState) {
        this.dynamicState = dynamicState;
    }
    
    /**
     * Gets the delay in milliseconds before this element loads
     * after its trigger.
     */
    public int getLoadDelay() {
        return loadDelay;
    }
    
    /**
     * Sets the delay in milliseconds before this element loads.
     */
    public void setLoadDelay(int loadDelay) {
        this.loadDelay = loadDelay;
    }
}
