package com.testgen.ai;

/**
 * Factory class for creating AIService instances.
 * This allows for different AI service implementations to be used.
 */
public class AIServiceFactory {
    
    /**
     * Service type enum for different AI service providers or implementations.
     */
    public enum ServiceType {
        DEFAULT,
        OPEN_AI,
        CUSTOM
    }
    
    /**
     * Creates an AIService instance of the specified type.
     * 
     * @param type The type of AI service to create
     * @return An AIService instance
     */
    public static AIService createService(ServiceType type) {
        switch (type) {
            case OPEN_AI:
                // In the future, this could create an OpenAI-based service
                // return new OpenAIService(apiKey);
                return new DefaultAIService(); // Fallback to default for now
            case CUSTOM:
                // In the future, this could create a custom service
                // return new CustomAIService(config);
                return new DefaultAIService(); // Fallback to default for now
            case DEFAULT:
            default:
                return new DefaultAIService();
        }
    }
    
    /**
     * Creates a default AIService instance.
     * 
     * @return A default AIService instance
     */
    public static AIService createDefaultService() {
        return new DefaultAIService();
    }
}
