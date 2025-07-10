package com.api.hub.exception;

/**
 * Exception thrown when an AI agent or one of its services encounters input or validation errors.
 * <p>
 * This class is part of the AI-Agent Framework and is used to represent issues caused by
 * incorrect, missing, or malformed input from users or external systems. These exceptions are 
 * categorized within the error code range <b>1000–1999</b>.
 * </p>
 *
 * <p><b>Error Codes Handled:</b></p>
 * <ul>
 *   <li><b>1001</b> - INVALID_INPUT: Input format is invalid or unparsable.</li>
 *   <li><b>1002</b> - MISSING_REQUIRED_FIELD: Required field is missing.</li>
 *   <li><b>1003</b> - INVALID_DATA_TYPE: Field value type mismatch.</li>
 *   <li><b>1004</b> - VALUE_OUT_OF_RANGE: Input exceeds allowed limits.</li>
 *   <li><b>1005</b> - UNSUPPORTED_INPUT_FORMAT: Input format not accepted.</li>
 *   <li><b>1006</b> - EMPTY_INPUT: Input field or payload is empty.</li>
 *   <li><b>1007</b> - INVALID_ENUM_VALUE: Value not in allowed enum list.</li>
 *   <li><b>1008</b> - XSS_DETECTED: Potential XSS attack detected.</li>
 *   <li><b>1009</b> - JSON_PARSE_ERROR: Malformed JSON input.</li>
 *   <li><b>1010</b> - MALFORMED_REQUEST: Request structure is incorrect.</li>
 * </ul>
 *
 * <p><b>Typical Usage:</b></p>
 * <ul>
 *   <li>When a user submits a form with missing or invalid fields.</li>
 *   <li>When a JSON payload cannot be parsed due to syntax errors.</li>
 *   <li>When a request contains unexpected data types or unsupported formats.</li>
 * </ul>
 *
 * <p>This exception improves error traceability and allows the AI agent to return consistent 
 * and meaningful error responses (e.g., HTTP 400 - Bad Request) to the user or external services.</p>
 *
 * <p><b>Example:</b></p>
 * <pre>{@code
 * if (request.getAgentId() == null) {
 *     throw new InputException("1002", "Missing required field: agentId", "Agent ID must be provided.");
 * }
 * }</pre>
 *
 * @see ApiHubException

 * @since 1.0
 */
public class InputException extends ApiHubException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new {@code InputException} with the specified error code, internal message,
     * and user-facing message.
     *
     * @param errorCode     the specific input validation error code (1001–1010)
     * @param exceptionMsg  technical message for internal debugging and logs
     * @param msgToUser     message safe to show to users or API clients
     */
    public InputException(String errorCode, String exceptionMsg, String msgToUser) {
        super(errorCode, exceptionMsg, msgToUser);
    }

    /**
     * Returns a concise string representation of the exception, useful for logs and diagnostics.
     *
     * @return formatted string with error code and exception message
     */
    @Override
    public String toString() {
        return "InvalidDataException [errorCode=" + errorCode + ", exceptionMsg=" + exceptionMsg + "]";
    }

    /**
     * Returns the stack trace of the exception as a string.
     *
     * @return string-form stack trace captured at the time of the exception
     */
    @Override
    public String getStacktrace() {
        return stackTrace;
    }
}