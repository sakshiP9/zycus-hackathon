package com.hackathon.backend.serviceImpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.backend.dto.RoutingRecommendation;
import com.hackathon.backend.entity.Agent;
import com.hackathon.backend.entity.Order;
import com.hackathon.backend.entity.TriggerReason;
import com.hackathon.backend.exception.LLMCallException;
import com.hackathon.backend.mcp.LLMGateway;
import com.hackathon.backend.service.RoutingStrategy;
import com.hackathon.backend.util.PromptBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("ai")
public class AiRoutingStrategy implements RoutingStrategy {

    private final LLMGateway llmGateway;
    private final PromptBuilder promptBuilder;
    private final RuleBasedRoutingStrategy fallback;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiRoutingStrategy(LLMGateway llmGateway,PromptBuilder promptBuilder,RuleBasedRoutingStrategy fallback){
        this.llmGateway = llmGateway;
        this.promptBuilder = promptBuilder;
        this.fallback = fallback;
    }

    @Override
    public List<RoutingRecommendation> recommend(
            Order order, List<Agent> availableAgents, TriggerReason triggerReason) {

        try {
            String prompt = promptBuilder.build(order, availableAgents, triggerReason);
            String raw = callLLMSafely(prompt);
            RoutingRecommendation rec = parseAndValidate(raw, availableAgents);
            return List.of(rec);

        } catch (LLMCallException e) {
            log("AI routing failed ({}), falling back to rule-based" + e.getMessage());
            return fallback.recommend(order, availableAgents, triggerReason);
        }
    }

    private String callLLMSafely(String prompt) {
        try {
            return llmGateway.callLLM(prompt);
        } catch (Exception e) {
            // LLMGateway throws plain RuntimeException on HTTP/timeout/parse failure —
            // wrap into our own type so the catch block above is the single place
            // that knows "AI failed, use fallback"
            throw new LLMCallException("LLM call failed", e);
        }
    }

    private RoutingRecommendation parseAndValidate(String raw, List<Agent> availableAgents) {
        JsonNode node;
        try {
            node = objectMapper.readTree(raw);
        } catch (Exception e) {
            throw new LLMCallException("LLM response was not valid JSON", e);
        }

        String agentId = node.path("agentId").asText(null);
        double confidence = node.path("confidence").asDouble(0.0);
        String reasoning = node.path("reasoning").asText("");

        boolean agentExists = availableAgents.stream()
                .anyMatch(a -> a.getId().equals(agentId));

        if (agentId == null || !agentExists) {
            throw new LLMCallException("LLM returned unknown or missing agentId: " + agentId, null);
        }

        return new RoutingRecommendation(agentId, confidence, reasoning);
    }

    private void log(String message) {
        System.out.println("[AiRoutingStrategy] " + message); // swap for a real Logger
    }
}
