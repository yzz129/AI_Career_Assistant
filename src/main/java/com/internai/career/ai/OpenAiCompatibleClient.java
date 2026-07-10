package com.internai.career.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.internai.career.common.BizException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class OpenAiCompatibleClient {
    private final WebClient webClient;
    private final String apiKey;
    private final String model;
    private final boolean thinkingEnabled;
    private final int maxTokens;
    private final ObjectMapper objectMapper;

    public OpenAiCompatibleClient(@Value("${ai.base-url:https://api.deepseek.com}") String baseUrl,
                                  @Value("${ai.api-key:}") String apiKey,
                                  @Value("${ai.model:deepseek-v4-flash}") String model,
                                  @Value("${ai.thinking-enabled:false}") boolean thinkingEnabled,
                                  @Value("${ai.max-tokens:1024}") int maxTokens,
                                  ObjectMapper objectMapper) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
        this.apiKey = apiKey == null ? "" : apiKey.trim();
        this.model = model;
        this.thinkingEnabled = thinkingEnabled;
        this.maxTokens = maxTokens;
        this.objectMapper = objectMapper;
    }

    public boolean isConfigured() {
        return !apiKey.isBlank();
    }

    public String getModel() {
        return model;
    }

    public Flux<String> stream(String prompt) {
        if (!isConfigured()) {
            return Flux.error(new BizException("未配置 DEEPSEEK_API_KEY，无法调用 DeepSeek"));
        }
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", model);
        body.put("stream", true);
        body.put("thinking", Map.of("type", thinkingEnabled ? "enabled" : "disabled"));
        body.put("max_tokens", maxTokens);
        body.put("messages", List.of(Map.of("role", "user", "content", prompt)));

        return webClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchangeToFlux(response -> {
                    if (response.statusCode().isError()) {
                        return response.bodyToMono(String.class)
                                .defaultIfEmpty("无响应内容")
                                .flatMapMany(message -> Flux.error(new BizException(
                                        "DeepSeek API 调用失败（" + response.statusCode().value() + "）：" + message)));
                    }
                    return response.bodyToFlux(String.class);
                })
                .flatMapIterable(chunk -> List.of(chunk.split("\\R")))
                .handle((line, sink) -> {
                    try {
                        String content = extractContent(line);
                        if (content != null && !content.isEmpty()) {
                            sink.next(content);
                        }
                    } catch (Exception ex) {
                        sink.error(new BizException("DeepSeek 流式响应解析失败：" + ex.getMessage()));
                    }
                })
                .cast(String.class)
                .timeout(Duration.ofSeconds(120));
    }

    String extractContent(String rawLine) throws Exception {
        if (rawLine == null || rawLine.isBlank()) {
            return null;
        }
        String line = rawLine.trim();
        if (line.startsWith("data:")) {
            line = line.substring(5).trim();
        }
        if (line.isEmpty() || "[DONE]".equals(line) || line.startsWith(":")) {
            return null;
        }
        JsonNode root = objectMapper.readTree(line);
        JsonNode choices = root.path("choices");
        if (!choices.isArray() || choices.isEmpty()) {
            return null;
        }
        JsonNode content = choices.get(0).path("delta").path("content");
        return content.isTextual() ? content.asText() : null;
    }
}
