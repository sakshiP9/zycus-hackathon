package com.hackathon.backend.service;

import com.hackathon.backend.dto.AgentResponse;
import com.hackathon.backend.entity.AgentStatus;

import java.util.List;

public interface AgentService {
    List<AgentResponse> listAgents();

    AgentResponse updateStatus(String agentId, AgentStatus newStatus);
}
