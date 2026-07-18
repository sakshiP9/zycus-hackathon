package com.hackathon.backend.serviceImpl;

import com.hackathon.backend.dto.RoutingRecommendation;
import com.hackathon.backend.entity.Agent;
import com.hackathon.backend.entity.Order;
import com.hackathon.backend.entity.TriggerReason;
import com.hackathon.backend.service.RoutingStrategy;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component("ruleBased")
public class RuleBasedRoutingStrategy implements RoutingStrategy {

    @Override
    public List<RoutingRecommendation> recommend(
            Order order, List<Agent> availableAgents, TriggerReason triggerReason) {

        return availableAgents.stream()
                .sorted(Comparator.comparingInt(Agent::getActiveOrderCount))
                .map(agent -> new RoutingRecommendation(
                        agent.getId(),
                        1.0, // deterministic, full confidence
                        "Selected as the available agent with the fewest active orders ("
                                + agent.getActiveOrderCount() + ")."
                ))
                .toList();
    }
}
