package com.hackathon.backend.repository;

import com.hackathon.backend.entity.ReassignmentSuggestion;
import com.hackathon.backend.entity.SuggestionStatus;
import com.hackathon.backend.entity.TriggerReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReassignmentSuggestionRepository extends JpaRepository<ReassignmentSuggestion, Long> {

    boolean existsByOrder_IdAndTriggerReasonAndStatus(
            String orderId, TriggerReason triggerReason, SuggestionStatus status);

    @Query("SELECT s FROM ReassignmentSuggestion s " +
            "JOIN FETCH s.order JOIN FETCH s.recommendedAgent " +
            "WHERE s.status = :status")
    List<ReassignmentSuggestion> findPendingWithDetails(@Param("status") SuggestionStatus status);
}
