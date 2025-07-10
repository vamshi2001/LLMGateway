package com.api.hub.exception;

public class ModelExecutionException extends ApiHubException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new ModelExecutionException with the specified error code, internal message,
     * and user-friendly message.
     *
     * @param errorCode    Specific model/AI error code (e.g., MODEL-4001)
     * @param exceptionMsg Detailed technical description for debugging
     * @param msgToUser    Message suitable for end-user or API consumers
     */
    public ModelExecutionException(String errorCode, String exceptionMsg, String msgToUser) {
        super(errorCode, exceptionMsg, msgToUser);
    }

    @Override
    public String toString() {
        return "ModelExecutionException [errorCode=" + errorCode + ", exceptionMsg=" + exceptionMsg + "]";
    }
}