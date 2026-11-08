package com.hulkhiretech.payment.service.client;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payment.constant.ErrorCodeEnum;
import com.hulkhiretech.payment.exception.ProcessingException;
import com.hulkhiretech.payment.http.HttpRequest;
import com.hulkhiretech.payment.http.HttpServiceEngine;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaypalProviderClient {
	
	private final HttpServiceEngine httpServiceEngine;
	@CircuitBreaker(name = "payment-processing-service", fallbackMethod = "makeCallFallback")
	
	public ResponseEntity<String>makeCall(HttpRequest httpRequest) {
		log.info("PaypalCLinet.makePaypalCall called:{}",httpRequest);
		
	
		ResponseEntity<String>httpResponse = httpServiceEngine.makeHttpCall(httpRequest);
		
		
		log.info("HttpServiceEngine.makeHttpCAll "+"response:{}",httpResponse);
		
		return httpResponse;
	}

	/**
	 * Convenience method for capture flow - delegates to makeCall to preserve
	 * existing authentication/headers behaviour.
	 */
	public ResponseEntity<String> captureOrder(HttpRequest httpRequest) {
		log.info("PaypalClient.captureOrder called: {}", httpRequest);
		return makeCall(httpRequest);
	}
	
	public ResponseEntity<String> makeCallFallback(HttpRequest httpRequest, Throwable t) {
		//handle fallback logic here
	
		log.error("Fallback method called for makeCall due to: {}", t.getMessage());
		//if t is instaance of paymentValidationException rethrow it with typecast
		if(t instanceof ProcessingException pve) {
			log.error("Rethrowing ProcessingException from fallback: {}", pve.getMessage());
			throw pve;
		}
		log.error("Returning fallback error response for HTTP call to URL:{}", httpRequest.getUrl());
		throw new ProcessingException(
				ErrorCodeEnum.UNABLE_TO_CONNECT_TO_PAYPAL_PROVIDER.getErrorCode(),
				ErrorCodeEnum.UNABLE_TO_CONNECT_TO_PAYPAL_PROVIDER.getErrorMessage(),
		HttpStatus.INTERNAL_SERVER_ERROR);
				
	}
}

