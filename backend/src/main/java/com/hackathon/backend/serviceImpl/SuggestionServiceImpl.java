package com.hackathon.backend.serviceImpl;

import com.hackathon.backend.dto.SuggestionResponse;
import com.hackathon.backend.entity.ReassignmentSuggestion;
import com.hackathon.backend.entity.SuggestionStatus;
import com.hackathon.backend.exception.SuggestionNotFoundException;
import com.hackathon.backend.repository.ReassignmentSuggestionRepository;
import com.hackathon.backend.service.SuggestionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SuggestionServiceImpl implements SuggestionService{
    private final ReassignmentSuggestionRepository suggestionRepository;

    public SuggestionServiceImpl(ReassignmentSuggestionRepository suggestionRepository) {
        this.suggestionRepository = suggestionRepository;
    }

    @Override
    public List<SuggestionResponse> listPending() {
        return suggestionRepository.findPendingWithDetails(SuggestionStatus.PENDING)
                .stream().map(this::toResponse).toList();
    }

    @Override
    public SuggestionResponse updateStatus(Long suggestionId, SuggestionStatus newStatus) {
        ReassignmentSuggestion suggestion = suggestionRepository.findById(suggestionId)
                .orElseThrow(() -> new SuggestionNotFoundException(suggestionId));

        if (newStatus == SuggestionStatus.ACCEPTED) {
            suggestion.accept();
            suggestion.getOrder().reassignTo(suggestion.getRecommendedAgent());
        } else if (newStatus == SuggestionStatus.REJECTED) {
            suggestion.reject();
        } else {
            throw new IllegalArgumentException("Cannot set suggestion status to " + newStatus + " via this endpoint");
        }

        return toResponse(suggestionRepository.save(suggestion));
    }

    private SuggestionResponse toResponse(ReassignmentSuggestion s) {
        return new SuggestionResponse(
                s.getId(), s.getOrder().getId(), s.getRecommendedAgent().getId(),
                s.getConfidence(), s.getReasoning(), s.getStatus(),
                s.getTriggerReason(), s.getCreatedAt()
        );
    }
}
