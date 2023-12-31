package com.example.orderservice.application.rest;

import com.example.orderservice.domain.service.dto.OrderDto;
import com.example.orderservice.infrastructure.kafka.producer.KafkaProducer;
import com.example.orderservice.infrastructure.kafka.producer.OrderProducer;
import com.example.orderservice.domain.entity.OrderEntity;
import com.example.orderservice.domain.service.OrderService;
import com.example.orderservice.application.rest.vo.RequestOrder;
import com.example.orderservice.application.rest.vo.ResponseOrder;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/order-service")
@Slf4j
public class OrderController {
    Environment env;
    OrderService orderService;
    KafkaProducer kafkaProducer;
    OrderProducer orderProducer;

    @Autowired
    public OrderController(Environment env, OrderService orderService,
                           KafkaProducer kafkaProducer, OrderProducer orderProducer) {
        this.env = env;
        this.orderService = orderService;
        this.kafkaProducer = kafkaProducer;
        this.orderProducer = orderProducer;
    }

    @GetMapping("/health_check")
    public String status() {
        return String.format("It's Working in Order Service on PORT %s",
                env.getProperty("local.server.port"));
    }

    @PostMapping("/{user_id}/orders")
    public ResponseEntity<ResponseOrder> createOrder(@PathVariable("user_id") String userId,
                                                     @RequestBody RequestOrder orderDetails){
        log.info("Before add orders data");
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        OrderDto orderDto = mapper.map(orderDetails, OrderDto.class);
        orderDto.setUserId(userId);

        /* JPA */
//        OrderDto createdOrder = orderService.createOrder(orderDto);
//        ResponseOrder responseOrder = mapper.map(createdOrder, ResponseOrder.class);

        /* Kafka */
        orderDto.setOrderId(UUID.randomUUID().toString());
        orderDto.setTotalPrice(orderDetails.getQty() * orderDetails.getUnitPrice());

        kafkaProducer.send("example-catalog-topic", orderDto);
        orderProducer.send("orders",orderDto);

        ResponseOrder responseOrder = mapper.map(orderDto, ResponseOrder.class);

        log.info("After added orders data");
        return new ResponseEntity(responseOrder, HttpStatus.CREATED);
    }

    @GetMapping("/{user_id}/orders")
    public ResponseEntity<List<ResponseOrder>> getOrder(@PathVariable("user_id") String userId){
        log.info("Before retrieve orders data");
        Iterable<OrderEntity> orderList = orderService.getOrdersByUserId(userId);

        List<ResponseOrder> result = new ArrayList<>();
        orderList.forEach(v ->{
            result.add(new ModelMapper().map(v, ResponseOrder.class));
        });
        log.info("After retrieve orders data");
        return new ResponseEntity(result, HttpStatus.OK);
    }


}
