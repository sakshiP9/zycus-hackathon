package com.hackathon.backend.dto;

public record RoutingRecommendation(
        String agentId,
        double confidence,
        String reasoning
) {}
