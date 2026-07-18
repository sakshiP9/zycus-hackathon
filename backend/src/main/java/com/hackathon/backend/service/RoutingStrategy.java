package com.hackathon.backend.service;

import com.hackathon.backend.dto.RoutingRecommendation;
import com.hackathon.backend.entity.Agent;
import com.hackathon.backend.entity.Order;
import com.hackathon.backend.entity.TriggerReason;

import java.util.List;

public interface RoutingStrategy {

    List<RoutingRecommendation> recommend(
            Order order,
            List<Agent> availableAgents,
            TriggerReason triggerReason
    );
}
