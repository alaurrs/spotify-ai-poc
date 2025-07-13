package com.datainsights.spotify_ai_poc.controller;

import com.datainsights.spotify_ai_poc.service.AIService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai")
public class AIController {

    private final AIService aiService;
    public AIController(AIService aiService) { this.aiService = aiService; }
    public record AIRequest(String question, String userId) {}

    @PostMapping("/ask")
    public String askQuestion(@RequestBody AIRequest request) {
        return aiService.getInsight(request.question(), request.userId());
    }
}
