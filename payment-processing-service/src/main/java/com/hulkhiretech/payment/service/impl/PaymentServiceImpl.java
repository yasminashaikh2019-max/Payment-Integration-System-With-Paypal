package com.hulkhiretech.payment.service.impl;

import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payment.constant.ErrorCodeEnum;
import com.hulkhiretech.payment.constant.TxnStatusEnum;
import com.hulkhiretech.payment.dto.TransactionDTO;
import com.hulkhiretech.payment.entity.TransactionEntity;
import com.hulkhiretech.payment.exception.ProcessingException;
import com.hulkhiretech.payment.http.HttpRequest;
import com.hulkhiretech.payment.paypalprovider.OrderResponse;
import com.hulkhiretech.payment.pojo.CreateTxnReq;
import com.hulkhiretech.payment.pojo.InitiateTxnReq;
import com.hulkhiretech.payment.pojo.PaymentResponse;
import com.hulkhiretech.payment.repository.interfaces.TransactionRepository;
import com.hulkhiretech.payment.service.PaymentStatusService;
import com.hulkhiretech.payment.service.client.PaypalProviderClient;
import com.hulkhiretech.payment.service.helper.PaypalProviderCaptureOrderHelper;
import com.hulkhiretech.payment.service.helper.PaypalProviderCreateOrderHelper;
import com.hulkhiretech.payment.service.interfaces.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
	private final PaymentStatusService statusService;
	private final ModelMapper modelMapper;

	private final TransactionRepository transactionRepository;
	private final PaypalProviderClient paypalProviderClient;
	private final PaypalProviderCreateOrderHelper paypalProviderCreateOrderHelper;
	
	private final PaypalProviderCaptureOrderHelper paypalProviderCaptureOrderHelper;

	@Override
	public PaymentResponse createPayment(CreateTxnReq createTxnReq) {
		log.info("Createpayment called with createTxnReq: {}", createTxnReq);

		TransactionDTO dto = prepareTxnDtoFromRequest(createTxnReq);
		log.info("Mapped CreateTxnReq to TransactionDTO: {}", dto);

		dto.setTxnStatus(TxnStatusEnum.CREATED.getName()); // CREATED
		String txnReference = generateTxnReference();
		dto.setTxnReference(txnReference);

		log.info("Prepared TransactionDTO: {}, txnReference= {}", dto, txnReference);

		TransactionDTO response = statusService.processStatuses(dto);

		log.info("Response from PaymentStatusService: {}", response);

		// Populate PaymentResponse with values from response DTO
		PaymentResponse paymentResponse = new PaymentResponse();
		paymentResponse.setTxnReference(response.getTxnReference());
		paymentResponse.setTxnStatus(response.getTxnStatus());

		log.info("PaymentResponse populated: {}", paymentResponse);
		return paymentResponse;

	}

	private String generateTxnReference() {
		return UUID.randomUUID().toString();
	}

	private TransactionDTO prepareTxnDtoFromRequest(CreateTxnReq createTxnReq) {

		// use ModelMapper to map CreateTxnReq to TransactionDTO

		return modelMapper.map(createTxnReq, TransactionDTO.class);
	}

	@Override
	public PaymentResponse initiatePayment(String txnReference, InitiateTxnReq initiateTxnReq) {

		log.info("initiatePayment called with txnReference: {}", txnReference, " and initiateTxnReq: {}", initiateTxnReq);

		TransactionEntity entity = transactionRepository.getTxnByTxnReference(txnReference);
		log.info("Fetched TransactionEntity from DB: {}", entity);

		// use modelMapper to map entity to DTO
		TransactionDTO dto = modelMapper.map(entity, TransactionDTO.class);
		log.info("Mapped TransactionEntity to TransactionDTO: {}", dto);

		dto.setTxnStatus(TxnStatusEnum.INITIATED.getName());
		TransactionDTO responseDTO = statusService.processStatuses(dto);
		log.info("Response from PaymentStatusService after processing INITIATED status: {}", responseDTO);

		HttpRequest httpRequest = paypalProviderCreateOrderHelper.prepareRequest(dto,initiateTxnReq);
		log.info("Prepared from PaymentStatusservice after processing INITATED:{}", httpRequest);

		try {
			
		ResponseEntity<String> httpResponse = paypalProviderClient.makeCall(httpRequest);
		log.info("Repsonse from PayaplProviderClient:{}", httpResponse);
		
		
		OrderResponse successResponseObj=paypalProviderCreateOrderHelper.processResponse(httpResponse);
		log.info("Processed OrderResponse from PayaplProviderClient:{}", successResponseObj);


		String redirectUrl = "https://paypal.com/checkout?orderId";
		// if success
		dto.setTxnStatus(TxnStatusEnum.PENDING.getName());
		dto.setProviderReference(successResponseObj.getOrderId());
		responseDTO = statusService.processStatuses(dto);

		PaymentResponse response = new PaymentResponse();

		response.setTxnReference(responseDTO.getTxnReference());

		response.setTxnStatus(responseDTO.getTxnStatus());

		response.setProviderReference(responseDTO.getProviderReference());
		response.setRedirectUrl(successResponseObj.getRedirectUrl());
		log.info("Returnong PaymentResponse: {}", response);

		return response;
		
		}catch(ProcessingException e) {
			log.error("Exception while calling paypalProviderClient: {}", e.getMessage(), e);
			responseDTO.setTxnStatus(TxnStatusEnum.FAILED.getName());
			responseDTO.setErrorCode(e.getErrorCode());
			responseDTO.setErrorMessage(e.getMessage());
			
			statusService.processStatuses(responseDTO);
			log.info("Updated TransactionDTO to FAILED status in DB: {}", responseDTO);

			throw e;// Handle exception appropriately, possibly throw a custom exception
		}catch(Exception e) {
			log.error("Exception while calling paypalProviderClient: {}", e.getMessage(), e);
			responseDTO.setTxnStatus(TxnStatusEnum.FAILED.getName());
			responseDTO.setErrorCode(ErrorCodeEnum.ERROR_CALLING_CREATE_ORDER.getErrorCode());
			responseDTO.setErrorMessage(ErrorCodeEnum.ERROR_CALLING_CREATE_ORDER.getErrorMessage());
			
			statusService.processStatuses(responseDTO);
			log.info("Updated TransactionDTO to FAILED status in DB: {}", responseDTO);

			throw new ProcessingException(
					ErrorCodeEnum.ERROR_CALLING_CREATE_ORDER.getErrorCode(),
					ErrorCodeEnum.ERROR_CALLING_CREATE_ORDER.getErrorMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR
			);
		}

		
	}

	@Override
	public PaymentResponse capturePayment(String txnReference) {

	    log.info("capturePayment called with txnReference: {}", txnReference);

	    TransactionEntity entity =
	            transactionRepository.getTxnByTxnReference(txnReference);

	    log.info("Fetched TransactionEntity from DB: {}", entity);

	    TransactionDTO dto =
	            modelMapper.map(entity, TransactionDTO.class);

	    log.info("Mapped TransactionEntity to TransactionDTO: {}", dto);

	    // DB is already APPROVED.
	    // Do NOT do APPROVED -> APPROVED again.

	    HttpRequest httpRequest =paypalProviderCaptureOrderHelper.prepareRequest(dto.getProviderReference());

	    log.info("Prepared capture request: {}", httpRequest);

	    try {

	        ResponseEntity<String> httpResponse = paypalProviderClient.makeCall(httpRequest);

	        log.info("Response from PaypalProviderClient: {}", httpResponse);

	        OrderResponse successResponseObj = paypalProviderCaptureOrderHelper.processResponse(httpResponse);

	        log.info(
	                "Processed OrderResponse from PaypalProviderClient: {}",
	                successResponseObj
	        );

	        // PayPal capture was successful
	        dto.setTxnStatus(TxnStatusEnum.SUCCESS.getName());

	        TransactionDTO responseDTO =
	                statusService.processStatuses(dto);

	        PaymentResponse response = new PaymentResponse();

	        response.setTxnReference(responseDTO.getTxnReference());
	        response.setProviderReference(responseDTO.getProviderReference());
	        response.setTxnStatus(responseDTO.getTxnStatus());
	        
	       
	        log.info("Returning PaymentResponse: {}", response);

	        return response;

	    } catch (ProcessingException e) {

	        log.error(  "Exception while calling paypalProviderClient: {}",e.getMessage(), e);

	        throw e;
	    }
	}
}