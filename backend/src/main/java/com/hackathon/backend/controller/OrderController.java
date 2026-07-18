package com.hackathon.backend.controller;

import com.hackathon.backend.dto.OrderRequest;
import com.hackathon.backend.dto.OrderResponse;
import com.hackathon.backend.dto.SuggestionResponse;
import com.hackathon.backend.entity.*;
import com.hackathon.backend.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.createOrder(request));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> listOrders(
            @RequestParam(required = false) OrderStatus status) {
        return ResponseEntity.ok(orderService.listOrders(status));
    }

    @PostMapping("/{id}/suggest")
    public ResponseEntity<SuggestionResponse> suggest(@PathVariable String id) {
        return ResponseEntity.ok(orderService.suggest(id));
    }
}
