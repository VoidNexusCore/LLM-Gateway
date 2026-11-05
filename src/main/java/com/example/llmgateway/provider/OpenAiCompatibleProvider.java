package com.example.llmgateway.provider;

import com.example.llmgateway.model.ChatRequest;
import com.example.llmgateway.model.ChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class OpenAiCompatibleProvider implements ModelProvider {

    private final WebClient webClient;
    private final String apiKey;
    private final String defaultModel;

    public OpenAiCompatibleProvider(
            WebClient.Builder builder,
            @Value("${llm.upstream.base-url}") String baseUrl,
            @Value("${llm.upstream.api-key:}") String apiKey,
            @Value("${llm.upstream.default-model}") String defaultModel) {
        this.webClient = builder.baseUrl(baseUrl).build();
        this.apiKey = apiKey;
        this.defaultModel = defaultModel;
    }

    @Override
    public Mono<ChatResponse> chat(ChatRequest request) {
        return webClient.post()
                .uri("/chat/completions")
                .headers(headers -> {
                    if (apiKey != null && !apiKey.isBlank()) {
                        headers.setBearerAuth(apiKey);
                    }
                })
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(withResolvedModel(request, false))
                .retrieve()
                .bodyToMono(ChatResponse.class);
    }

    @Override
    public Flux<String> stream(ChatRequest request) {
        return webClient.post()
                .uri("/chat/completions")
                .headers(headers -> {
                    if (apiKey != null && !apiKey.isBlank()) {
                        headers.setBearerAuth(apiKey);
                    }
                })
                .accept(MediaType.TEXT_EVENT_STREAM)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(withResolvedModel(request, true))
                .retrieve()
                .bodyToFlux(String.class);
    }

    @Override
    public Map<String, Object> models() {
        return Map.of(
                "object", "list",
                "data", java.util.List.of(Map.of("id", defaultModel, "object", "model"))
        );
    }

    private Map<String, Object> withResolvedModel(ChatRequest request, boolean stream) {
        Map<String, Object> body = new java.util.LinkedHashMap<>();
        body.put("model", request.model() == null || request.model().isBlank()
                ? defaultModel : request.model());
        body.put("messages", request.messages());
        body.put("stream", stream);
        if (request.temperature() != null) body.put("temperature", request.temperature());
        if (request.max_tokens() != null) body.put("max_tokens", request.max_tokens());
        if (request.extra() != null) body.putAll(request.extra());
        return body;
    }
}
