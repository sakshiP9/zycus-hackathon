package com.hackathon.backend.serviceImpl;

import com.hackathon.backend.config.RoutingStrategyConfig;
import com.hackathon.backend.dto.RoutingRecommendation;
import com.hackathon.backend.entity.*;
import com.hackathon.backend.exception.AgentNotFoundException;
import com.hackathon.backend.exception.OrderNotFoundException;
import com.hackathon.backend.repository.AgentRepository;
import com.hackathon.backend.repository.OrderRepository;
import com.hackathon.backend.repository.ReassignmentSuggestionRepository;
import com.hackathon.backend.service.ReassignmentService;
import com.hackathon.backend.service.RoutingStrategy;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class ReassignmentServiceImpl implements ReassignmentService {

    private final Map<String, RoutingStrategy> strategies; // key = bean name
    private final RoutingStrategyConfig routingStrategyConfig;
    private final AgentRepository agentRepository;
    private final ReassignmentSuggestionRepository suggestionRepository;
    private final OrderRepository orderRepository;

    public ReassignmentServiceImpl(Map<String, RoutingStrategy> strategies,RoutingStrategyConfig routingStrategyConfig,
                                   AgentRepository agentRepository, ReassignmentSuggestionRepository suggestionRepository,
                                   OrderRepository orderRepository){
        this.strategies = strategies;
        this.routingStrategyConfig = routingStrategyConfig;
        this.agentRepository = agentRepository;
        this.suggestionRepository = suggestionRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public ReassignmentSuggestion generateSuggestion(String orderId, TriggerReason triggerReason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        List<Agent> availableAgents = agentRepository.findByStatus(AgentStatus.AVAILABLE);

        RoutingStrategy strategy = activeStrategy();
        List<RoutingRecommendation> recommendations =
                strategy.recommend(order, availableAgents, triggerReason);

        RoutingRecommendation top = recommendations.get(0);

        Agent recommendedAgent = agentRepository.findById(top.agentId())
                .orElseThrow(() -> new AgentNotFoundException(top.agentId()));

        ReassignmentSuggestion suggestion = new ReassignmentSuggestion();
        suggestion.setOrder(order);
        suggestion.setRecommendedAgent(recommendedAgent);
        suggestion.setConfidence(top.confidence());
        suggestion.setReasoning(top.reasoning());
        suggestion.setStatus(SuggestionStatus.PENDING);
        suggestion.setTriggerReason(triggerReason);
        suggestion.setCreatedAt(Instant.now());

        order.markReassignmentPending();

        return suggestionRepository.saveAndFlush(suggestion);
    }


    // Spring auto-injects all RoutingStrategy beans into this map,
    // keyed by their @Component name ("ruleBased", "ai")

    private RoutingStrategy activeStrategy() {
        String name = routingStrategyConfig.get();
        RoutingStrategy strategy = strategies.get(name);
        if (strategy == null) {
            // shouldn't happen if the controller validated on write,
            // but guard anyway in case the property default is wrong at startup
            throw new IllegalStateException("Unknown routing.strategy configured: " + name);
        }
        return strategy;
    }
}
