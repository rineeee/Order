package com.example.orderservice.infrastructure.kafka.dto;

import com.example.orderservice.infrastructure.kafka.dto.Field;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Schema {
    private String type;
    private List<Field> fields;
    private boolean optional;
    private String name;
}