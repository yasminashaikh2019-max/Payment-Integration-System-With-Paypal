package com.hulkhiretech.payment.service.helper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payment.constant.ErrorCodeEnum;
import com.hulkhiretech.payment.dto.TransactionDTO;
import com.hulkhiretech.payment.exception.ProcessingException;
import com.hulkhiretech.payment.http.HttpRequest;
import com.hulkhiretech.payment.paypalprovider.CreateOrderReq;
import com.hulkhiretech.payment.paypalprovider.OrderResponse;
import com.hulkhiretech.payment.paypalprovider.PPErrorResponse;
import com.hulkhiretech.payment.pojo.InitiateTxnReq;
import com.hulkhiretech.payment.util.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaypalProviderCaptureOrderHelper {
	
	
	private static final String COMPLETED = "COMPLETED";

    @Value("${paypal.provider.captureOrder.url.Template}")
    private String paypalProvidercaptureOrderUrlTemplate;


	private final JsonUtil jsonUtil;
	

	public HttpRequest prepareRequest(String ProviderReference) {
		HttpHeaders customHttpHeaders = new HttpHeaders();

		//customHttpHeaders.add(Constant.PAYPAL_REQUEST_ID_HEADER,UUID.randomUUID().toString());

		customHttpHeaders.setContentType(MediaType.APPLICATION_JSON);


				
				
		String url = paypalProvidercaptureOrderUrlTemplate.replace("{orderId}", ProviderReference); // Replace with actual



		HttpRequest httpRquest = HttpRequest.builder()
				.httpMethod(HttpMethod.POST)
				.url(url)
				.httpHeaders(customHttpHeaders)
				.body("")
				.build();
		return httpRquest;
	}


	public OrderResponse processResponse(
	        ResponseEntity<String> httpResponse) {

	    log.info("Processing PayPal Create Order response");

	    // =========================
	    // SUCCESS - 2xx
	    // =========================
	    if (httpResponse.getStatusCode().is2xxSuccessful()) {

	        OrderResponse paypalResponseObj = jsonUtil.convertJsonToObject(
	                        httpResponse.getBody(),
	                        OrderResponse.class);

	        log.info(
	                "Converted PayPal response JSON to PaypalOrderResponse: {}",
	                paypalResponseObj);

	        // Create Order specific success definition
	        if (paypalResponseObj != null
	                && paypalResponseObj.getOrderId() != null
	                && "COMPLETED"
	                        .equalsIgnoreCase(paypalResponseObj.getPaypalStatus()))
	                         {

	            log.info(
	                    "PayPal Create Order successful. orderId={}, status={}",
	                    paypalResponseObj.getOrderId(),
	                    paypalResponseObj.getPaypalStatus());

	            return paypalResponseObj;
	        }

	        // 2xx but business response is not what we expect
	        log.error("PayPal Create Order returned 2xx but response is invalid: {}",paypalResponseObj);

	        throw new ProcessingException(
	                ErrorCodeEnum.PAYPAL_CREATE_ORDER_FAILED.getErrorCode(),
	                ErrorCodeEnum.PAYPAL_CREATE_ORDER_FAILED.getErrorMessage(),
	                HttpStatus.INTERNAL_SERVER_ERROR
	        );
	    }


	    // =========================
	    // ERROR - 4xx or 5xx
	    // =========================
	    if (httpResponse.getStatusCode().is4xxClientError()
	            || httpResponse.getStatusCode().is5xxServerError()) {

	        log.error("PayPal Create Order failed. status={}, response={}",
	                httpResponse.getStatusCode(),
	                httpResponse.getBody());

	        PPErrorResponse errorObj =jsonUtil.convertJsonToObject(
	                        httpResponse.getBody(),
	                        PPErrorResponse.class);

	        if (errorObj != null) {

	            //String errorMessage = generateErrorMessage(errorObj);

	            log.error("PayPal error details:errorCode = {}, errorMessage ={}",errorObj.getErrorCode()
	            		,errorObj.getErrorMessage());

	            throw new ProcessingException(
	            		errorObj.getErrorCode(),
	            		errorObj.getErrorMessage(),
	                    HttpStatus.valueOf(
	                            httpResponse.getStatusCode().value()
	                    )
	            );
	        }

	        // PayPal returned 4xx/5xx but error JSON couldn't be parsed
	        log.error(
	                "Unable to parse PayPal error response. status={}, body={}",
	                httpResponse.getStatusCode(),
	                httpResponse.getBody());

	        throw new ProcessingException(
	                ErrorCodeEnum.PAYPAL_CAPTURE_ORDER_ERROR.getErrorCode(),
	                ErrorCodeEnum.PAYPAL_CAPTURE_ORDER_ERROR.getErrorMessage(),
	                HttpStatus.valueOf(
	                        httpResponse.getStatusCode().value()
	                )
	        );
	    }


	    // =========================
	    // ANY OTHER STATUS
	    // =========================
	    log.error(
	            "Unexpected PayPal response. status={}, body={}",
	            httpResponse.getStatusCode(),
	            httpResponse.getBody());

	    throw new ProcessingException(
	            ErrorCodeEnum.PAYPAL_CAPTURE_ORDER_FAILED.getErrorCode(),
	            ErrorCodeEnum.PAYPAL_CAPTURE_ORDER_FAILED.getErrorMessage(),
	            HttpStatus.INTERNAL_SERVER_ERROR
	    );
	}
	}
/*
	private String generateErrorMessage(PPErrorResponse error) {

	    if (error == null) {
	        return "PayPal request failed";
	    }

	    StringJoiner joiner = new StringJoiner(" | ");

	    // Top-level fields
	    Arrays.stream(PPErrorResponse.class.getDeclaredFields())
	            .filter(field -> !"details".equals(field.getName()))
	            .forEach(field ->
	                    addIfNotNull(
	                            joiner,
	                            field.getName(),
	                            field,
	                            error));

	    // Nested details fields
	    if (error.getDetails() != null) {

	        for (int i = 0; i < error.getDetails().size(); i++) {

	            PaypalError.Detail detail =
	                    error.getDetails().get(i);

	            String prefix = "details[" + i + "].";

	            Arrays.stream(PPErrorResponse.Detail.class.getDeclaredFields())
	                    .forEach(field ->
	                            addIfNotNull(
	                                    joiner,
	                                    prefix + field.getName(),
	                                    field,
	                                    detail));
	        }
	    }

	    return joiner.toString();
	}


	private void addIfNotNull(
	        StringJoiner joiner,
	        String key,
	        Field field,
	        Object target) {

	    try {

	        field.setAccessible(true);

	        Object value = field.get(target);
	        

	        if (value != null) {
	            joiner.add(key + ":" + value);
	        }

	    } catch (IllegalAccessException ignored) {
	    }
	}

	

}
	*/