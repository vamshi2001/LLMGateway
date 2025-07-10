package com.api.hub.gateway;

import static dev.langchain4j.internal.Utils.generateUUIDFrom;
import static dev.langchain4j.internal.Utils.isNullOrBlank;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.internal.Json;
import dev.langchain4j.internal.JsonSchemaElementUtils.VisitedClassMetadata;
import dev.langchain4j.model.chat.request.json.JsonArraySchema;
import dev.langchain4j.model.chat.request.json.JsonBooleanSchema;
import dev.langchain4j.model.chat.request.json.JsonIntegerSchema;
import dev.langchain4j.model.chat.request.json.JsonNumberSchema;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.chat.request.json.JsonReferenceSchema;
import dev.langchain4j.model.chat.request.json.JsonSchemaElement;
import dev.langchain4j.model.chat.request.json.JsonStringSchema;

public class LLMToolCallUtility {
	
	private static JsonSchemaElement jsonSchemaElementForGeneralTypes(JsonNode elem, String fieldDescription, boolean areSubFieldsRequiredByDefault,
			Map<String, VisitedClassMetadata> visited) throws Exception {
		if ("string".equalsIgnoreCase(elem.path("type").asText())) {
			return JsonStringSchema.builder()
			.description(fieldDescription)
			.build();
		}
		
		if ("integer".equalsIgnoreCase(elem.path("type").asText())) {
			return JsonIntegerSchema.builder().description(fieldDescription).build();
		}
		
		if ("number".equalsIgnoreCase(elem.path("type").asText())) {
			return JsonNumberSchema.builder().description(fieldDescription).build();
		}
		
		if ("boolean".equalsIgnoreCase(elem.path("type").asText())) {
			return JsonBooleanSchema.builder().description(fieldDescription).build();
		}
		
		return jsonObjectOrReferenceSchemaFrom(elem, fieldDescription, areSubFieldsRequiredByDefault, visited, false);
	}
	

	private static JsonSchemaElement jsonSchemaElementFrom(JsonNode elem, String fieldDescription, boolean areSubFieldsRequiredByDefault,
			Map<String, VisitedClassMetadata> visited) throws Exception {
		
		boolean arrayType = elem.path("required").asText("false").equalsIgnoreCase("true");
		
		if(arrayType) {
			return JsonArraySchema.builder()
					.items(jsonSchemaElementForGeneralTypes(elem, null, areSubFieldsRequiredByDefault, visited))
					.description(fieldDescription)
					.build();
		}else {
			return jsonSchemaElementForGeneralTypes(elem, fieldDescription, areSubFieldsRequiredByDefault, visited);
		}
		
	}
	
	private static JsonSchemaElement jsonObjectOrReferenceSchemaFrom(JsonNode customNode, String description, boolean areSubFieldsRequiredByDefault,
			Map<String, VisitedClassMetadata> visited, boolean setDefinitions) throws Exception{
        if (visited.containsKey(customNode.path("type").asText())) {
            VisitedClassMetadata visitedClassMetadata = visited.get(customNode.path("type").asText());
            JsonSchemaElement jsonSchemaElement = visitedClassMetadata.jsonSchemaElement;
            if (jsonSchemaElement instanceof JsonReferenceSchema) {
                visitedClassMetadata.recursionDetected = true;
            }
            return jsonSchemaElement;
        }

        String reference = generateUUIDFrom(customNode.path("type").asText());
        JsonReferenceSchema jsonReferenceSchema =
                JsonReferenceSchema.builder().reference(reference).build();
        visited.put(customNode.path("type").asText(), new VisitedClassMetadata(jsonReferenceSchema, reference, false));

		String schemaText = customNode.path("customJson").asText();
        
        ObjectMapper mapper = new ObjectMapper();
        JsonNode customJsonNode = mapper.readTree(schemaText);
        
        Map<String, JsonSchemaElement> properties = new LinkedHashMap<>();
        List<String> required = new ArrayList<>();
        
        
        for (JsonNode elem : customJsonNode) {
            
            if (elem.path("required").asText("false").equalsIgnoreCase("true")) {
                required.add(elem.get("name").asText());
            }
            String fieldDescription = elem.path("description").asText(null);
            JsonSchemaElement jsonSchemaElement = jsonSchemaElementFrom(elem, fieldDescription, areSubFieldsRequiredByDefault, visited);
            properties.put(elem.get("name").asText(), jsonSchemaElement);
        }

        JsonObjectSchema.Builder builder = JsonObjectSchema.builder()
                .description(description)
                .addProperties(properties)
                .required(required);

        visited.get(customNode.path("type").asText()).jsonSchemaElement = builder.build();


        return builder.build();
    }
	
	public static JsonObjectSchema buildSchemaFromJson(JsonNode array) throws Exception {
        
        if (!array.isArray()) {
            throw new IllegalArgumentException("Expected a JSON array");
        }

        Map<String, JsonSchemaElement> properties = new LinkedHashMap<>();
        List<String> required = new ArrayList<>();
        Map<String, VisitedClassMetadata> visited = new LinkedHashMap<>();
        
        for (JsonNode elem : array) {
        	
        	boolean isRequired = elem.path("required").asText("false").equalsIgnoreCase("true");

            properties.put(elem.get("name").asText(), jsonSchemaElementFrom(elem, elem.path("description").asText(null), true, visited));
            if (isRequired) {
                required.add(elem.get("name").asText());
            }
        }

        Map<String, JsonSchemaElement> definitions = new LinkedHashMap<>();
        visited.forEach((clazz, visitedClassMetadata) -> {
            if (visitedClassMetadata.recursionDetected) {
                definitions.put(visitedClassMetadata.reference, visitedClassMetadata.jsonSchemaElement);
            }
        });

        if (properties.isEmpty()) {
            return null;
        }

        return JsonObjectSchema.builder()
                .addProperties(properties)
                .required(required)
                .definitions(definitions.isEmpty() ? null : definitions)
                .build();
    }
	
	 public static ToolSpecification toolSpecificationFrom(String toolName, String toolDescription, String toolArguments) {

	        try {
	        	ObjectMapper mapper = new ObjectMapper();

	            // Parse your input array
	            JsonNode node = mapper.readTree(toolArguments);
	            
	            JsonObjectSchema parameters = buildSchemaFromJson(node);
	            
	            return ToolSpecification.builder()
		                .name(toolName)
		                .description(toolDescription)
		                .parameters(parameters)
		                .build();
	            
	        }catch (Exception e) {
				// TODO: handle exception
			}
	        return null;
	       
	    }
	 
	 private static final Pattern TRAILING_COMMA_PATTERN = Pattern.compile(",(\\s*[}\\]])");
	    private static final Pattern LEADING_TRAILING_QUOTE_PATTERN = Pattern.compile("^\"|\"$");
	    private static final Pattern ESCAPED_QUOTE_PATTERN = Pattern.compile("\\\\\"");


	    private static final Type MAP_TYPE = new ParameterizedType() {

	        @Override
	        public Type[] getActualTypeArguments() {
	            return new Type[] {String.class, Object.class};
	        }

	        @Override
	        public Type getRawType() {
	            return Map.class;
	        }

	        @Override
	        public Type getOwnerType() {
	            return null;
	        }
	    };

	    /**
	     * Convert arguments to map.
	     *
	     * @param arguments json string
	     * @return map
	     */
	    public static Map<String, Object> argumentsAsMap(String arguments) {
	        if (isNullOrBlank(arguments)) {
	            return Map.of();
	        }

	        try {
	            return Json.fromJson(arguments, MAP_TYPE);
	        } catch (Exception ignored) {
	            String normalizedArguments = removeTrailingComma(normalizeJsonString(arguments));
	            return Json.fromJson(normalizedArguments, MAP_TYPE);
	        }
	    }

	    /**
	     * Removes trailing commas before closing braces or brackets in JSON strings.
	     *
	     * @param json the JSON string
	     * @return the corrected JSON string
	     */
	    static String removeTrailingComma(String json) {
	        if (json == null || json.isEmpty()) {
	            return json;
	        }
	        Matcher matcher = TRAILING_COMMA_PATTERN.matcher(json);
	        return matcher.replaceAll("$1");
	    }

	    /**
	     * Normalizes a JSON string by removing leading and trailing quotes and unescaping internal double quotes.
	     *
	     * @param arguments the raw JSON string
	     * @return the normalized JSON string
	     */
	    static String normalizeJsonString(String arguments) {
	        if (arguments == null || arguments.isEmpty()) {
	            return arguments;
	        }

	        Matcher leadingTrailingMatcher = LEADING_TRAILING_QUOTE_PATTERN.matcher(arguments);
	        String normalizedJson = leadingTrailingMatcher.replaceAll("");

	        Matcher escapedQuoteMatcher = ESCAPED_QUOTE_PATTERN.matcher(normalizedJson);
	        return escapedQuoteMatcher.replaceAll("\"");
	    }
}
