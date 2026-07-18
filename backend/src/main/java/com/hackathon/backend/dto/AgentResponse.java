package com.hackathon.backend.dto;

import com.hackathon.backend.entity.AgentStatus;

public record AgentResponse(
        String id,
        String name,
        int activeOrderCount,
        AgentStatus status
) {}