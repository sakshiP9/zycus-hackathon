package com.hackathon.backend.event;

import com.hackathon.backend.entity.Order;
import com.hackathon.backend.entity.SuggestionStatus;
import com.hackathon.backend.entity.TriggerReason;
import com.hackathon.backend.repository.OrderRepository;
import com.hackathon.backend.repository.ReassignmentSuggestionRepository;
import com.hackathon.backend.service.ReassignmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
public class AgentOfflineListener {
    private static final Logger log = (Logger) LoggerFactory.getLogger(AgentOfflineListener.class);

    private final OrderRepository orderRepository;
    private final ReassignmentSuggestionRepository suggestionRepository;
    private final ReassignmentService reassignmentService;

    public AgentOfflineListener(OrderRepository orderRepository,
                                ReassignmentSuggestionRepository suggestionRepository,
                                ReassignmentService reassignmentService) {
        this.orderRepository = orderRepository;
        this.suggestionRepository = suggestionRepository;
        this.reassignmentService = reassignmentService;
    }

    @Async
    @TransactionalEventListener // fires only after the OFFLINE status commit — avoids stale reads
    public void onAgentOffline(AgentWentOfflineEvent event) {
        String agentId = event.getAgentId();
        List<Order> affectedOrders = orderRepository.findByAssignedAgentId(agentId);

        log.info("Agent {} went offline — {} order(s) affected", agentId, affectedOrders.size());

        for (Order order : affectedOrders) {
            boolean alreadyPending = suggestionRepository
                    .existsByOrder_IdAndTriggerReasonAndStatus(
                            order.getId(), TriggerReason.AGENT_OFFLINE, SuggestionStatus.PENDING);

            if (alreadyPending) {
                log.info("Skipping order {} — a pending re-plan suggestion already exists", order.getId());
                continue;
            }

            try {
                reassignmentService.generateSuggestion(order.getId(), TriggerReason.AGENT_OFFLINE);
            } catch (Exception e) {
                // a failure for one order must not stop re-planning for the rest
                log.error("Failed to generate re-plan suggestion for order {}: {}", order.getId(), e.getMessage());
            }
        }
    }
}
