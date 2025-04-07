package com.testgen.exception;

/**
 * Exception thrown by the TestGen library when an error occurs.
 */
public class TestGenException extends RuntimeException {
    /**
     * Creates a new TestGenException with the specified message.
     *
     * @param message The error message
     */
    public TestGenException(String message) {
        super(message);
    }

    /**
     * Creates a new TestGenException with the specified message and cause.
     *
     * @param message The error message
     * @param cause   The cause of the exception
     */
    public TestGenException(String message, Throwable cause) {
        super(message, cause);
    }
}
