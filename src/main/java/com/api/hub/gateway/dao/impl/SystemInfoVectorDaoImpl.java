package com.api.hub.gateway.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.api.hub.exception.ApiHubException;
import com.api.hub.exception.DatabaseException;
import com.api.hub.gateway.Utility;
import com.api.hub.gateway.dao.SystemInfoVectorDao;
import com.api.hub.gateway.model.GatewayRequest;
import com.api.hub.gateway.model.GatewayResponse;
import com.api.hub.gateway.model.RagModel;
import com.api.hub.gateway.provider.helper.impl.OllamaProviderService;
import com.api.hub.vector.util.WeaviateEmbeddingStore;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import jakarta.annotation.PostConstruct;

@Component
@ConditionalOnProperty(name = "weaviateDB.sysinfo.enable", havingValue = "true")
public class SystemInfoVectorDaoImpl implements SystemInfoVectorDao{

	@Value("${weaviate.api-key}")
    private String apiKey;

    @Value("${weaviate.scheme}")
    private String scheme;

    @Value("${weaviate.host}")
    private String host;

    @Value("${weaviate.port}")
    private Integer port;

    @Value("${weaviate.grpc-enabled}")
    private Boolean useGrpcForInserts;

    @Value("${weaviate.grpc-secured}")
    private Boolean securedGrpc;

    @Value("${weaviate.grpc-port}")
    private Integer grpcPort;

    @Value("${weaviate.object-class}")
    private String objectClass;

    @Value("${weaviate.consistency-level}")
    private String consistencyLevel;

    @Value("${weaviate.metadata-keys:}")
    private String metadataKeysString;

    @Value("${weaviate.text-field:}")
    private String textFieldName;

    @Value("${weaviate.metadata-field:}")
    private String metadataFieldName;
    
    @Value("${weaviate.minScore}")
    private Double minScore;
    
    @Value("${weaviate.maxResults}")
    private Integer maxResults;

    private WeaviateEmbeddingStore store;
    
	@Override
	public String get(RagModel model) throws ApiHubException {
		
		try {
			
			EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
	                .queryEmbedding(model.getQueryVector())
	                .minScore(minScore)
	                .maxResults(maxResults)
	                .build();

	        EmbeddingSearchResult<TextSegment> result = store.search(request, model.getPersona());
	        
	       List<String> stringArr = result.matches().stream().map( e -> {
	        	return e.embedded().text();
	        }).collect(Collectors.toList());
	       
	       return StringUtils.join(stringArr, "\n");
		}catch (Exception e) {
			throw new DatabaseException("4011-vector-gateway", e.getMessage(), "Unable to fetch data from Vector DB");
		}
	}
	
	@PostConstruct
    public void init() {
        List<String> metadataKeys = Arrays.stream(metadataKeysString.split(","))
                                          .map(String::trim)
                                          .collect(Collectors.toList());

        this.store = WeaviateEmbeddingStore.builder()
                .apiKey(apiKey)
                .scheme(scheme)
                .host(host)
                .port(port)
                .useGrpcForInserts(useGrpcForInserts)
                .securedGrpc(securedGrpc)
                .grpcPort(grpcPort)
                .objectClass(objectClass)
                .avoidDups(true)
                .consistencyLevel(consistencyLevel)
                .metadataKeys(metadataKeys)
                .textFieldName(textFieldName)
                .metadataFieldName(metadataFieldName)
                .build();

    }
	
	@Autowired
	OllamaProviderService serv;
	
	@Override
	public void save(RagModel rag) throws ApiHubException {
		
		List<String> uuids = new ArrayList<String>(rag.getSegment().size());
		for(TextSegment seg : rag.getSegment()) {
			seg.metadata().put("persona", rag.getPersona());
			uuids.add(UUID.randomUUID().toString());
		}
		store.addAll(uuids, rag.getEmbedings(), rag.getSegment());
	}
}
