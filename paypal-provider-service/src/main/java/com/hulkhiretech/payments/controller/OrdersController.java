package com.hulkhiretech.payments.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hulkhiretech.payments.pojo.CreateOrderReq;
import com.hulkhiretech.payments.pojo.OrderResponse;
import com.hulkhiretech.payments.service.interfaces.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/orders")
@Slf4j
@RequiredArgsConstructor
public class OrdersController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderReq createOrderReq) {

        log.info("createOrder called (controller)-delegating to service" + "CreateOrder():{}", createOrderReq);

        OrderResponse response = orderService.createOrder(createOrderReq);

        log.info("createOrder response: {}", response);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{providerReference}/capture")
    public ResponseEntity<OrderResponse> captureOrder(
            @PathVariable String providerReference) {

        log.info("captureOrder called, providerReference:{}",
                providerReference);

        OrderResponse response =
                orderService.captureOrder(providerReference);

        log.info("captureOrder response: {}", response);

        return ResponseEntity.ok(response);
    }
    }
