package com.example.llmgateway.provider;

import com.example.llmgateway.model.ChatRequest;
import com.example.llmgateway.model.ChatResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface ModelProvider {
    Mono<ChatResponse> chat(ChatRequest request);
    Flux<String> stream(ChatRequest request);
    Map<String, Object> models();
}
