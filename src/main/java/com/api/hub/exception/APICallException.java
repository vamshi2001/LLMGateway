package com.api.hub.exception;

public class APICallException extends ApiHubException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new APICallException with the specified error code, internal message,
     * and user-friendly message.
     *
     * @param errorCode    Specific API call error code (e.g., API-2001)
     * @param exceptionMsg Detailed technical description for debugging
     * @param msgToUser    Message suitable for end-user or API consumers
     */
    public APICallException(String errorCode, String exceptionMsg, String msgToUser) {
        super(errorCode, exceptionMsg, msgToUser);
    }

    @Override
    public String toString() {
        return "APICallException [errorCode=" + errorCode + ", exceptionMsg=" + exceptionMsg + "]";
    }
}
