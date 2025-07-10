package com.api.hub.exception;

public class NetworkOrTimeoutException extends ApiHubException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new NetworkTimeoutException with the specified error code, internal message,
     * and user-friendly message.
     *
     * @param errorCode    Specific network timeout error code (e.g., NET-5001)
     * @param exceptionMsg Detailed technical description for debugging
     * @param msgToUser    Message suitable for end-user or API consumers
     */
    public NetworkOrTimeoutException(String errorCode, String exceptionMsg, String msgToUser) {
        super(errorCode, exceptionMsg, msgToUser);
    }

    @Override
    public String toString() {
        return "NetworkOrTimeoutException [errorCode=" + errorCode + ", exceptionMsg=" + exceptionMsg + "]";
    }
}
