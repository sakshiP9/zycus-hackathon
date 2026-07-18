package com.hackathon.backend.dto;

import com.hackathon.backend.entity.SuggestionStatus;
import com.hackathon.backend.entity.TriggerReason;

import java.time.Instant;

public record SuggestionResponse(
        Long id,
        String orderId,
        String recommendedAgentId,
        double confidence,
        String reasoning,
        SuggestionStatus status,
        TriggerReason triggerReason,
        Instant createdAt
) {}