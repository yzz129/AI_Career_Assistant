package com.internai.career.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class OpenAiCompatibleClientTest {
    private final OpenAiCompatibleClient client = new OpenAiCompatibleClient(
            "https://api.deepseek.com",
            "test-key",
            "deepseek-v4-flash",
            false,
            1024,
            new ObjectMapper()
    );

    @Test
    void extractsContentFromDeepSeekSseChunk() throws Exception {
        String chunk = "data: {\"choices\":[{\"delta\":{\"content\":\"你好\",\"role\":\"assistant\"}}]}";

        assertEquals("你好", client.extractContent(chunk));
    }

    @Test
    void ignoresDoneKeepAliveAndUsageChunks() throws Exception {
        assertNull(client.extractContent("data: [DONE]"));
        assertNull(client.extractContent(": keep-alive"));
        assertNull(client.extractContent("data: {\"choices\":[],\"usage\":{\"total_tokens\":8}}"));
    }
}
