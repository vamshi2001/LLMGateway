package com.api.hub.exception;

public class DatabaseException extends ApiHubException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new DatabaseException with the specified error code, internal message,
     * and user-friendly message.
     *
     * @param errorCode    Specific database error code (e.g., DB-3001)
     * @param exceptionMsg Detailed technical description for debugging
     * @param msgToUser    Message suitable for end-user or API consumers
     */
    public DatabaseException(String errorCode, String exceptionMsg, String msgToUser) {
        super(errorCode, exceptionMsg, msgToUser);
    }

    @Override
    public String toString() {
        return "DatabaseException [errorCode=" + errorCode + ", exceptionMsg=" + exceptionMsg + "]";
    }
}
