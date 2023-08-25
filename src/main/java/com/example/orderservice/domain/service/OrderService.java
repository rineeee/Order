package com.example.orderservice.domain.service;

import com.example.orderservice.domain.service.dto.OrderDto;
import com.example.orderservice.domain.entity.OrderEntity;

public interface OrderService {
    OrderDto createOrder(OrderDto orderDto);
    OrderDto getOrderByOrderId(String orderId);
    Iterable<OrderEntity> getOrdersByUserId(String userId);
}
