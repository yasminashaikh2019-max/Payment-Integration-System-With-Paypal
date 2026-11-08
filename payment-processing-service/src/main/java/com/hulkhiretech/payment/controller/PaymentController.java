package com.hulkhiretech.payment.controller;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hulkhiretech.payment.pojo.CreateTxnReq;
import com.hulkhiretech.payment.pojo.InitiateTxnReq;
import com.hulkhiretech.payment.pojo.PaymentResponse;
import com.hulkhiretech.payment.service.interfaces.PaymentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/payments")

@Slf4j
public class PaymentController {
	
	private final PaymentService paymentService;
	
	@PostMapping
public ResponseEntity<PaymentResponse> createPAyment(@RequestBody CreateTxnReq createTxnReq)	
{
		log.info("POST/payments - createPayment Called with " + "createTxnReq: {}", createTxnReq);
		
		PaymentResponse response = paymentService.createPayment(createTxnReq);
		
		log.info("POST/Payments/{id}/create"
				+" -createPayment called ");
		
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
		}
/**
 * 1.Make DB call to get transaction details by txnReference
 * 2.USe status system to update/process INITIATED status in db
 * 3.Call create order paypal-provider  API
 * 4.Once  succes then update DB as pending
 * 5.Return the values to api response.
 * @param txnReference
 * @return
 */
	
	@PostMapping("/{txnReference}/initiate")
	public ResponseEntity<PaymentResponse> initiatePAyment(
			@PathVariable String txnReference,
			@RequestBody InitiateTxnReq initiateTxnReq)		
	{
			log.info(" -initiatePayment called TxnReference:{}",txnReference);
			
			PaymentResponse response = paymentService.initiatePayment(txnReference,initiateTxnReq);
			log.info("initiatePayment reponse: {} ",response);
			
			
			return ResponseEntity.ok()
					.body(response);
			}
	@PostMapping("/{txnReference}/capture")
	public ResponseEntity<PaymentResponse> capturePAyment(
			@PathVariable String txnReference)	
	{
			log.info(" capturePayment called TxnReference:{}",txnReference);
			
			PaymentResponse response = paymentService.capturePayment(txnReference);
			log.info("capturePayment reponse: {} ",response);
			
			
			return ResponseEntity.ok()
					.body(response);
			}

@GetMapping("/cancel")
public ResponseEntity<String> paymentCancel(
        @RequestParam String token) {

    log.info("PayPal payment cancelled. Token: {}", token);

    return ResponseEntity.ok("Payment cancelled");
}

@GetMapping("/success")
public ResponseEntity<String> paymentSuccess(
        @RequestParam String token) {

    log.info("PayPal payment approved. Token: {}", token);

    return ResponseEntity.ok("Payment approved");
}
}