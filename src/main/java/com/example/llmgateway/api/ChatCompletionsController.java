package com.example.llmgateway.api;

import com.example.llmgateway.model.ChatRequest;
import com.example.llmgateway.provider.ModelProvider;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1")
public class ChatCompletionsController {

    private final ModelProvider modelProvider;

    public ChatCompletionsController(ModelProvider modelProvider) {
        this.modelProvider = modelProvider;
    }

    @PostMapping(value = "/chat/completions", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<?>> chat(@Valid @RequestBody ChatRequest request) {
        if (Boolean.TRUE.equals(request.stream())) {
            Flux<String> stream = modelProvider.stream(request);
            return Mono.just(ResponseEntity.ok()
                    .contentType(MediaType.TEXT_EVENT_STREAM)
                    .body(stream));
        }
        return modelProvider.chat(request).map(ResponseEntity::ok);
    }

    @GetMapping("/models")
    public Mono<ResponseEntity<?>> models() {
        return Mono.just(ResponseEntity.ok(modelProvider.models()));
    }
}
