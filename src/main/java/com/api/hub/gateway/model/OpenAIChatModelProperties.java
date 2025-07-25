package com.api.hub.gateway.model;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dev.langchain4j.model.chat.Capability;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.chat.request.json.JsonSchema;

public record OpenAIChatModelProperties(
		String provider,
		String modelType,
        String baseUrl,
        String apiKey,
        String organizationId,
        String projectId,
        String modelName,
        Double temperature,
        Double topP,
        List<String> stop,
        Integer maxTokens,
        Integer maxCompletionTokens,
        Double presencePenalty,
        Double frequencyPenalty,
        Map<String, Integer> logitBias,
        String responseFormat,
        Set<Capability> supportedCapabilities,
        Boolean strictJsonSchema,
        Integer seed,
        String user,
        Boolean strictTools,
        Boolean parallelToolCalls,
        Boolean store,
        Map<String, String> metadata,
        String serviceTier,
        String reasoningEffort,
        Duration timeout,
        Integer maxRetries,
        Boolean logRequests,
        Boolean logResponses,
        Map<String, String> customHeaders
) {
}