package com.hackathon.backend.serviceImpl;

import com.hackathon.backend.dto.AgentResponse;
import com.hackathon.backend.entity.Agent;
import com.hackathon.backend.entity.AgentStatus;
import com.hackathon.backend.event.AgentWentOfflineEvent;
import com.hackathon.backend.exception.AgentNotFoundException;
import com.hackathon.backend.repository.AgentRepository;
import com.hackathon.backend.service.AgentService;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgentServiceImpl implements AgentService {

    private final AgentRepository agentRepository;
    private final ApplicationEventPublisher eventPublisher;

    public AgentServiceImpl(AgentRepository agentRepository,
                            ApplicationEventPublisher eventPublisher) {
        this.agentRepository = agentRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public List<AgentResponse> listAgents() {
        return agentRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public AgentResponse updateStatus(String agentId, AgentStatus newStatus) {
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new AgentNotFoundException(agentId));

        agent.setStatus(newStatus);
        Agent saved = agentRepository.saveAndFlush(agent);

        if (newStatus == AgentStatus.OFFLINE) {
            // publish AFTER save — listener re-fetches from DB, must see the new status
            eventPublisher.publishEvent(new AgentWentOfflineEvent(agentId));
        }

        return toResponse(saved);
    }

    private AgentResponse toResponse(Agent agent) {
        return new AgentResponse(
                agent.getId(),
                agent.getName(),
                agent.getActiveOrderCount(),
                agent.getStatus()
        );
    }
}
