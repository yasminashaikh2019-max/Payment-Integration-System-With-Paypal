package com.hulkhiretech.payments.service.helper;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.StringJoiner;
import java.util.UUID;

import org.apache.hc.core5.http.HttpResponse;
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
import com.hulkhiretech.payments.paypal.PaypalCreateOrderRequest;
import com.hulkhiretech.payments.paypal.PaypalCreateOrderRequest.Amount;
import com.hulkhiretech.payments.paypal.PaypalCreateOrderRequest.ExperienceContext;
import com.hulkhiretech.payments.paypal.PaypalError;
import com.hulkhiretech.payments.paypal.PaypalOrderResponse;
import com.hulkhiretech.payments.pojo.CreateOrderReq;
import com.hulkhiretech.payments.util.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreateOrderHelper {
	
	
	private static final String PAYER_ACTION_REQUIRED = "PAYER_ACTION_REQUIRED";

	@Value("${paypal.createOrder.url}")
	
	private String createOrderUrl;
	
	private final JsonUtil jsonUtil;
	

	public HttpRequest prepareCreateOrderPaypalRequest(CreateOrderReq createOrderReq) {
		HttpHeaders customHttpHeaders = new HttpHeaders();

		customHttpHeaders.add(
				Constant.PAYPAL_REQUEST_ID_HEADER,
				UUID.randomUUID().toString());

		customHttpHeaders.setContentType(MediaType.APPLICATION_JSON);


		ExperienceContext experienceContext = PaypalCreateOrderRequest.ExperienceContext.builder()
		.paymentMethodPreference(Constant.PAYMENT_METHOD_PREFERENCE)
		.landingPage(Constant.LANDING_PAGE)
		.shippingPreference(Constant.SHIPPING_PREFERENCE)
		.userAction(Constant.USER_ACTION)
		.returnUrl(createOrderReq.getReturnUrl())
		.cancelUrl(createOrderReq.getCancelUrl())
		.build();
		Amount amount = PaypalCreateOrderRequest.Amount.builder()
		 .currencyCode(createOrderReq.getCurrencyCode())
		    .value(String.valueOf(createOrderReq.getAmount()))
		.build();
		PaypalCreateOrderRequest createOrderrequestObj =PaypalCreateOrderRequest.builder()
				.intent(Constant.INTENT_CAPTURE)
				.paymentSource(
						PaypalCreateOrderRequest.PaymentSource.builder()
						.paypal(
								PaypalCreateOrderRequest.Paypal.builder()
								.experienceContext(
										experienceContext
										)
								.build()
								)
						.build()
						)
				.purchaseUnits(
						java.util.List.of(
								PaypalCreateOrderRequest.PurchaseUnit.builder()
								.invoiceId(UUID.randomUUID().toString())	
								.amount(
										amount
										)
								.build()
								)
						)
				.build();
		
		String reqAsJson=jsonUtil.converttoObjectToJson(createOrderrequestObj);
		

		log.info("PaypalCreateOrderRequest as json: {}",reqAsJson);



		HttpRequest httpRquest = HttpRequest.builder()
				.httpMethod(HttpMethod.POST)
				.url(createOrderUrl)
				.httpHeaders(customHttpHeaders)
				.body(reqAsJson)
				.build();
		return httpRquest;
	}

	public PaypalOrderResponse processCreateOrderResponse(
	        ResponseEntity<String> httpResponse) {

	    log.info("Processing PayPal Create Order response");

	    // =========================
	    // SUCCESS - 2xx
	    // =========================
	    if (httpResponse.getStatusCode().is2xxSuccessful()) {

	        PaypalOrderResponse paypalResponseObj = jsonUtil.convertJsonToObject(
	                        httpResponse.getBody(),
	                        PaypalOrderResponse.class);

	        log.info(
	                "Converted PayPal response JSON to PaypalOrderResponse: {}",
	                paypalResponseObj);

	        // Create Order specific success definition
	        if (paypalResponseObj != null
	                && paypalResponseObj.getId() != null
	                && "PAYER_ACTION_REQUIRED"
	                        .equalsIgnoreCase(paypalResponseObj.getStatus())) {

	            log.info(
	                    "PayPal Create Order successful. orderId={}, status={}",
	                    paypalResponseObj.getId(),
	                    paypalResponseObj.getStatus());

	            return paypalResponseObj;
	        }

	        // 2xx but business response is not what we expect
	        log.error("PayPal Create Order returned 2xx but response is invalid: {}",paypalResponseObj);

	        throw new PaypalProviderException(
	                ErrorCodeEnum.PAYPAL_CREATE_ORDER_FAILED.getErrorCode(),
	                ErrorCodeEnum.PAYPAL_CREATE_ORDER_FAILED.getErrorCode(),	               
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

	        PaypalError errorObj =jsonUtil.convertJsonToObject(
	                        httpResponse.getBody(),
	                        PaypalError.class);

	        if (errorObj != null) {

	            String errorMessage = generateErrorMessage(errorObj);

	            log.error(
	                    "PayPal error details: {}",
	                    errorMessage);

	            throw new PaypalProviderException(
	                    ErrorCodeEnum.PAYPAL_ERROR.getErrorCode(),
	                    errorMessage,
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

	        throw new PaypalProviderException(
	                ErrorCodeEnum.PAYPAL_ERROR.getErrorCode(),
	                ErrorCodeEnum.PAYPAL_ERROR.getErrorMessage(),
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

	    throw new PaypalProviderException(
	            ErrorCodeEnum.PAYPAL_ERROR.getErrorCode(),
	            ErrorCodeEnum.PAYPAL_ERROR.getErrorMessage(),
	            HttpStatus.INTERNAL_SERVER_ERROR
	    );
	}

	private String generateErrorMessage(PaypalError error) {

	    if (error == null) {
	        return "PayPal request failed";
	    }

	    StringJoiner joiner = new StringJoiner(" | ");

	    // Top-level fields
	    Arrays.stream(PaypalError.class.getDeclaredFields())
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

	            Arrays.stream(PaypalError.Detail.class.getDeclaredFields())
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