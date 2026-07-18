package com.hackathon.backend.dto;

import com.hackathon.backend.entity.SuggestionStatus;
import jakarta.validation.constraints.NotNull;

public record SuggestionStatusRequest(
        @NotNull SuggestionStatus status
) {}
