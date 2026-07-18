package com.hackathon.backend.controller;

import com.hackathon.backend.dto.AgentResponse;
import com.hackathon.backend.dto.AgentStatusRequest;
import com.hackathon.backend.service.AgentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/agents")
public class AgentController {
    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    @GetMapping
    public ResponseEntity<List<AgentResponse>> listAgents() {
        return ResponseEntity.ok(agentService.listAgents());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AgentResponse> updateStatus(@PathVariable String id,@Valid @RequestBody AgentStatusRequest request) {
        return ResponseEntity.ok(agentService.updateStatus(id, request.status()));
    }
}
