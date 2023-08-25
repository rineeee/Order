package com.example.orderservice.domain.service.dto;

import lombok.Data;
import org.apache.kafka.clients.producer.Callback;

import java.io.Serializable;

@Data
public class OrderDto implements Serializable {
    private String productId;
    private Integer qty;
    private Integer unitPrice;
    private Integer totalPrice;

    private String orderId;
    private String userId;
}
