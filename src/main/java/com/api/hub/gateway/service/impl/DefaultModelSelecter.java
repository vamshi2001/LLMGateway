package com.api.hub.gateway.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.api.hub.exception.ApiHubException;
import com.api.hub.exception.InternalServerException;
import com.api.hub.gateway.constants.ChatType;
import com.api.hub.gateway.model.GatewayRequest;
import com.api.hub.gateway.model.GatewayResponse;
import com.api.hub.gateway.model.Model;
import com.api.hub.gateway.provider.helper.Provider;
import com.api.hub.gateway.service.ModelSelecter;
import com.api.hub.gateway.service.impl.LLMModelsHolder.AvalibleModel;

import dev.langchain4j.model.chat.response.ChatResponse;

@Component
public class DefaultModelSelecter implements ModelSelecter{
	
	@Autowired
	private LLMModelsHolder holder;
	
	@Override
	public GatewayResponse getResponse(GatewayRequest request) throws ApiHubException {
		
		
		Integer maxFallBack = request.getPersonaProps().getMaxFallBackModels();
		List<String> modelsToSkip = request.getSkipModels();
		do {
			AvalibleModel avalible = getModel(request);
			if(avalible == null || !avalible.isAvailable()) {
				continue;
			}
			Model model = avalible.getModel();
			GatewayResponse res = null;
			try {
				
				Provider provider = holder.getProvider(model.getProvider());
				request.setModelName(model.getModelId());
				
				if(request.isPrompt()) {
					
				}else if(request.isEmbed()) {
					res = provider.getEmbeddingResponse(request);
				}else if(request.isModeration()) {
					
				}else if(request.getUserMessage() != null) {
					res = provider.getChatResponse(request);
				}else if(request.getUserImage() != null) {
					
				}
				
				holder.compute(model.getModelId(), res, avalible);
				
				return res;
			}catch (Exception e) {
				modelsToSkip.add(model.getModelId());
				holder.failed(avalible);
				e.printStackTrace();
			}
		}while(maxFallBack-- > 0);
		
		throw new InternalServerException("8001-ai-gateway", "failed to find model for given request", "unable to find a model for given request");
	}
	
	public AvalibleModel getModel(GatewayRequest request) {
		Set<String> modelsList = null;
		
		List<String> finialModels;
		if(request.isPrompt()) {
			
		}else if(request.isEmbed()) {
			modelsList = holder.getModels(ChatType.EMBEDDING);
		}else if(request.isModeration()) {
			
		}else if(request.getUserMessage() != null) {
			modelsList = holder.getModels(ChatType.CHAT);
		}else if(request.getUserImage() != null) {
			
		}
		
		if(modelsList == null || modelsList.size() < 1) {
			return null;
		}
		
		List<String> modelsToSkip = request.getSkipModels();
		if(modelsToSkip.size() > 0) {
			modelsList = modelsList
					.stream()
					.filter(e -> !modelsToSkip.contains(e))
					.collect(Collectors.toSet());
		}
		
		
		if(modelsList.size() < 1) {
			
			return null;
		}
		
		Set<String> topicSupportedModels = new HashSet<String>();
		if(request.getPersona() != null && !request.getPersona().isBlank()) {
			
			Set<String> modelsListTmp = holder.getTopicSupportedModels(request.getPersona());
			if(modelsListTmp != null && modelsListTmp.size() > 0) {
				topicSupportedModels.addAll(modelsListTmp);
			}
			
		}
		
		if(topicSupportedModels.size() > 1) {
			modelsList = modelsList
					.stream()
					.filter(e -> topicSupportedModels.contains(e))
					.collect(Collectors.toSet());
		}
		
		if(modelsList == null || modelsList.size() < 1) {
			return null;
		}
		
		finialModels = modelsList.stream().sorted(holder.getSortingFunction()).collect(Collectors.toList());
		
		//Arrays.sort(modelsList.toArray(new String[0]), modelSortFun);
		
		for(String modelId : finialModels) {
			AvalibleModel modelSelected = holder.getIfModelAvailable(modelId);
			if(modelSelected.isAvailable()) {
				return modelSelected;
			}
		}
		
		return null;
	}
	
	
	

}
