package com.hackathon.backend.util;

import com.hackathon.backend.entity.Agent;
import com.hackathon.backend.entity.Order;
import com.hackathon.backend.entity.TriggerReason;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PromptBuilder {

    public String build(Order order, List<Agent> availableAgents, TriggerReason triggerReason) {
        return switch (triggerReason) {
            case INITIAL -> buildInitialPrompt(order, availableAgents);
            case AGENT_OFFLINE -> buildReplanPrompt(order, availableAgents);
        };
    }

    private String buildInitialPrompt(Order order, List<Agent> availableAgents) {
        return """
            You are assigning a delivery order to the best available agent.

            Order: %s (%s)

            Available agents:
            %s

            Respond with ONLY a JSON object, no other text:
            {"agentId": "...", "confidence": 0.0-1.0, "reasoning": "..."}
            """.formatted(order.getId(), order.getDescription(), formatAgents(availableAgents));
    }

    private String buildReplanPrompt(Order order, List<Agent> availableAgents) {
        return """
            RECOVERY SITUATION: An agent has gone offline mid-shift, and this order
            is now stranded and needs urgent reassignment. Any previous assignment
            for this order is void.

            Stranded order: %s (%s)

            Currently available agents:
            %s

            Respond with ONLY a JSON object, no other text:
            {"agentId": "...", "confidence": 0.0-1.0, "reasoning": "..."}
            """.formatted(order.getId(), order.getDescription(), formatAgents(availableAgents));
    }

    private String formatAgents(List<Agent> agents) {
        return agents.stream()
                .map(a -> "- %s: %d active orders".formatted(a.getId(), a.getActiveOrderCount()))
                .collect(Collectors.joining("\n"));
    }
}
