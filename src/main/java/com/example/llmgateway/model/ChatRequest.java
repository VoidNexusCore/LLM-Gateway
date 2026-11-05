package com.example.llmgateway.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

public record ChatRequest(
        @NotBlank String model,
        @NotEmpty List<Message> messages,
        Boolean stream,
        Double temperature,
        Integer max_tokens,
        Map<String, Object> extra
) {
    public record Message(
            @NotBlank String role,
            String content
    ) {}
}
