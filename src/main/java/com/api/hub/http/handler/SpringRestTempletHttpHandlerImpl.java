package com.api.hub.http.handler;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.routing.DefaultProxyRoutePlanner;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.BackOffExecution;
import org.springframework.web.client.RestTemplate;

import com.api.hub.exception.APICallException;
import com.api.hub.exception.ApiHubException;
import com.api.hub.exception.AuthenticationException;
import com.api.hub.exception.InternalServerException;
import com.api.hub.exception.NetworkOrTimeoutException;
import com.api.hub.gateway.constants.MarkerConstants;
import com.api.hub.http.HttpHandler;
import com.api.hub.http.HttpRequest;
import com.api.hub.http.ResponseHolder;
import com.api.hub.http.url.HostReslover;

import lombok.extern.slf4j.Slf4j;

@Component("SpringRestTempletHttpHandlerImpl")
@Scope(value = "prototype")
@Slf4j
@ConditionalOnBean(value =  SpringRestTempletHandler.class)
public class SpringRestTempletHttpHandlerImpl implements HttpHandler, MarkerConstants{

	@Autowired
	private Environment env;
	
	private RestTemplate restTemplate;
	
	@Autowired
	private SpringRestTempletHandler handler;
	
	@Override
	public void init(String name) {
		
		int maxTotalConnections = env.getProperty("http.rt."+name+".maxTotalConnections", Integer.class);
		int maxPerRoute = env.getProperty("http.rt."+name+".maxPerRoute", Integer.class);
		long connectTimeoutMs = env.getProperty("http.rt."+name+".connectTimeoutMs", Long.class);
		long readTimeoutMs = env.getProperty("http.rt."+name+".readTimeoutMs", Long.class);
		long connectionRequestTimeoutMs = env.getProperty("http.rt."+name+".connectionRequestTimeoutMs", Long.class);
		boolean proxyEnabled = env.getProperty("http.rt."+name+".proxy.enables", Boolean.class, false);
		HttpHost httpProxy = null;
		if(proxyEnabled) {
			String host = env.getProperty("http.rt."+name+".proxy.http.host","");
			String port = env.getProperty("http.rt."+name+".proxy.http.port","");
			if(host!=null && !host.isBlank()) {
				httpProxy = new HttpHost("http", host, Integer.parseInt(port));
			}
			String httpsHost = env.getProperty("http.rt."+name+".proxy.https.host","");
			String httpsPort = env.getProperty("http.rt."+name+".proxy.https.port","");
			if(httpsHost!=null && !httpsHost.isBlank()) {
				httpProxy = new HttpHost("http", httpsHost, Integer.parseInt(httpsPort));
			}
		}
		
		// === 1. Setup Connection Manager ===
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        connManager.setMaxTotal(maxTotalConnections);
        connManager.setDefaultMaxPerRoute(maxPerRoute);

        // Optional: deeper socket and connection configuration
        SocketConfig socketConfig = SocketConfig.custom()
                .setSoKeepAlive(true)
                .setSoTimeout(Timeout.ofMilliseconds(readTimeoutMs))
                .build();
        connManager.setDefaultSocketConfig(socketConfig);

        ConnectionConfig connectionConfig = ConnectionConfig.custom()
        		.setConnectTimeout(Timeout.ofMilliseconds(connectTimeoutMs))
                .build();
        
        connManager.setDefaultConnectionConfig(connectionConfig);

        // === 2. Timeout and Proxy Config ===
        RequestConfig requestConfig = RequestConfig.custom()
                .setResponseTimeout(Timeout.ofMilliseconds(readTimeoutMs))
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(connectionRequestTimeoutMs))
                .build();

        // === 3. Build HttpClient ===
        HttpClientBuilder clientBuilder  = HttpClientBuilder.create()
                .setConnectionManager(connManager)
                .setDefaultRequestConfig(requestConfig);
        
        if(proxyEnabled) {
        	clientBuilder.setRoutePlanner(new DefaultProxyRoutePlanner(httpProxy));
        }
        CloseableHttpClient client = clientBuilder.build();

        // === 4. Create Request Factory and RestTemplate ===
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(client);

        restTemplate = new RestTemplate(factory);
	}
	
	@Override
	public <Res, Req> HttpRequest<Res, Req> createRequest(Class<Res> responseType, Class<Req> requestType)
			throws ApiHubException {
		
		return new HttpRequest<Res,Req>(responseType, requestType);
	}
	@Override
	public <Res, Req> Future<ResponseHolder<Res>> sendRequest(HttpRequest<Res, Req> request) throws ApiHubException {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return processRequest(request);
			} catch (ApiHubException e) {
				// TODO Auto-generated catch block
				return new ResponseHolder<Res>(false, e, null);
			}
		});
		
	}
	private <Res, Req> ResponseHolder<Res> processRequest(HttpRequest<Res, Req> request) throws ApiHubException{
		if (request.getAuthetication() == null) {
			throw new InputException("gateway-1002-api", "Authentication handler is missing in HttpRequest", "Request setup error: Authentication handler not provided.");
		}
		if (request.getAuthetication().getHeaders() == null) {
			throw new InputException("gateway-1002-api", "Headers from authentication handler are missing in HttpRequest", "Request setup error: Headers from authentication handler not provided.");
		}
		request.setHeaders(request.getAuthetication().getHeaders()); // Must be called after null checks

		if (request.getHostReslover() == null) {
			throw new InputException("gateway-1002-api", "Host resolver is missing in HttpRequest", "Request setup error: Host resolver not provided.");
		}
		HostReslover hosts = request.getHostReslover();
		Iterator<String> hostsIter = hosts.getIter();
		if (hostsIter == null) {
		    throw new InternalServerException("gateway-8002-api", "Host resolver returned a null iterator", "Internal error: Host resolver provided invalid data.");
		}

		if (request.getHttpMethod() == null) {
            throw new InputException("gateway-1002-api", "HTTP method is missing in HttpRequest", "Request setup error: HTTP method not provided.");
        }
        String endpoint = getEndpoint(request);

		if(request.isBackOffEnabled()) {
			if (request.getBackoff() == null) {
				throw new InputException("gateway-1002-api", "BackOff strategy is missing in HttpRequest when backOff is enabled", "Request setup error: BackOff strategy not provided.");
			}
			BackOff backOff = request.getBackoff();
			BackOffExecution execution = backOff.start();
			long next = execution.nextBackOff();
			while(BackOffExecution.STOP != next) {
				if(!hostsIter.hasNext()) {
					break;
				}
				
				String url = hostsIter.next();
				try {
					ResponseEntity<Res> response = handler.process(url+endpoint, request.getRequestBody(), request.getSpringHeaders(), restTemplate, request.getResponseClass(), request.getHttpMethod());
					return new ResponseHolder<Res>(true, null, response);
				} catch (ApiHubException e) {
					if(e instanceof AuthenticationException) { // Handled by SpringRestTempletHandler.wrap
						throw e;
					}else if (e instanceof APICallException) {
						// Specific APICallExceptions that should stop retries (e.g., 404 Not Found)
						if ("gateway-3001-api".equals(e.getErrorCode())) { // Already updated format
							throw e;
						}
						// Other APICallExceptions might be retriable (e.g. temporary server errors)
					} else if (e instanceof NetworkOrTimeoutException) {
						// Network errors are generally retriable
					}
					// For other ApiHubException types, or APICallExceptions not specifically handled above, log and continue retry
					log.error(EXTERNAL_API,"Error occurred while calling API " + url + ", error code: " + (e.getErrorCode() != null ? e.getErrorCode() : "N/A") + ". Retrying if possible.", e);
				}

				if (BackOffExecution.STOP == next) { // Check if it was the last attempt
					break;
				}

				try {
					Thread.sleep(next);
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
					throw new InternalServerException("gateway-8005-api", "Thread interrupted during backoff delay while calling " + url, "The operation was interrupted.", ie);
				}
				next = execution.nextBackOff();
			}
			// If loop finishes, all retries failed
			throw new NetworkOrTimeoutException("gateway-7002-api", "Unable to call service "  + hosts.getAllURLS() + " after multiple retries.", "The external service is currently unavailable after multiple attempts.", null);
			
		}else { // No backoff enabled
			try {
				String url = hostsIter.next();
				ResponseEntity<Res> response = handler.post(url+endpoint, request.getRequestBody(), request.getSpringHeaders(), restTemplate, request.getResponseClass());
				return new ResponseHolder<Res>(true, null, response);
			} catch (ApiHubException e) {
				throw e;
			}
		}
	}

	private <Res, Req> String getEndpoint(HttpRequest<Res, Req> request) {
		String pathParams = request.getPathParams();
		pathParams = (pathParams == null || pathParams.length() < 1) ? "" : "/"+pathParams;
		Map<String, String> queryParams = request.getQueryParams();
		if(queryParams!=null && queryParams.size() > 0) {
			StringBuilder queryString = new StringBuilder("?");
			for(Entry<String, String> var : queryParams.entrySet()) {
				queryString.append(var.getKey()+"="+var.getValue());
			}
			return pathParams + queryString.toString();
		}
		return pathParams;
	}

	@Override
	public <Res> ResponseHolder<Res> getResponse(Future<ResponseHolder<Res>> future) throws ApiHubException {
		try {
			ResponseHolder<Res> result =  future.get(); // This can throw ExecutionException or InterruptedException
			if(result.isSuccess()) {
				return result;
			}else {
				// If result indicates failure, it should contain the ApiHubException
				if (result.getException() != null) {
					throw result.getException();
				}
                // Fallback if exception is somehow not set in ResponseHolder on failure
                throw new InternalServerException("gateway-8001-api", "Request processing failed, but no specific exception was provided in ResponseHolder.", "An unknown error occurred while processing your request.");
			}
		} catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn(ASYNC_PROCESS_WARN, "Future.get() was interrupted",e);
            throw new InternalServerException("gateway-8005-api", "Request future was interrupted: " + e.getMessage(), "The operation was interrupted.", e);
        }catch (ExecutionException e) {
			Throwable cause = e.getCause();
			if(cause instanceof ApiHubException) {
				throw (ApiHubException) cause;
			} else if (cause instanceof Exception) {
			    log.error(ASYNC_PROCESS_ERROR,"Unwrapped exception from ExecutionException was not an ApiHubException.", cause);
                throw new InternalServerException("gateway-8001-api", "An unexpected error occurred during asynchronous execution: " + cause.getMessage(), "An internal error occurred.", (Exception)cause);
			} else {
			    log.error(ASYNC_PROCESS_ERROR,"ExecutionException cause was not an Exception.", e);
                throw new InternalServerException("gateway-8001-api", "An unexpected and critical error occurred during asynchronous execution: " + e.getMessage(), "An internal error occurred.", e);
			}
		}
	}
	
}
