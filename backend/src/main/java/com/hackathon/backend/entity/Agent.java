package com.hackathon.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Agent")
@Getter
@Setter
public class Agent {

    @Id
    private String id;

    private String name;

    private int activeOrderCount;

    @Enumerated(EnumType.STRING)
    private AgentStatus status;

    // Sprint 2 placeholders
    private String currentZone;
    private Integer maxCapacity;

    // --- state machine / behavior ---

    public void goOffline() {
        this.status = AgentStatus.OFFLINE;
    }

    public void incrementLoad() {
        this.activeOrderCount++;
    }

    public boolean isAvailable() {
        return status == AgentStatus.AVAILABLE;
    }
}
