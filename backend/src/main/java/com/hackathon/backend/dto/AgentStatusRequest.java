package com.hackathon.backend.dto;

import com.hackathon.backend.entity.AgentStatus;
import jakarta.validation.constraints.NotNull;

public record AgentStatusRequest(
        @NotNull AgentStatus status
) {}