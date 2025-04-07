package com.testgen.model;

/**
 * Represents a client-side route change in a Single Page Application (SPA).
 * This captures the navigation between different "views" or "pages" within
 * a SPA without full page reloads.
 */
public class RouteChange {
    private String routeName;
    private String serverPath;
    private String clientPath;
    private String triggerSelector;
    private boolean requiresAuthentication;
    
    /**
     * Creates a new RouteChange without authentication requirement.
     * 
     * @param routeName The name of the route (e.g., "Home", "Products", "About")
     * @param serverPath The server-side path that would correspond to this route
     * @param clientPath The client-side path or hash fragment (e.g., "#/products")
     * @param triggerSelector The CSS selector for elements that trigger this route change
     */
    public RouteChange(String routeName, String serverPath, String clientPath, String triggerSelector) {
        this.routeName = routeName;
        this.serverPath = serverPath;
        this.clientPath = clientPath;
        this.triggerSelector = triggerSelector;
        this.requiresAuthentication = false;
    }
    
    /**
     * Creates a new RouteChange with authentication requirement specified.
     * 
     * @param routeName The name of the route
     * @param serverPath The server-side path
     * @param clientPath The client-side path
     * @param triggerSelector The trigger selector
     * @param requiresAuthentication Whether this route requires authentication
     */
    public RouteChange(String routeName, String serverPath, String clientPath, 
                       String triggerSelector, boolean requiresAuthentication) {
        this.routeName = routeName;
        this.serverPath = serverPath;
        this.clientPath = clientPath;
        this.triggerSelector = triggerSelector;
        this.requiresAuthentication = requiresAuthentication;
    }
    
    /**
     * Gets the name of the route.
     */
    public String getRouteName() {
        return routeName;
    }
    
    /**
     * Sets the name of the route.
     */
    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }
    
    /**
     * Gets the server-side path that would correspond to this route.
     */
    public String getServerPath() {
        return serverPath;
    }
    
    /**
     * Sets the server-side path that would correspond to this route.
     */
    public void setServerPath(String serverPath) {
        this.serverPath = serverPath;
    }
    
    /**
     * Gets the client-side path or hash fragment.
     */
    public String getClientPath() {
        return clientPath;
    }
    
    /**
     * Sets the client-side path or hash fragment.
     */
    public void setClientPath(String clientPath) {
        this.clientPath = clientPath;
    }
    
    /**
     * Gets the CSS selector for elements that trigger this route change.
     */
    public String getTriggerSelector() {
        return triggerSelector;
    }
    
    /**
     * Sets the CSS selector for elements that trigger this route change.
     */
    public void setTriggerSelector(String triggerSelector) {
        this.triggerSelector = triggerSelector;
    }
    
    /**
     * Gets whether this route requires authentication.
     */
    public boolean isRequiresAuthentication() {
        return requiresAuthentication;
    }
    
    /**
     * Sets whether this route requires authentication.
     */
    public void setRequiresAuthentication(boolean requiresAuthentication) {
        this.requiresAuthentication = requiresAuthentication;
    }
}
