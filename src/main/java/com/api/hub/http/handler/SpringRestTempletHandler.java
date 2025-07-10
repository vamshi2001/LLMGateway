package com.api.hub.http.handler;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.api.hub.exception.APICallException;
import com.api.hub.exception.ApiHubException;
import com.api.hub.exception.AuthenticationException;
import com.api.hub.exception.GenericException;
import com.api.hub.exception.InputException;
import com.api.hub.exception.NetworkOrTimeoutException;
import com.api.hub.gateway.constants.MarkerConstants;

@Component("SpringRestTempletHandler")
@ConditionalOnProperty(name = "http.spring.restTemplet.enable", havingValue = "true")
public class SpringRestTempletHandler implements MarkerConstants{

	public <R> ResponseEntity<R> process(String url, Object body, HttpHeaders headers,RestTemplate restTemplate, Class<R> responseClass, HttpMethod method) throws ApiHubException {
		if(method.equals(HttpMethod.POST)) {
			return post( url, body, headers, restTemplate, responseClass);
		}else if (method.equals(HttpMethod.GET)) {
			return get(url, headers, restTemplate, responseClass);
		}
		return null;
	}
	
    /**
     * Sends a POST request to the given URL with the provided body and headers.
     *
     * @param url     target URL
     * @param body    request payload
     * @param headers optional headers (nullable)
     * @return ResponseEntity<String> containing the response
     * @throws LLMRequestException when request fails
     */
    public <R> ResponseEntity<R> post(String url, Object body, HttpHeaders headers,RestTemplate restTemplate, Class<R> responseClass) throws ApiHubException {
        try {
            HttpEntity<Object> request = new HttpEntity<>(body, headers);
            return restTemplate.exchange(url, HttpMethod.POST, request, responseClass);
        } catch (HttpStatusCodeException e) {
            throw wrap(e);
        } catch (ResourceAccessException e) {
            throw mapToCustomException(e);
        } catch (RestClientException e) {
            // This is a general client-side error not covered by HttpStatusCodeException or ResourceAccessException
            throw new APICallException("gateway-3002-api","Unexpected REST client failure: " + e.getMessage(), "An unexpected error occurred while trying to communicate with an external service.", e);
        }
    }

    /**
     * Sends a GET request to the given URL with optional headers.
     *
     * @param url     target URL
     * @param headers optional headers (nullable)
     * @return ResponseEntity<String> containing the response
     * @throws ApiHubException 
     * @throws LLMRequestException when request fails
     */
    public <R> ResponseEntity<R> get(String url, HttpHeaders headers, RestTemplate restTemplate, Class<R> responseClass) throws ApiHubException {
        try {
            HttpEntity<Void> request = new HttpEntity<>(headers);
            return restTemplate.exchange(url, HttpMethod.GET, request, responseClass);
        } catch (HttpStatusCodeException e) {
            // HttpStatusCodeException is already handled by the wrap method, which will be called if this method is part of a larger flow
            // If called directly, this ensures it's wrapped appropriately.
            throw wrap(e);
        } catch (ResourceAccessException e) {
            throw mapToCustomException(e);
        } catch (RestClientException e) {
            // This is a general client-side error not covered by HttpStatusCodeException or ResourceAccessException
            throw new APICallException("gateway-3002-api","Unexpected REST client failure: " + e.getMessage(), "An unexpected error occurred while trying to communicate with an external service.", e);
        }
    }
    
    private ApiHubException mapToCustomException(ResourceAccessException e) {
        Throwable cause = e.getCause();
        String originalMessage = e.getMessage() != null ? e.getMessage() : "No specific message";

        if (cause instanceof java.net.UnknownHostException) {
            return new NetworkOrTimeoutException("gateway-7005-api", "DNS resolution failed for host. Details: " + originalMessage , "Unable to resolve the address of the external service.", e);
        } else if (cause instanceof java.net.ConnectException) {
            return new NetworkOrTimeoutException("gateway-7003-api","Host unavailable or connection refused. Details: "+ originalMessage, "Could not connect to the external service. It may be down or a firewall is blocking the connection.", e);
        } else if (cause instanceof java.net.SocketTimeoutException) {
            return new NetworkOrTimeoutException("gateway-7002-api","Request to external service timed out. Details: "+ originalMessage, "The external service did not respond in time.", e);
        } else if (cause instanceof javax.net.ssl.SSLHandshakeException) {
            return new NetworkOrTimeoutException("gateway-7004-api","SSL handshake failed. Details: "+ originalMessage, "A security error occurred while connecting to the external service (SSL handshake failed).", e);
        } else if (originalMessage.toLowerCase().contains("timed out")) { // General timeout message check
            return new NetworkOrTimeoutException("gateway-7002-api","Network timeout. Details: "+ originalMessage, "The connection to the external service timed out.", e);
        } else if (originalMessage.toLowerCase().contains("connection reset")) {
            return new NetworkOrTimeoutException("gateway-7007-api","Connection reset. Details: "+ originalMessage, "The connection to the external service was unexpectedly closed.", e);
        } else if (originalMessage.toLowerCase().contains("too many follow-up requests") || originalMessage.toLowerCase().contains("too many redirects")) {
            return new NetworkOrTimeoutException("gateway-7008-api","Too many redirects. Details: "+ originalMessage, "The request resulted in too many redirects.", e);
        } else if (originalMessage.toLowerCase().contains("proxy")) {
            return new NetworkOrTimeoutException("gateway-7006-api","Proxy error. Details: " + originalMessage, "An error occurred with the proxy server while trying to reach the external service.", e);
        }

        // Default network error if no specific cause is matched
        return new NetworkOrTimeoutException("gateway-7001-api","Network unreachable or other network error. Details: "+ originalMessage, "A network error occurred while trying to communicate with an external service.", e);
    }
    
    public ApiHubException wrap(HttpStatusCodeException ex) {
        int statusCode = ex.getStatusCode().value();
        String responseBody = ex.getResponseBodyAsString();
        String errorMessage = "HTTP Status " + statusCode + ": " + ex.getStatusText() + ". Response: " + responseBody;
        String userMessage;

        // Map status or content to error category
        if (statusCode == 401) { // Unauthorized
            userMessage = "Authentication failed: Access denied due to invalid credentials for an external service.";
            return new AuthenticationException("gateway-2001-api", errorMessage, userMessage, ex);
        } else if (statusCode == 403) { // Forbidden
            userMessage = "Access forbidden: You do not have permission to access the requested resource from an external service.";
            return new AuthenticationException("gateway-2006-api", errorMessage, userMessage, ex);
        } else if (statusCode >= 500 && statusCode < 600) { // Server errors (5xx)
            userMessage = "External service error: The external service reported an internal error.";
            return new APICallException("gateway-3002-api", errorMessage, userMessage, ex); // EXTERNAL_SERVICE_FAILURE
        } else if (statusCode == 404) { // Not Found
            userMessage = "External resource not found: The requested resource could not be found on the external service.";
            return new APICallException("gateway-3001-api", errorMessage, userMessage, ex); // API_NOT_FOUND
        } else if (statusCode == 400) { // Bad Request
            userMessage = "Invalid request to external service: The request was malformed or contained invalid data.";
            return new InputException("gateway-1010-api", errorMessage, userMessage, ex); // MALFORMED_REQUEST (could also be 1001, 1002 etc. based on body)
        } else if (statusCode == 408 || statusCode == 504) { // Request Timeout or Gateway Timeout
             userMessage = "External service timeout: The external service did not respond in a timely manner.";
            return new APICallException("gateway-3004-api", errorMessage, userMessage, ex); // API_TIMEOUT
        } else if (statusCode == 429) { // Too Many Requests
            userMessage = "Rate limit exceeded: Too many requests made to the external service. Please try again later.";
            return new APICallException("gateway-3008-api", errorMessage, userMessage, ex); // RATE_LIMIT_EXCEEDED
        }
         else {
            // Fallback case for other 4xx errors or unhandled codes
            userMessage = "An unexpected error occurred while communicating with an external API (HTTP Status: " + statusCode + ").";
            return new APICallException("gateway-3002-api", errorMessage, userMessage, ex); // Generic EXTERNAL_SERVICE_FAILURE
        }
    }

}

