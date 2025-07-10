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
import com.api.hub.exception.GenericException;
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
		String endpoint = getEndpoint(request);
		HostReslover hosts = request.getHostReslover();
		Iterator<String> hostsIter = hosts.getIter();
		request.setHeaders(request.getAuthetication().getHeaders());
		if(request.isBackOffEnabled()) {
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
					if(e instanceof AuthenticationException) {
						throw e;
					}else if (e instanceof APICallException) {
						if(e.getErrorCode().equals("3001-api-hub")) {
							throw e;
						}
					}
					log.error(EXTERNAL_API,"error occured while call api " + url );
				}
				next = execution.nextBackOff();
				try {
					Thread.sleep(next);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			throw new APICallException("3003-api-hub", "unable to call service "  + hosts.getAllURLS(), "");
			
		}else {
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
			ResponseHolder<Res> result =  future.get();
			if(result.isSuccess()) {
				return result;
			}else {
				throw new GenericException("", "failed", "");
			}
		}catch (ExecutionException e) {
			Throwable exp = e.getCause();
			if(exp instanceof ApiHubException) {
				throw (ApiHubException) exp;
			}else {
				throw new GenericException("", e.getMessage(), "");
			}
		}
		catch (Exception e) {
			throw new GenericException("", e.getMessage(), "");
		}
	}
	
}
