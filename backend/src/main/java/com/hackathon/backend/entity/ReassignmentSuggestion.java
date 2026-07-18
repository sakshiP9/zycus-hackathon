package com.hackathon.backend.entity;

import com.hackathon.backend.exception.InvalidStateTransitionException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "reassignment_suggestions")
@Setter
@Getter
public class ReassignmentSuggestion {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "recommended_agent_id")
    private Agent recommendedAgent;

    private double confidence;

    @Column(length = 2000)
    private String reasoning;

    @Enumerated(EnumType.STRING)
    private SuggestionStatus status;

    @Enumerated(EnumType.STRING)
    private TriggerReason triggerReason;

    private Instant createdAt;

    // --- state machine ---

    public void accept() {
        if (status != SuggestionStatus.PENDING) {
            throw new InvalidStateTransitionException("Already resolved");
        }
        this.status = SuggestionStatus.ACCEPTED;
    }

    public void reject() {
        if (status != SuggestionStatus.PENDING) {
            throw new InvalidStateTransitionException("Already resolved");
        }
        this.status = SuggestionStatus.REJECTED;
    }
}
