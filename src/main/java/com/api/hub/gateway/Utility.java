package com.api.hub.gateway;

import java.util.ArrayList;
import java.util.List;

import com.api.hub.exception.ApiHubException;
import com.api.hub.exception.InputException;
import com.api.hub.gateway.constants.ChatType;
import com.api.hub.gateway.constants.RagFlowType;
import com.api.hub.gateway.model.GatewayRequest;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentBySentenceSplitter;
import dev.langchain4j.data.segment.TextSegment;

public class Utility {

	public static ChatType getChatType(GatewayRequest request) {
		if(request.isPrompt()) {
			
		}else if(request.isEmbed()) {
			
		}else if(request.isModeration()) {
			
		}else if(request.getUserMessage() != null) {
			return ChatType.CHAT;
		}else if(request.getUserImage() != null) {
				
		}
		
		return ChatType.CHAT;
	}
	
	public static List<RagFlowType> ragFlowType(String ragSource) throws ApiHubException{
		
		if(ragSource == null || ragSource.isBlank()) {
			throw new InputException("1006-rag-gateway", "rag source is empty", "rag source is required");
		}
		String[] ragSources = (ragSource!= null) ? ragSource.split(",") : new String[0];
		
		List<RagFlowType> ragFlows = new ArrayList<RagFlowType>();
		for(String source : ragSources) {
			if(source.equals(RagFlowType.DOCUMENT_SEARCH.getLabel())) {
				ragFlows.add(RagFlowType.DOCUMENT_SEARCH);
			} else if(source.equals(RagFlowType.VECTOR_SEARCH.getLabel())) {
				ragFlows.add(RagFlowType.VECTOR_SEARCH);
			} else if(source.equals(RagFlowType.WEB_SEARCH.getLabel())) {
				ragFlows.add(RagFlowType.WEB_SEARCH);
			}
		}
		
		if(ragFlows.size() < 1) {
			throw new InputException("1005-rag-gateway", "rag source is invalid not matching with configured sources " + ragSource, "rag source is invalid please enter correct source");
		}
		return ragFlows;
	}
	
	public static List<TextSegment> generateTextSegments(List<String> allDocs,int maxSegmentSizeInChars,
            int maxOverlapSizeInChars){
		List<TextSegment> textSegments = new ArrayList<TextSegment>();
		for(String info : allDocs) {
			Document document = Document.from(info);

	        DocumentSplitter splitter = new DocumentBySentenceSplitter(maxSegmentSizeInChars, maxOverlapSizeInChars);
	        List<TextSegment> segments = splitter.split(document);
	        textSegments.addAll(segments);
		}
		
		return textSegments;
	}
}
