package com.api.hub.exception;

/**
 * Exception thrown when the AI-Agent Framework encounters configuration-related issues.
 * <p>
 * This class represents errors arising from missing, invalid, or conflicting configuration parameters,
 * typically during application startup or initialization. These errors are categorized under the
 * <b>6000–6999</b> range.
 * </p>
 *
 * <p><b>Error Codes Handled:</b></p>
 * <ul>
 *   <li><b>6001</b> - CONFIG_NOT_FOUND: Required config missing.</li>
 *   <li><b>6002</b> - INVALID_CONFIG_VALUE: Config value is invalid or unsupported.</li>
 *   <li><b>6003</b> - MISSING_ENV_VARIABLE: Expected environment variable not set.</li>
 *   <li><b>6004</b> - UNSUPPORTED_DEPLOYMENT_MODE: Invalid or unsupported deployment profile.</li>
 *   <li><b>6005</b> - YAML_PARSE_ERROR: Configuration file contains syntax errors.</li>
 *   <li><b>6006</b> - SECURITY_POLICY_MISSING: Required security policy is not configured.</li>
 *   <li><b>6007</b> - MODULE_NOT_ENABLED: Attempted to use a module that is not enabled.</li>
 *   <li><b>6008</b> - INVALID_SECRET_FORMAT: Secret or credential is malformed or improperly formatted.</li>
 *   <li><b>6009</b> - CONFIG_CONFLICT: Two or more configuration values are in conflict.</li>
 *   <li><b>6010</b> - VERSION_MISMATCH: Incompatible or unsupported version specified.</li>
 * </ul>
 *
 * <p><b>Typical Usage:</b></p>
 * <ul>
 *   <li>Thrown when required configuration files or properties are missing.</li>
 *   <li>Thrown when configuration files contain invalid values or syntax.</li>
 *   <li>Thrown when environment-specific variables are not set correctly.</li>
 * </ul>
 *
 * <p>This exception promotes consistent handling of configuration failures and helps provide
 * detailed error diagnostics during the bootstrapping and deployment phases.</p>
 *
 * <p><b>Example:</b></p>
 * <pre>{@code
 * if (System.getenv("AGENT_SECRET_KEY") == null) {
 *     throw new ConfigurationException("6003", "Missing required environment variable: AGENT_SECRET_KEY", "Server misconfiguration. Please contact support.");
 * }
 * }</pre>
 *
 * @see ApiHubException
 * @since 1.0
 */
public class ConfigurationException extends ApiHubException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new ConfigurationException with the specified error code, internal message,
     * and user-friendly message.
     *
     * @param errorCode      A specific configuration error code (6001–6010)
     * @param exceptionMsg   Detailed technical description for debugging
     * @param msgToUser      Message suitable for end-user or API consumers
     */
    public ConfigurationException(String errorCode, String exceptionMsg, String msgToUser) {
        super(errorCode, exceptionMsg, msgToUser);
    }

    /**
     * Returns a string representation of the exception, including the error code and message.
     *
     * @return string with error code and exception message
     */
    @Override
    public String toString() {
        return "ConfigurationException [errorCode=" + errorCode + ", exceptionMsg=" + exceptionMsg + "]";
    }
}