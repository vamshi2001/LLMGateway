package com.api.hub.gateway.constants;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * MarkerConstants defines a set of standardized SLF4J {@link static Marker} instances
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

    /** static Marker for general security-related log messages. */
     static Marker SECURITY = MarkerFactory.getMarker("SECURITY");

    /** static Marker for authentication events (login attempts, token generation, etc.). */
     static Marker AUTH = MarkerFactory.getMarker("AUTH");

    /** static Marker for failed authentication attempts. */
    static Marker AUTH_FAILURE = MarkerFactory.getMarker("AUTH_FAILURE");

    /** static Marker for access denial events (e.g., unauthorized resource access). */
    static Marker ACCESS_DENIED = MarkerFactory.getMarker("ACCESS_DENIED");

    // ---------------------- Performance-related logs ----------------------

    /** static Marker for general performance-related logs. */
    static Marker PERFORMANCE = MarkerFactory.getMarker("PERFORMANCE");

    /** static Marker for identifying slow requests or operations. */
    static Marker SLOW_REQUEST = MarkerFactory.getMarker("SLOW_REQUEST");

    // ---------------------- Error handling ----------------------

    /** static Marker for general error logging. */
    static Marker ERROR = MarkerFactory.getMarker("ERROR");

    /** static Marker for validation-related errors (e.g., invalid input). */
    static Marker VALIDATION_ERROR = MarkerFactory.getMarker("VALIDATION_ERROR");

    /** static Marker for system or internal server errors. */
    static Marker SYSTEM_ERROR = MarkerFactory.getMarker("SYSTEM_ERROR");

    // ---------------------- API & External Integrations ----------------------

    /** static Marker for internal API-related operations. */
    static Marker API = MarkerFactory.getMarker("API");

    /** static Marker for external API calls (e.g., third-party integrations). */
    static Marker EXTERNAL_API = MarkerFactory.getMarker("EXTERNAL_API");

    /** static Marker for database operations. */
    static Marker DATABASE = MarkerFactory.getMarker("DATABASE");

    /** static Marker for cache operations (e.g., Redis, in-memory cache). */
    static Marker CACHE = MarkerFactory.getMarker("CACHE");

    // ---------------------- Business logic ----------------------

    /** static Marker for general business logic processing. */
    static Marker BUSINESS = MarkerFactory.getMarker("BUSINESS");

    /** static Marker for workflow-related operations. */
    static Marker WORKFLOW = MarkerFactory.getMarker("WORKFLOW");

    // ---------------------- Debugging and Diagnostics ----------------------

    /** static Marker for debug-level log messages. */
    static Marker DEBUG = MarkerFactory.getMarker("DEBUG");

    /** static Marker for diagnostic logs, useful in troubleshooting. */
    static Marker DIAGNOSTIC = MarkerFactory.getMarker("DIAGNOSTIC");

    /** static Marker for trace-level logs, offering fine-grained logging detail. */
    static Marker TRACE = MarkerFactory.getMarker("TRACE");

    // ---------------------- Notifications and Alerts ----------------------

    /** static Marker for system alerts or alarms. */
    static Marker ALERT = MarkerFactory.getMarker("ALERT");

    /** static Marker for user or system-generated notifications. */
    static Marker NOTIFICATION = MarkerFactory.getMarker("NOTIFICATION");

    // ---------------------- Audit logs ----------------------

    /** static Marker for audit logging (e.g., tracking user or admin actions). */
    static Marker AUDIT = MarkerFactory.getMarker("AUDIT");

    // ---------------------- Scheduled jobs / Background Processing ----------------------

    /** static Marker for scheduled or background job execution. */
    static Marker JOB = MarkerFactory.getMarker("JOB");

    /** static Marker for general background task processing. */
    static Marker BACKGROUND_TASK = MarkerFactory.getMarker("BACKGROUND_TASK");

    // ---------------------- User Activity ----------------------

    /** static Marker for tracking user actions and interactions. */
    static Marker USER_ACTIVITY = MarkerFactory.getMarker("USER_ACTIVITY");

}