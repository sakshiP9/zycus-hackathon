package com.hackathon.backend.service;

import com.hackathon.backend.dto.SuggestionResponse;
import com.hackathon.backend.entity.SuggestionStatus;

import java.util.List;

public interface SuggestionService {
    List<SuggestionResponse> listPending();

    SuggestionResponse updateStatus(Long suggestionId, SuggestionStatus newStatus);
}
