package com.api.hub.exception;

public class AuthenticationException extends ApiHubException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new AuthenticationException with the specified error code, internal message,
     * and user-friendly message.
     *
     * @param errorCode    Specific authentication error code (e.g., AUTH-1001)
     * @param exceptionMsg Detailed technical description for debugging
     * @param msgToUser    Message suitable for end-user or API consumers
     */
    public AuthenticationException(String errorCode, String exceptionMsg, String msgToUser) {
        super(errorCode, exceptionMsg, msgToUser);
    }

    @Override
    public String toString() {
        return "AuthenticationException [errorCode=" + errorCode + ", exceptionMsg=" + exceptionMsg + "]";
    }
}
