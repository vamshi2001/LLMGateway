package com.api.hub.gateway.service.impl;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.api.hub.gateway.model.ChatHistory;
import com.api.hub.gateway.service.SearchService;

import dev.langchain4j.web.search.WebSearchEngine;
import dev.langchain4j.web.search.WebSearchOrganicResult;
import dev.langchain4j.web.search.WebSearchRequest;
import dev.langchain4j.web.search.WebSearchResults;
import dev.langchain4j.web.search.google.customsearch.GoogleCustomWebSearchEngine;
import jakarta.annotation.PostConstruct;

@Component("websearch")
public class WebSearchService implements SearchService{

	@Value("${websearch.maxcharacters.aimsg}")
	private Integer maxCharInAiMsg;
	
	@Value("${websearch.maxcharacters.result}")
	private Integer maxCharactersResults;
	
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
	}
	
	@Override
	public String getData(String userMessage, List<ChatHistory> chatHistory) {
		ChatHistory history = null;
		if(chatHistory != null && chatHistory.size() > 0) {
			history = chatHistory.get(chatHistory.size()-1);
		}
		
		String botMsg = "";
		if(history!=null && history.getAiMessage()!= null) {
			if(history.getAiMessage().length() > maxCharInAiMsg) {
				botMsg = history.getAiMessage().substring(0,maxCharInAiMsg);
			}else {
				botMsg = history.getAiMessage();
			}
		}
		
		String msg = userMessage + botMsg;
		
		WebSearchRequest request = WebSearchRequest.builder()
        .searchTerms(msg)
        .maxResults(maxResults)
        .language(language)
        .geoLocation(geoLocation)
        .startPage(startPage)
        .startIndex(startIndex)
        .safeSearch(safeSearch)
        .build();
		
		WebSearchResults webresult = searchEngine.search(request);
		String str = "";
		for(WebSearchOrganicResult result : webresult.results()) {
			
			Document doc = null;
			try {
				doc = Jsoup.connect(result.url().toString()).get();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
             
             if(doc != null) {
            	 str += doc.body().text();
            	 if(str.length() > maxCharactersResults) {
     				str = str.substring(0, maxCharactersResults);
     				break;
     			}
             }
			
			
			
		}
		
		return str;
	}

}
