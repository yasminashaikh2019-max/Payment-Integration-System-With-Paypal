package com.hulkhiretech.payments.service.IMPL;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hulkhiretech.payments.constant.Constant;
import com.hulkhiretech.payments.constant.ErrorCodeEnum;
import com.hulkhiretech.payments.exception.PaypalProviderException;
import com.hulkhiretech.payments.http.HttpRequest;
import com.hulkhiretech.payments.paypal.PaypalOrderResponse;
import com.hulkhiretech.payments.pojo.CreateOrderReq;
import com.hulkhiretech.payments.pojo.OrderResponse;
import com.hulkhiretech.payments.service.client.PaypalClient;
import com.hulkhiretech.payments.service.helper.CaptureOrderHelper;
import com.hulkhiretech.payments.service.helper.CreateOrderHelper;
import com.hulkhiretech.payments.service.interfaces.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceIMPL implements OrderService {

	private final PaypalClient paypalClient;
	ObjectMapper objectMapper = new ObjectMapper();
	private final CreateOrderHelper createOrderHelper;
	
	private final CaptureOrderHelper captureOrderHelper;


	@Value("${paypal.createOrder.url}")
	private String paypalCreateOrderUrl;
	
	@Value("${paypal.captureOrder.url}")
	private String paypalCaptureOrderUrl;

	@Override
	public OrderResponse createOrder(CreateOrderReq createOrderReq) {
		log.info("OrderService.createOrder  called createOrderReq:{}", createOrderReq);
		

	    if (createOrderReq.getAmount().compareTo(BigDecimal.ZERO) <= 0) {

	        throw new PaypalProviderException(
	                "30001",
	                "Amount must be greater than zero",
	                HttpStatus.BAD_REQUEST);
	    }

		HttpRequest httpRequest = createOrderHelper.prepareCreateOrderPaypalRequest(createOrderReq);
		log.info("Prepared httpRequest for paypal create order : {}", httpRequest);

		ResponseEntity<String> httpResponse = paypalClient.makeCall(httpRequest);

		log.info("PaypalClient.makeCall response: {}", httpResponse);

		PaypalOrderResponse paypalResponseObj = createOrderHelper.processCreateOrderResponse(httpResponse);
		log.info("PaypalClinet.makeCall response:{}", httpResponse);

		OrderResponse orderRes = new OrderResponse();
		orderRes.setOrderId(paypalResponseObj.getId());
		orderRes.setPaypalStatus(paypalResponseObj.getStatus());
		orderRes.setRedirectUrl(paypalResponseObj.getLinks()
				.stream()
				.filter(link -> "payer-action".equals(link.getRel()))
				.map(PaypalOrderResponse.Link::getHref)
				.findFirst()
				.orElse(null));
		log.info("Mapped PaypalOrderResponse  to OrderResponse:{}", orderRes);

		return orderRes;
	}
	@Override
	public OrderResponse captureOrder(String providerReference) {
	    log.info("OrderService.captureOrder called: {}", providerReference);

	    String captureUrl = paypalCaptureOrderUrl
	            + "/" + providerReference
	            + "/capture";

	    HttpHeaders headers = new HttpHeaders();

	    headers.add(
	            Constant.PAYPAL_REQUEST_ID_HEADER,
	            UUID.randomUUID().toString()
	    );

	    headers.setContentType(MediaType.APPLICATION_JSON);
					// Ask PayPal to return full representation for the capture (optional)
					headers.add("Prefer", "return=representation");

	    HttpRequest httpRequest = HttpRequest.builder()
	            .httpMethod(HttpMethod.POST)
	            .url(captureUrl)
	            .httpHeaders(headers)
							.body("{}")
	            .build();

	    log.info("Prepared capture request: {}", httpRequest);

	    ResponseEntity<String> httpResponse =
	            paypalClient.captureOrder(httpRequest);

	    log.info("PayPal capture response: {}", httpResponse);

	    ResponseEntity<String> httpResponse1 =
	            paypalClient.captureOrder(httpRequest);
	    log.info("PayaplCLient.makeCallresponse():",httpResponse1);

	    PaypalOrderResponse paypalResponseObj =
	            captureOrderHelper.processCaptureOrderResponse(httpResponse1);
log.info("Processed paypal response into paypalResponse object:{}",paypalResponseObj);

OrderResponse orderRes = new OrderResponse();
orderRes.setOrderId(paypalResponseObj.getId());
orderRes.setPaypalStatus(paypalResponseObj.getStatus());
log.info("Mapped paypalResponse to orderRes:{}",orderRes);    
	    return orderRes;
	}
}