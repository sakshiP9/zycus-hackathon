package com.hackathon.backend.controller;

import com.hackathon.backend.config.RoutingStrategyConfig;
import com.hackathon.backend.entity.RoutingStrategyRequest;
import com.hackathon.backend.service.RoutingStrategy;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/config")
public class RoutingConfigController {

    private final RoutingStrategyConfig routingStrategyConfig;
    private final Map<String, RoutingStrategy> strategies; // for validation

    public RoutingConfigController(RoutingStrategyConfig routingStrategyConfig,Map<String, RoutingStrategy> strategies){
        this.routingStrategyConfig = routingStrategyConfig;
        this.strategies = strategies;
    }

    @PatchMapping("/routing-strategy")
    public ResponseEntity<Map<String, String>> updateStrategy(
            @Valid @RequestBody RoutingStrategyRequest request) {

        if (!strategies.containsKey(request.strategy())) {
            throw new IllegalArgumentException(
                    "Unknown strategy: " + request.strategy()
                            + ". Valid options: " + strategies.keySet());
        }

        routingStrategyConfig.set(request.strategy());
        return ResponseEntity.ok(Map.of("active", request.strategy()));
    }

    @GetMapping("/routing-strategy")
    public ResponseEntity<Map<String, String>> currentStrategy() {
        return ResponseEntity.ok(Map.of("active", routingStrategyConfig.get()));
    }
}
