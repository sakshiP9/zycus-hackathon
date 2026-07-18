package com.hackathon.backend.dto;

import com.hackathon.backend.entity.OrderStatus;

import java.time.Instant;

public record OrderResponse(
        String id,
        String description,
        String assignedAgentId,
        OrderStatus status,
        Instant createdAt
) {}
