package com.hackathon.backend.repository;

import com.hackathon.backend.entity.Order;
import com.hackathon.backend.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByAssignedAgentId(String agentId);
}
