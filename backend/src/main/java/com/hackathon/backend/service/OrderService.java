package com.hackathon.backend.service;

import com.hackathon.backend.dto.OrderRequest;
import com.hackathon.backend.dto.OrderResponse;
import com.hackathon.backend.dto.SuggestionResponse;
import com.hackathon.backend.entity.OrderStatus;

import java.util.List;

public interface OrderService {

    OrderResponse createOrder(OrderRequest request);

    List<OrderResponse> listOrders(OrderStatus status);

    SuggestionResponse suggest(String orderId);
}
