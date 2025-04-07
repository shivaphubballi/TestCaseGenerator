package com.testgen.exception;

/**
 * Exception thrown by TestGen library components.
 */
public class TestGenException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructs a new TestGenException with null as its detail message.
     */
    public TestGenException() {
        super();
    }
    
    /**
     * Constructs a new TestGenException with the specified detail message.
     *
     * @param message The detail message
     */
    public TestGenException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new TestGenException with the specified detail message and cause.
     *
     * @param message The detail message
     * @param cause The cause
     */
    public TestGenException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructs a new TestGenException with the specified cause.
     *
     * @param cause The cause
     */
    public TestGenException(Throwable cause) {
        super(cause);
    }
}
