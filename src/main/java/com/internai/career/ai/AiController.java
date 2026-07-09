package com.internai.career.ai;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/ai")
public class AiController {
    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @GetMapping(value = "/resume/optimize", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> resumeOptimize(@RequestParam Long resumeId) {
        return aiService.resumeOptimize(resumeId);
    }

    @GetMapping(value = "/job/recommend", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> jobRecommend(@RequestParam Long studentId) {
        return aiService.jobRecommend(studentId);
    }

    @GetMapping(value = "/interview", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> interview(@RequestParam(required = false) Long jobId, @RequestParam(defaultValue = "") String answer) {
        return aiService.interview(jobId, answer);
    }

    @GetMapping(value = "/log/summary", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> logSummary(@RequestParam Long studentId) {
        return aiService.logSummary(studentId);
    }

    @GetMapping(value = "/kb/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> kbChat(@RequestParam String question) {
        return aiService.kbChat(question);
    }
}
