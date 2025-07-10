package com.api.hub.gateway.constants;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * MarkerConstants defines a set of standardized SLF4J {@link Marker} instances
 * used for categorizing and filtering logs throughout the application.
 * <p>
 * These markers help improve log readability and facilitate log analysis
 * and monitoring by associating log messages with specific application domains
 * such as security, performance, API integration, business logic, etc.
 * <p>
 * Example usage:
 * <pre>
 *     logger.info(MarkerConstants.SECURITY, "User login attempt");
 *     logger.error(MarkerConstants.VALIDATION_ERROR, "Invalid input provided");
 * </pre>
 */
public interface MarkerConstants {

    // ---------------------- Security-related logs ----------------------

    /** Marker for general security-related log messages. */
    Marker SECURITY = MarkerFactory.getMarker("SECURITY");

    /** Marker for authentication events (login attempts, token generation, etc.). */
    Marker AUTH = MarkerFactory.getMarker("AUTH");

    /** Marker for failed authentication attempts. */
    Marker AUTH_FAILURE = MarkerFactory.getMarker("AUTH_FAILURE");

    /** Marker for access denial events (e.g., unauthorized resource access). */
    Marker ACCESS_DENIED = MarkerFactory.getMarker("ACCESS_DENIED");

    // ---------------------- Performance-related logs ----------------------

    /** Marker for general performance-related logs. */
    Marker PERFORMANCE = MarkerFactory.getMarker("PERFORMANCE");

    /** Marker for identifying slow requests or operations. */
    Marker SLOW_REQUEST = MarkerFactory.getMarker("SLOW_REQUEST");

    // ---------------------- Error handling ----------------------

    /** Marker for general error logging. */
    Marker ERROR = MarkerFactory.getMarker("ERROR");

    /** Marker for validation-related errors (e.g., invalid input). */
    Marker VALIDATION_ERROR = MarkerFactory.getMarker("VALIDATION_ERROR");

    /** Marker for system or internal server errors. */
    Marker SYSTEM_ERROR = MarkerFactory.getMarker("SYSTEM_ERROR");

    // ---------------------- API & External Integrations ----------------------

    /** Marker for internal API-related operations. */
    Marker API = MarkerFactory.getMarker("API");

    /** Marker for external API calls (e.g., third-party integrations). */
    Marker EXTERNAL_API = MarkerFactory.getMarker("EXTERNAL_API");

    /** Marker for database operations. */
    Marker DATABASE = MarkerFactory.getMarker("DATABASE");

    /** Marker for cache operations (e.g., Redis, in-memory cache). */
    Marker CACHE = MarkerFactory.getMarker("CACHE");

    // ---------------------- Business logic ----------------------

    /** Marker for general business logic processing. */
    Marker BUSINESS = MarkerFactory.getMarker("BUSINESS");

    /** Marker for workflow-related operations. */
    Marker WORKFLOW = MarkerFactory.getMarker("WORKFLOW");

    // ---------------------- Debugging and Diagnostics ----------------------

    /** Marker for debug-level log messages. */
    Marker DEBUG = MarkerFactory.getMarker("DEBUG");

    /** Marker for diagnostic logs, useful in troubleshooting. */
    Marker DIAGNOSTIC = MarkerFactory.getMarker("DIAGNOSTIC");

    /** Marker for trace-level logs, offering fine-grained logging detail. */
    Marker TRACE = MarkerFactory.getMarker("TRACE");

    // ---------------------- Notifications and Alerts ----------------------

    /** Marker for system alerts or alarms. */
    Marker ALERT = MarkerFactory.getMarker("ALERT");

    /** Marker for user or system-generated notifications. */
    Marker NOTIFICATION = MarkerFactory.getMarker("NOTIFICATION");

    // ---------------------- Audit logs ----------------------

    /** Marker for audit logging (e.g., tracking user or admin actions). */
    Marker AUDIT = MarkerFactory.getMarker("AUDIT");

    // ---------------------- Scheduled jobs / Background Processing ----------------------

    /** Marker for scheduled or background job execution. */
    Marker JOB = MarkerFactory.getMarker("JOB");

    /** Marker for general background task processing. */
    Marker BACKGROUND_TASK = MarkerFactory.getMarker("BACKGROUND_TASK");

    // ---------------------- User Activity ----------------------

    /** Marker for tracking user actions and interactions. */
    Marker USER_ACTIVITY = MarkerFactory.getMarker("USER_ACTIVITY");
}