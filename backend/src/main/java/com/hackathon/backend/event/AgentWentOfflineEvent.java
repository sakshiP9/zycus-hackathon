package com.hackathon.backend.event;

public class AgentWentOfflineEvent {
    private final String agentId;

    public AgentWentOfflineEvent(String agentId) {
        this.agentId = agentId;
    }

    public String getAgentId() {
        return agentId;
    }
}
