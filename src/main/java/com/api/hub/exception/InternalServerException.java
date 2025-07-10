package com.api.hub.exception;

/**
 * Represents internal server-side exceptions that occur within the AI-Agent Framework.
 * <p>
 * This exception handles critical errors in the system, typically mapped to the error code
 * range <b>8000–8999</b>. These are not caused by user input but are internal failures 
 * that indicate bugs, infrastructure issues, or system-level anomalies.
 * </p>
 *
 * <p><b>Error Codes Managed:</b></p>
 * <ul>
 *   <li><b>8001</b> - INTERNAL_SERVER_ERROR: Unknown failure.</li>
 *   <li><b>8002</b> - NULL_POINTER_EXCEPTION: Null value accessed.</li>
 *   <li><b>8003</b> - ILLEGAL_STATE: Invalid operation state.</li>
 *   <li><b>8004</b> - TYPE_CAST_EXCEPTION: Serialization or casting error.</li>
 *   <li><b>8005</b> - THREAD_INTERRUPTED: Thread was interrupted during execution.</li>
 *   <li><b>8006</b> - STACK_OVERFLOW: Recursive or cyclic overflow.</li>
 *   <li><b>8007</b> - HEAP_MEMORY_EXCEEDED: JVM heap memory exhausted.</li>
 *   <li><b>8008</b> - RESOURCE_LEAK: Database/file/socket resource not released.</li>
 *   <li><b>8009</b> - UNSUPPORTED_OPERATION: Unimplemented or unsupported logic invoked.</li>
 *   <li><b>8010</b> - SCHEDULER_ERROR: Error in asynchronous or scheduled job execution.</li>
 * </ul>
 *
 * <p>
 * Use this exception to signal critical, unexpected, and non-recoverable issues within agents,
 * services, or infrastructure that require investigation or error alerting.
 * </p>
 *
 * <p><b>Example usage:</b></p>
 * <pre>{@code
 * if (dbConnection == null) {
 *     throw new InternalServerException("8002", "Database connection is null", "Unexpected server issue occurred.");
 * }
 * }</pre>
 *

 * @since 1.0
 * @see ApiHubException
 */
public class InternalServerException extends ApiHubException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new InternalServerException with the specified structured error code,
     * detailed developer-facing exception message, and user-facing safe message.
     *
     * @param errorCode     one of the 8000-series error codes indicating specific internal failure
     * @param exceptionMsg  technical message for logs and internal diagnostics
     * @param msgToUser     safe message to display to the user or API clients
     */
    public InternalServerException(String errorCode, String exceptionMsg, String msgToUser) {
        super(errorCode, exceptionMsg, msgToUser);
    }

    /**
     * Provides a formatted string representation of this internal server exception.
     *
     * @return a string containing the error code and technical exception message
     */
    @Override
    public String toString() {
        return "InternalServerException [errorCode=" + errorCode + ", exceptionMsg=" + exceptionMsg + "]";
    }

    /**
     * Returns the captured stack trace for diagnostics or logging.
     *
     * @return stack trace as a string
     */
    @Override
    public String getStacktrace() {
        return stackTrace;
    }
}