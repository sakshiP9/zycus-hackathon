package com.hackathon.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record OrderRequest(
        @NotBlank String id,
        @NotBlank String description,
        @NotBlank String assignedAgentId
) {}
