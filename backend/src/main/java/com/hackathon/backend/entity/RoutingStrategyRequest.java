package com.hackathon.backend.entity;

import jakarta.validation.constraints.NotBlank;

public record RoutingStrategyRequest(
        @NotBlank String strategy
) {}
