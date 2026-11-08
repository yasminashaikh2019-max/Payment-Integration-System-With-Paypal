package com.hulkhiretech.payments.service.interfaces;

import org.springframework.web.bind.annotation.PathVariable;

import com.hulkhiretech.payments.pojo.CreateOrderReq;
import com.hulkhiretech.payments.pojo.OrderResponse;

public interface OrderService {
	OrderResponse createOrder(CreateOrderReq createOrderReq);
	OrderResponse captureOrder(String providerReference);

}
