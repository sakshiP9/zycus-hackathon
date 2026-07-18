package com.hackathon.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

// config/RoutingStrategyConfig.java
@Component
public class RoutingStrategyConfig {

    private final AtomicReference<String> activeStrategy;

    public RoutingStrategyConfig(@Value("${routing.strategy:ruleBased}") String initial) {
        this.activeStrategy = new AtomicReference<>(initial);
    }

    public String get() {
        return activeStrategy.get();
    }

    public void set(String strategyName) {
        this.activeStrategy.set(strategyName);
    }
}