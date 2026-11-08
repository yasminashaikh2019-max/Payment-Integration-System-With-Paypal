package com.hulkhiretech.payments.service.helper;


import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.StringJoiner;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payments.constant.Constant;
import com.hulkhiretech.payments.constant.ErrorCodeEnum;
import com.hulkhiretech.payments.exception.PaypalProviderException;
import com.hulkhiretech.payments.http.HttpRequest;
import com.hulkhiretech.payments.paypal.PaypalError;
import com.hulkhiretech.payments.paypal.PaypalOrderResponse;
import com.hulkhiretech.payments.util.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CaptureOrderHelper {

    @Value("${paypal.captureOrder.url}")
    private String captureOrderUrl;
	private static final String COMPLETED = "COMPLETED";

    private final JsonUtil jsonUtil;

    public HttpRequest prepareCaptureOrderPaypalRequest(
            String providerReference) {

        String captureUrl = captureOrderUrl
                        + "/"
                        + providerReference
                        + "/capture";

        HttpHeaders headers = new HttpHeaders();

        headers.add(
                Constant.PAYPAL_REQUEST_ID_HEADER,
                UUID.randomUUID().toString());

        headers.setContentType(MediaType.APPLICATION_JSON);

        // Optional
        headers.add("Prefer", "return=representation");

        return HttpRequest.builder()
                .httpMethod(HttpMethod.POST)
                .url(captureUrl)
                .httpHeaders(headers)
                .build();
    }
    public PaypalOrderResponse processCaptureOrderResponse(
            ResponseEntity<String> httpResponse) {

        log.info("Processing PayPal Capture Order response");

        // SUCCESS
        if (httpResponse.getStatusCode().is2xxSuccessful()) {

            PaypalOrderResponse paypalResponseObj =
                    jsonUtil.convertJsonToObject(
                            httpResponse.getBody(),
                            PaypalOrderResponse.class);

            log.info(
                    "Converted PayPal Capture response to PaypalOrderResponse: {}",
                    paypalResponseObj);

            if (paypalResponseObj != null
                    && paypalResponseObj.getId() != null
                    && "COMPLETED".equalsIgnoreCase(
                            paypalResponseObj.getStatus())) {

                log.info(
                        "PayPal Capture Order successful. orderId={}, status={}",
                        paypalResponseObj.getId(),
                        paypalResponseObj.getStatus());

                return paypalResponseObj;
            }

            log.error(
                    "PayPal Capture returned 2xx but response is invalid: {}",
                    paypalResponseObj);

            throw new PaypalProviderException(
                    ErrorCodeEnum.PAYPAL_ERROR.getErrorCode(),
                    "Invalid response received from PayPal Capture Order",
                    HttpStatus.BAD_GATEWAY);
        }

        // PAYPAL BUSINESS ERROR
        if (httpResponse.getStatusCode().is4xxClientError()
                || httpResponse.getStatusCode().is5xxServerError()) {

            log.error(
                    "PayPal Capture Order failed. status={}, response={}",
                    httpResponse.getStatusCode(),
                    httpResponse.getBody());

            PaypalError errorObj =
                    jsonUtil.convertJsonToObject(
                            httpResponse.getBody(),
                            PaypalError.class);

            log.info("Paypal Error Object: {}", errorObj);

            String errorMessage = generateErrorMessage(errorObj);

            log.error(
                    "PayPal Capture error details: {}",
                    errorMessage);

            throw new PaypalProviderException(
                    ErrorCodeEnum.PAYPAL_ERROR.getErrorCode(),
                    errorMessage,
                    HttpStatus.valueOf(
                            httpResponse.getStatusCode().value()));
        }

        // UNEXPECTED STATUS
        throw new PaypalProviderException(
                ErrorCodeEnum.PAYPAL_CAPTURE_ORER_ERROR.getErrorCode(),
                "Unexpected response received from PayPal Capture Order",
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
	private String generateErrorMessage(PaypalError errorObj) {

		    if (errorObj == null) {
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
		                            errorObj));

		    // Details fields
		    if (errorObj.getDetails() != null) {

		        for (int i = 0; i < errorObj.getDetails().size(); i++) {

		            PaypalError.Detail detail =
		                    errorObj.getDetails().get(i);

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