package com.hackathon.backend.controller;

import com.hackathon.backend.dto.SuggestionResponse;
import com.hackathon.backend.dto.SuggestionStatusRequest;
import com.hackathon.backend.service.SuggestionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/suggestions")
public class SuggestionController {

    private final SuggestionService suggestionService;

    public SuggestionController(SuggestionService suggestionService) {
        this.suggestionService = suggestionService;
    }

    @GetMapping
    public ResponseEntity<List<SuggestionResponse>> listPending() {
        return ResponseEntity.ok(suggestionService.listPending());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SuggestionResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody SuggestionStatusRequest request) {
        return ResponseEntity.ok(suggestionService.updateStatus(id, request.status()));
    }
}
