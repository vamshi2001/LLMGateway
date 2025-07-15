package com.api.hub.gateway.service.impl;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.api.hub.exception.ApiHubException;
import com.api.hub.exception.GenericException;
import com.api.hub.exception.InputException;
import com.api.hub.gateway.cache.Cache;
import com.api.hub.gateway.model.ChatHistory;
import com.api.hub.gateway.model.GatewayRequest;
import com.api.hub.gateway.model.JsoupExtractionConfig;
import com.api.hub.gateway.service.SearchService;

import dev.langchain4j.web.search.WebSearchEngine;
import dev.langchain4j.web.search.WebSearchOrganicResult;
import dev.langchain4j.web.search.WebSearchRequest;
import dev.langchain4j.web.search.WebSearchResults;
import dev.langchain4j.web.search.google.customsearch.GoogleCustomWebSearchEngine;
import jakarta.annotation.PostConstruct;

@Component("websearch")
public class WebSearchService implements SearchService{

	/*
	 * @Value("${websearch.maxcharacters.aimsg}") private Integer maxCharInAiMsg;
	 * 
	 * @Value("${websearch.maxcharacters.result}") private Integer
	 * maxCharactersResults;
	 */
	
	@Value("${google.search.api-key}")
    private String apiKey;

    @Value("${google.search.csi}")
    private String csi;

    @Value("${google.search.site-restrict:false}")
    private Boolean siteRestrict;

    @Value("${google.search.include-images:false}")
    private Boolean includeImages;

    @Value("${google.search.timeout:5s}")
    private Duration timeout;

    @Value("${google.search.max-retries:3}")
    private Integer maxRetries;

    @Value("${google.search.log-requests:false}")
    private Boolean logRequests;

    @Value("${google.search.log-responses:false}")
    private Boolean logResponses;
    
    @Value("${google.search.request.max-results:5}")
    private int maxResults;

    @Value("${google.search.request.language:en}")
    private String language;

    @Value("${google.search.request.geo-location:IN}")
    private String geoLocation;

    @Value("${google.search.request.start-page:1}")
    private int startPage;

    @Value("${google.search.request.start-index:1}")
    private int startIndex;

    @Value("${google.search.request.safe-search:true}")
    private boolean safeSearch;
	
	private	WebSearchEngine searchEngine;
	
	@Autowired
	@Qualifier("JsoupExtractionConfigCache")
	private Cache<String,JsoupExtractionConfig> cache;
	
	Set<String> hosts;
	
	@PostConstruct
	public void init() {
		searchEngine = GoogleCustomWebSearchEngine.builder()
                .apiKey(apiKey)
                .csi(csi)
                .siteRestrict(siteRestrict)
                .includeImages(includeImages)
                .timeout(timeout)
                .maxRetries(maxRetries)
                .logRequests(logRequests)
                .logResponses(logResponses)
                .build();
		
		hosts = cache.keys();
	}
	
	@Override
	public String getData(GatewayRequest req) throws ApiHubException {
		
		String userMessage = req.getUserMessage();
		if(userMessage == null || userMessage.isBlank()) {
			throw new InputException("1002-websearch-gateway", "UserMessage is empty", "User query is required to perform websearch");
		}
		try {
			/*
			 * ChatHistory history = null; if(chatHistory != null && chatHistory.size() > 0)
			 * { history = chatHistory.get(chatHistory.size()-1); }
			 */
			
			/*String botMsg = "";
			if(history!=null && history.getAiMessage()!= null) {
				if(history.getAiMessage().length() > maxCharInAiMsg) {
					botMsg = history.getAiMessage().substring(0,maxCharInAiMsg);
				}else {
					botMsg = history.getAiMessage();
				}
			}*/
			
			String msg = userMessage ;
			
			WebSearchRequest request = WebSearchRequest.builder()
	        .searchTerms(msg)
	        .maxResults(maxResults)
	        .language(language)
	        .geoLocation(geoLocation)
	        .startPage(startPage)
	        .startIndex(startIndex)
	        .safeSearch(safeSearch)
	        .build();
			
			WebSearchResults webresult =  null;
			
			
			webresult = searchEngine.search(request);
			
			String str = "";
			for(WebSearchOrganicResult result : webresult.results()) {
				
				Document doc = null;
				if(hosts.contains(result.url().getHost())) {
					doc = Jsoup.connect(result.url().toString()).get();
					List<String> extractedTexts = extractTextFromPage(doc, cache.get(result.url().getHost()));
					str += StringUtils.join(extractedTexts, "\n");
				}
			}
			return str;
		}catch (Exception e) {
			throw new GenericException("9001-websearch-gateway", e.getMessage(), "unable to retrieve information from websearch");
		}
		
	}
	
	public List<String> extractTextFromPage( Document doc, JsoupExtractionConfig config) throws IOException {
	    
	    List<String> extractedTexts = new ArrayList<>();
	    
	    for (JsoupExtractionConfig.ExtractionRule rule : config.getRules()) {
	        Elements elements = switch (rule.getType()) {
	            case "id" -> {
	                Element el = doc.getElementById(rule.getValue());
	                yield el != null ? new Elements(el) : new Elements();
	            }
	            case "tag" -> doc.getElementsByTag(rule.getValue());
	            case "class" -> doc.getElementsByClass(rule.getValue());
	            case "selector" -> doc.select(rule.getValue());
	            default -> throw new IllegalArgumentException("Unknown rule type: " + rule.getType());
	        };

	        if (rule.isAll()) {
	            for (Element el : elements) {
	                extractedTexts.add(el.text());
	            }
	        } else if (!elements.isEmpty()) {
	            extractedTexts.add(elements.get(0).text());
	        }
	    }
	    return extractedTexts;
	}

}
