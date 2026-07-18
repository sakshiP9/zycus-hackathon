package com.hackathon.backend.repository;

import com.hackathon.backend.entity.Agent;
import com.hackathon.backend.entity.AgentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgentRepository extends JpaRepository<Agent, String> {

    List<Agent> findByStatus(AgentStatus status);
}
