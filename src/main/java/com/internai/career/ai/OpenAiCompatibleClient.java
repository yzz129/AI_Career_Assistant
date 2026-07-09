package com.internai.career.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
public class OpenAiCompatibleClient {
    private final WebClient webClient;
    private final String apiKey;
    private final String model;

    public OpenAiCompatibleClient(@Value("${ai.base-url:https://api.openai.com/v1}") String baseUrl,
                                  @Value("${ai.api-key:}") String apiKey,
                                  @Value("${ai.model:gpt-4o-mini}") String model) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
        this.apiKey = apiKey;
        this.model = model;
    }

    public Flux<String> stream(String prompt) {
        Map<String, Object> body = Map.of(
                "model", model,
                "stream", true,
                "messages", List.of(Map.of("role", "user", "content", prompt))
        );
        return webClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToFlux(String.class)
                .timeout(Duration.ofSeconds(60));
    }
}
