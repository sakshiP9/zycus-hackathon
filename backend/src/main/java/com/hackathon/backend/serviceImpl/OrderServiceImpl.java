package com.hackathon.backend.serviceImpl;

import com.hackathon.backend.dto.OrderRequest;
import com.hackathon.backend.dto.OrderResponse;
import com.hackathon.backend.dto.SuggestionResponse;
import com.hackathon.backend.entity.*;
import com.hackathon.backend.exception.AgentNotFoundException;
import com.hackathon.backend.repository.AgentRepository;
import com.hackathon.backend.repository.OrderRepository;
import com.hackathon.backend.service.OrderService;
import com.hackathon.backend.service.ReassignmentService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final AgentRepository agentRepository;
    private final ReassignmentService reassignmentService;

    public OrderServiceImpl(OrderRepository orderRepository,
                            AgentRepository agentRepository,
                            ReassignmentService reassignmentService) {
        this.orderRepository = orderRepository;
        this.agentRepository = agentRepository;
        this.reassignmentService = reassignmentService;
    }

    @Override
    public OrderResponse createOrder(OrderRequest request) {
        Agent agent = agentRepository.findById(request.assignedAgentId())
                .orElseThrow(() -> new AgentNotFoundException(request.assignedAgentId()));

        Order order = new Order();
        order.setId(request.id());
        order.setDescription(request.description());
        order.setAssignedAgent(agent);
        order.setStatus(OrderStatus.ASSIGNED);
        order.setCreatedAt(Instant.now());

        Order saved = orderRepository.save(order);
        return toResponse(saved);
    }

    @Override
    public List<OrderResponse> listOrders(OrderStatus status) {
        List<Order> orders = (status != null)
                ? orderRepository.findByStatus(status)
                : orderRepository.findAll();

        return orders.stream().map(this::toResponse).toList();
    }

    @Override
    public SuggestionResponse suggest(String orderId) {
        ReassignmentSuggestion suggestion =
                reassignmentService.generateSuggestion(orderId, TriggerReason.INITIAL);
        return toSuggestionResponse(suggestion);
    }

    private OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getDescription(),
                order.getAssignedAgent() != null ? order.getAssignedAgent().getId() : null,
                order.getStatus(),
                order.getCreatedAt()
        );
    }

    private SuggestionResponse toSuggestionResponse(ReassignmentSuggestion s) {
        return new SuggestionResponse(
                s.getId(),
                s.getOrder().getId(),
                s.getRecommendedAgent().getId(),
                s.getConfidence(),
                s.getReasoning(),
                s.getStatus(),
                s.getTriggerReason(),
                s.getCreatedAt()
        );
    }
}
