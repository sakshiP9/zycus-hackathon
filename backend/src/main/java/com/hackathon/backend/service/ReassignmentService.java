package com.hackathon.backend.service;

import com.hackathon.backend.entity.ReassignmentSuggestion;
import com.hackathon.backend.entity.TriggerReason;

public interface ReassignmentService {
    ReassignmentSuggestion generateSuggestion(String orderId, TriggerReason triggerReason);

}
