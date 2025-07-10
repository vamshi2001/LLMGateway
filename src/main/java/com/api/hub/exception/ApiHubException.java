package com.api.hub.exception;

/**
 * Base abstract exception class for the AI-Agent Framework.
 * <p>
 * All custom exceptions thrown by AI agents, agent services, workflows, or system modules 
 * within the framework must extend this class. It provides a consistent structure for 
 * error handling, logging, and user-facing messages.
 * </p>
 *
 * <p><b>Key Features:</b></p>
 * <ul>
 *   <li>Holds a unique error code to identify the type or category of exception.</li>
 *   <li>Maintains an internal exception message for system logs and debugging.</li>
 *   <li>Provides a user-friendly message to return to frontend or external clients.</li>
 *   <li>Automatically captures and exposes the exception stack trace as a string.</li>
 * </ul>
 *
 * <p><b>Usage in AI-Agent Framework:</b></p>
 * <ul>
 *   <li>All AI agents and their handlers should throw subclasses of <code>ApiHubException</code> 
 *   for consistent error handling and logging.</li>
 *   <li>This promotes centralized management of fault conditions across data processing,
 *   AI execution flows, and integrations.</li>
 *   <li>Enables the framework to map errors to appropriate HTTP status codes, retries, or fallback agents.</li>
 * </ul>
 *
 * <p><b>Example:</b></p>
 * <pre>{@code
 * public class AgentExecutionException extends ApiHubException {
 *     public AgentExecutionException(String errorCode, String internalMsg, String userMsg) {
 *         super(errorCode, internalMsg, userMsg);
 *     }
 * }
 * }</pre>
 *

 * @since 1.0
 */
public abstract class ApiHubException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Unique error code for identifying exception category.
     */
    protected String errorCode;

    /**
     * Detailed internal exception message for logs and debugging.
     */
    protected String exceptionMsg;

    /**
     * User-facing message to provide a friendly explanation or fallback text.
     */
    protected String msgToUser;

    /**
     * Captured stack trace of the exception in string format.
     */
    protected String stackTrace = this.getStackTrace().toString();

    /**
     * Constructs a new ApiHubException with the specified details.
     *
     * @param errorCode     the error code that uniquely identifies the exception type
     * @param exceptionMsg  detailed internal message for system logging
     * @param msgToUser     message intended to be shown to users or clients
     */
    public ApiHubException(String errorCode, String exceptionMsg, String msgToUser) {
        super();
        this.errorCode = errorCode;
        this.exceptionMsg = exceptionMsg;
        this.msgToUser = msgToUser;
    }

    /**
     * Returns the captured stack trace of the exception as a string.
     *
     * @return string representation of the exception stack trace
     */
    public String getStacktrace() {
        return stackTrace;
    }

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getExceptionMsg() {
		return exceptionMsg;
	}

	public void setExceptionMsg(String exceptionMsg) {
		this.exceptionMsg = exceptionMsg;
	}

	public String getMsgToUser() {
		return msgToUser;
	}

	public void setMsgToUser(String msgToUser) {
		this.msgToUser = msgToUser;
	}

	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}
}