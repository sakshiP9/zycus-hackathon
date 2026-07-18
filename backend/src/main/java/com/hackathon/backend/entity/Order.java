package com.hackathon.backend.entity;

import com.hackathon.backend.exception.InvalidStateTransitionException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {

    @Id
    private String id;

    private String description;

    @ManyToOne
    @JoinColumn(name = "assigned_agent_id")
    private Agent assignedAgent;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private Instant createdAt;

    // Sprint 2 placeholders — nullable, unused today
    private String pickupZone;
    private String dropoffZone;
    @Enumerated(EnumType.STRING)
    private WeightClass weightClass; // nullable enum, LIGHT/HEAVY — not used yet

    // --- state machine methods, not raw setters ---

    public void markReassignmentPending() {
        if (status != OrderStatus.ASSIGNED) {
            throw new InvalidStateTransitionException(
                    "Cannot mark pending from state: " + status);
        }
        this.status = OrderStatus.REASSIGNMENT_PENDING;
    }

    public void reassignTo(Agent newAgent) {
        if (status != OrderStatus.REASSIGNMENT_PENDING) {
            throw new InvalidStateTransitionException(
                    "Cannot reassign from state: " + status);
        }
        this.assignedAgent = newAgent;
        this.status = OrderStatus.REASSIGNED;
    }

    public void markDelivered() {
        this.status = OrderStatus.DELIVERED;
    }
}
