package com.api.hub.exception;

public class GenericException extends ApiHubException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new GenericException with the specified error code, internal message,
     * and user-friendly message.
     *
     * @param errorCode    Specific generic error code (e.g., GEN-6001)
     * @param exceptionMsg Detailed technical description for debugging
     * @param msgToUser    Message suitable for end-user or API consumers
     */
    public GenericException(String errorCode, String exceptionMsg, String msgToUser) {
        super(errorCode, exceptionMsg, msgToUser);
    }

    @Override
    public String toString() {
        return "GenericException [errorCode=" + errorCode + ", exceptionMsg=" + exceptionMsg + "]";
    }
}
