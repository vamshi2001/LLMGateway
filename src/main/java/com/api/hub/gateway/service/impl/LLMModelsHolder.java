package com.api.hub.gateway.service.impl;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.api.hub.gateway.cache.Cache;
import com.api.hub.gateway.constants.ChatType;
import com.api.hub.gateway.constants.ProviderType;
import com.api.hub.gateway.model.GatewayResponse;
import com.api.hub.gateway.model.Model;
import com.api.hub.gateway.model.ModelMetric;
import com.api.hub.gateway.provider.helper.Provider;

import dev.langchain4j.model.output.TokenUsage;
import jakarta.annotation.PostConstruct;

@Component
@DependsOn(value = {"AllLLMModelsCache","AllLLMModelsMetricCache"})
public class LLMModelsHolder {
	
	@Autowired
	@Qualifier("AllLLMModelsCache")
	private Cache<String,Model> modelsCache;
	
	@Autowired
	@Qualifier("AllLLMModelsMetricCache")
	private Cache<String,ModelMetric> modelMetricsCache;
	
	@Autowired
	@Qualifier("ollamaProvider")
	private Provider ollama;
	
	@Autowired
	@Qualifier("openAiProvider")
	private Provider openAI;
	
	public Provider getProvider(String providerName) {
		
		if(ProviderType.OLLAMA.getLabel().equals(providerName)) {
			return ollama;
		}
		return openAI;
	}
	
	@PostConstruct
	public void init() {
		loadModels(modelsCache);
	}
	
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	
	private Map<ChatType,Set<String>> modelsData = new HashMap<ChatType, Set<String>>();
	private Map<String,Set<String>> topicsData = new HashMap<String, Set<String>>();
	private Map<String,Boolean> modelAvailability = new ConcurrentHashMap<String, Boolean>();
	
	public Set<String> getModels(ChatType chatType){
		lock.readLock().lock();
		Set<String> result = modelsData.get(chatType);
		lock.readLock().unlock();
		return result;
	}
	
	public Set<String> getTopicSupportedModels(String topic){
		lock.readLock().lock();
		Set<String> result = topicsData.get(topic);
		lock.readLock().unlock();
		return result;
	}
	
	void loadModels(Cache<String,Model> data) {
		
		//modelsData = new Concurrent
		lock.writeLock().lock();
		try {
		
			for(String key : data.keys()) {
				Model model = data.get(key);
				if(!model.isEnable()){
					continue;
				}
				
				List<String> modelSupportedtopics = model.getTopics();
				if(modelSupportedtopics != null && modelSupportedtopics.size() > 0) {
					for(String supportedTopic : modelSupportedtopics) {
						Set<String> suppportedModels = topicsData.get(supportedTopic);
						if( suppportedModels == null) {
							suppportedModels = new HashSet<String>();
							topicsData.put(supportedTopic, suppportedModels);
						}
						suppportedModels.add(key);
					}
					
				}
				
				if(model.getType().equals(ChatType.CHAT.getLabel())) {
					Set<String> modelList = modelsData.get(ChatType.CHAT);
					if(modelList == null) {
						modelList = new HashSet<String>();
						modelsData.put(ChatType.CHAT, modelList);
					}
					modelList.add(key);
				}else if(model.getType().equals(ChatType.LANGUAGE.getLabel())) {
					Set<String> modelList = modelsData.get(ChatType.LANGUAGE);
					if(modelList == null) {
						modelList = new HashSet<String>();
						modelsData.put(ChatType.LANGUAGE, modelList);
					}
					modelList.add(key);
				}else if(model.getType().equals(ChatType.EMBEDDING.getLabel())) {
					Set<String> modelList = modelsData.get(ChatType.EMBEDDING);
					if(modelList == null) {
						modelList = new HashSet<String>();
						modelsData.put(ChatType.EMBEDDING, modelList);
					}
					modelList.add(key);
				}else if(model.getType().equals(ChatType.MODERATION.getLabel())) {
					Set<String> modelList = modelsData.get(ChatType.MODERATION);
					if(modelList == null) {
						modelList = new HashSet<String>();
						modelsData.put(ChatType.MODERATION, modelList);
					}
					modelList.add(key);
				}else if(model.getType().equals(ChatType.IMAGE.getLabel())) {
					Set<String> modelList = modelsData.get(ChatType.IMAGE);
					if(modelList == null) {
						modelList = new HashSet<String>();
						modelsData.put(ChatType.IMAGE, modelList);
					}
					modelList.add(key);
				}
			}
			
			for(String key : data.keys()) {
				Model model = data.get(key);
				if(!model.isEnable()){
					continue;
				}
				modelAvailability.put(key, isAvailable(model, modelMetricsCache.get(key)));
			}
		
		}finally {
			lock.writeLock().unlock();
		}
		
	}
	public void compute(String id, GatewayResponse res, AvalibleModel avalible) {
		TokenUsage usage = null;
		if(res.getChatResponse() != null) {
			usage = res.getChatResponse().tokenUsage();
		}else if(res.getEmbeddingResponse() != null) {
			usage = res.getEmbeddingResponse().tokenUsage();
		}else {
			avalible.unlock();
			return;
		}
		ModelMetric modelMetric = avalible.getMetrics();
		if(usage != null) {
			
			modelMetric.setCurrentInputTokenConsumedPerDay(usage.inputTokenCount());
			modelMetric.setCurrentInputTokenConsumedPerMonth(usage.inputTokenCount());
			
			modelMetric.setCurrentOutputTokenConsumedPerDay(usage.outputTokenCount());
			modelMetric.setCurrentOutputTokenConsumedPerMonth(usage.outputTokenCount());
		}
		
		
		if(!isAvailable(avalible.getModel(), modelMetric)) {
			if(modelMetric.equals(modelMetricsCache.get(id))){
				modelAvailability.replace(id, false);
			}
		}
		
		avalible.unlock();
	}
	public void failed(AvalibleModel avalible) {
		
		avalible.getMetrics().setTotalFailuresToday(1);
		avalible.unlock();
	}
	
	public boolean isAvailableById(String id) {
		return modelAvailability.get(id);
	}
	
	public boolean isAvailable(Model model, ModelMetric modelMetric) {
		if(model.getMaxTokenDay() <= 
				(modelMetric.getCurrentInputTokenConsumedPerDay().get() + modelMetric.getCurrentOutputTokenConsumedPerDay().get())) {
			return false;
		}
		
		if(model.getMaxTokenMonth() <= 
				(modelMetric.getCurrentInputTokenConsumedPerMonth().get() + modelMetric.getCurrentOutputTokenConsumedPerMonth().get())) {
			return false;
		}
		
		if(model.getMaxRequestDay() <= modelMetric.getRequestPerDay().get() ){
			return false;
		}
		
		if(model.getMaxRequestMonth() <= modelMetric.getRequestPerMonth().get()) {
			return false;
		}
		
		return true;
	}
	
	public AvalibleModel getIfModelAvailable(String modelId) {
		AvalibleModel avalible = new AvalibleModel();
		Model model = modelsCache.get(modelId);
		ModelMetric modelMetric = modelMetricsCache.get(modelId);
		
		if(isAvailableById(modelId)) {
			long currentRequestCount = modelMetric.setRequestPerDay(1);
			if(model.getMaxRequestDay() < currentRequestCount){
				modelAvailability.replace(modelId, false);
				avalible.setAvailable(false);
				return null;
			}
			long currentRequestCountMonth = modelMetric.setRequestPerMonth(1);
			if(model.getMaxRequestMonth() < currentRequestCountMonth) {
				modelAvailability.replace(modelId, false);
				modelMetric.setRequestPerDay(-1);
				modelMetric.setRequestPerMonth(-1);
				avalible.setAvailable(false);
				return null;
			}
			avalible.setMetrics(modelMetric);
			avalible.setModel(model);
			avalible.setAvailable(true);
			avalible.lock();
			return avalible;
		}
		avalible.setAvailable(false);
		return avalible;
	}
	
	Comparator<String> modelSortFun = new Comparator<String>() {
	    @Override
	    public int compare(String m1, String m2) {
	    	
	        return Float.compare(modelsCache.get(m1).getRank(), modelsCache.get(m2).getRank()); // ascending
	    }
	};
	
	public Comparator<String> getSortingFunction(){
		return modelSortFun;
	}
	
	public class AvalibleModel{
		private Model model;
		private ModelMetric metrics;
		
		private boolean isAvailable = false;
		
		public void lock() {
			metrics.setCurrentActiveRequest(1);
		}
		public void unlock() {
			metrics.setCurrentActiveRequest(-1);
		}
		public Model getModel() {
			return model;
		}

		public void setModel(Model model) {
			this.model = model;
		}

		public ModelMetric getMetrics() {
			return metrics;
		}

		public void setMetrics(ModelMetric metrics) {
			this.metrics = metrics;
		}

		public boolean isAvailable() {
			return isAvailable;
		}

		public void setAvailable(boolean isAvailable) {
			this.isAvailable = isAvailable;
		}
	}
	
}
