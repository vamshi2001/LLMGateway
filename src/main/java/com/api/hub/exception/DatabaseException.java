package com.api.hub.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;

import com.mongodb.ErrorCategory;
import com.mongodb.MongoCommandException;
import com.mongodb.MongoException;
import com.mongodb.MongoQueryException;
import com.mongodb.MongoTimeoutException;
import com.mongodb.MongoWriteException;

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
    
    
 // MongoDB Exception Mapping
    public static DatabaseException fromMongoException(Throwable ex, String context) {
        if (ex instanceof MongoTimeoutException) {
            return new DatabaseException("4001-mongodb-gateway", ex.getMessage(), context);
        }

        if (ex instanceof MongoWriteException) {
            MongoWriteException writeEx = (MongoWriteException) ex;
            if (writeEx.getError().getCategory() == ErrorCategory.DUPLICATE_KEY) {
                return new DatabaseException("4007-mongodb-gateway", ex.getMessage(), context);
            }
            return new DatabaseException("4002-mongodb-gateway", ex.getMessage(), context);
        }

        if (ex instanceof MongoQueryException) {
            return new DatabaseException("4005-mongodb-gateway", ex.getMessage(), context);
        }

        if (ex instanceof MongoCommandException) {
            return new DatabaseException("4009-mongodb-gateway", ex.getMessage(), context);
        }

        if (ex instanceof MongoException) {
            return new DatabaseException("4008-mongodb-gateway", ex.getMessage(), context);
        }

        return new DatabaseException("4000-mongodb-gateway", ex.getMessage(), context);
    }

    // Spring JDBC Exception Mapping
    public static DatabaseException fromJdbcException(Throwable ex, String context) {
        if (ex instanceof CannotGetJdbcConnectionException) {
            return new DatabaseException("4001-sqldb-gateway", ex.getMessage(), context);
        }

        if (ex instanceof DuplicateKeyException) {
            return new DatabaseException("4007-sqldb-gateway", ex.getMessage(), context);
        }

        if (ex instanceof DataIntegrityViolationException) {
            return new DatabaseException("4009-sqldb-gateway", ex.getMessage(), context);
        }

        if (ex instanceof QueryTimeoutException) {
            return new DatabaseException("4005-sqldb-gateway", ex.getMessage(), context);
        }

        if (ex instanceof DataAccessException) {
            return new DatabaseException("4008-sqldb-gateway", ex.getMessage(), context);
        }

        return new DatabaseException("4000-sqldb-gateway", ex.getMessage(), context);
    }
}
