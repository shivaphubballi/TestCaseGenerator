package com.testgen.model;

/**
 * Represents a dynamic event in a Single Page Application (SPA).
 * These events include modals, dropdowns, accordion panels,
 * and other UI components that change state through user interaction.
 */
public class DynamicEvent {
    private String eventType;
    private String triggerSelector;
    private String targetSelector;
    private String initialState;
    private String finalState;
    private String eventName;
    private String dependsOnAttribute;
    private int timeout;
    
    public DynamicEvent() {
        this.eventType = "";
        this.triggerSelector = "";
        this.targetSelector = "";
        this.initialState = "hidden";
        this.finalState = "visible";
        this.eventName = "click";
        this.timeout = 0;
    }
    
    /**
     * Gets the type of dynamic event (e.g., modal, dropdown, loading).
     */
    public String getEventType() {
        return eventType;
    }
    
    /**
     * Sets the type of dynamic event.
     */
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    
    /**
     * Gets the CSS selector for the element that triggers the event.
     */
    public String getTriggerSelector() {
        return triggerSelector;
    }
    
    /**
     * Sets the CSS selector for the element that triggers the event.
     */
    public void setTriggerSelector(String triggerSelector) {
        this.triggerSelector = triggerSelector;
    }
    
    /**
     * Gets the CSS selector for the element that is affected by the event.
     */
    public String getTargetSelector() {
        return targetSelector;
    }
    
    /**
     * Sets the CSS selector for the element that is affected by the event.
     */
    public void setTargetSelector(String targetSelector) {
        this.targetSelector = targetSelector;
    }
    
    /**
     * Gets the initial state of the target element (e.g., hidden, inactive).
     */
    public String getInitialState() {
        return initialState;
    }
    
    /**
     * Sets the initial state of the target element.
     */
    public void setInitialState(String initialState) {
        this.initialState = initialState;
    }
    
    /**
     * Gets the final state of the target element after the event (e.g., visible, active).
     */
    public String getFinalState() {
        return finalState;
    }
    
    /**
     * Sets the final state of the target element after the event.
     */
    public void setFinalState(String finalState) {
        this.finalState = finalState;
    }
    
    /**
     * Gets the DOM event name that triggers the state change (e.g., click, mouseover).
     */
    public String getEventName() {
        return eventName;
    }
    
    /**
     * Sets the DOM event name that triggers the state change.
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
    
    /**
     * Gets the attribute that determines which target is affected
     * (useful for tabbed interfaces, etc.).
     */
    public String getDependsOnAttribute() {
        return dependsOnAttribute;
    }
    
    /**
     * Sets the attribute that determines which target is affected.
     */
    public void setDependsOnAttribute(String dependsOnAttribute) {
        this.dependsOnAttribute = dependsOnAttribute;
    }
    
    /**
     * Gets the timeout in milliseconds after which the event effect ends
     * (e.g., for temporary notifications).
     */
    public int getTimeout() {
        return timeout;
    }
    
    /**
     * Sets the timeout in milliseconds after which the event effect ends.
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
