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
            throw new GenericException("9001-api-hub","Unexpected request failure " + e.getMessage(), "");
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
            throw new GenericException("9001-api-hub","HTTP error: " + e.getStatusCode() + e.getMessage(),"");
        } catch (ResourceAccessException e) {
            throw mapToCustomException(e);
        } catch (RestClientException e) {
            throw new GenericException("9001-api-hub","Unexpected request failure " + e.getMessage(), "");
        }
    }
    
    private ApiHubException mapToCustomException(ResourceAccessException e) {
        Throwable cause = e.getCause();

        if (cause instanceof java.net.UnknownHostException) {
            return new NetworkOrTimeoutException("7005-api-hub", "DNS resolution failed" + e.getMessage() , "");
        } else if (cause instanceof java.net.ConnectException) {
            return new NetworkOrTimeoutException("7003-api-hub","Host unavailable or firewall blocked "+ e.getMessage(), "");
        } else if (cause instanceof java.net.SocketTimeoutException) {
            return new NetworkOrTimeoutException("7002-api-hub","Request timeout "+ e.getMessage(), "");
        } else if (cause instanceof javax.net.ssl.SSLHandshakeException) {
            return new NetworkOrTimeoutException("7004-api-hub","SSL handshake failed "+ e.getMessage(), "");
        } else if (e.getMessage().contains("timed out")) {
            return new NetworkOrTimeoutException("7002-api-hub","Network timeout "+ e.getMessage(), "");
        } else if (e.getMessage().contains("Connection reset")) {
            return new NetworkOrTimeoutException("7007-api-hub","Connection reset "+ e.getMessage(), "");
        } else if (e.getMessage().contains("Too many follow-up requests")) {
            return new NetworkOrTimeoutException("7008-api-hub","Too many redirects "+ e.getMessage(), "");
        } else if (e.getMessage().contains("proxy")) {
            return new NetworkOrTimeoutException("7006","Proxy error" + e.getMessage(), "");
        }

        return new NetworkOrTimeoutException("7001-api-hub","Network unreachable"+ e.getMessage(), "");
    }
    
    public ApiHubException wrap(HttpStatusCodeException ex) {
        int statusCode = ex.getStatusCode().value();
        String errorMessage = ex.getMessage();

        // Map status or content to error category
        if (statusCode == 401 || statusCode == 403) {
            return new AuthenticationException("2001-api-hub", errorMessage, "Authentication failed while calling external service.");
        } else if (statusCode >= 500 && statusCode < 600) {
            return new APICallException("3002-api-hub", errorMessage, "External service is currently unavailable.");
        } else if (statusCode == 404) {
            return new APICallException("3001-api-hub", errorMessage, "Requested API endpoint not found.");
        } else if (statusCode == 400) {
            return new InputException("1010-api-hub", errorMessage, "Bad request made to external service.");
        } else if (statusCode == 408 || statusCode == 504) {
            return new APICallException("3004-api-hub", errorMessage, "External service request timed out.");
        } else {
            // Fallback case
            return new APICallException("3003-api-hub", errorMessage, "An unknown error occurred while calling an external API.");
        }
    }

}

