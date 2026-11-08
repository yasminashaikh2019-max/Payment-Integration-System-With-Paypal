package com.hulkhiretech.payment.http;

import org.springframework.http.HttpHeaders;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import com.hulkhiretech.payment.constant.ErrorCodeEnum;
import com.hulkhiretech.payment.exception.ProcessingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class HttpServiceEngine {

    private final RestClient restClient;

    public ResponseEntity<String> makeHttpCall(HttpRequest httpRequest) {

        log.info("HttpServiceEngine.makeHttpCall called");
        log.info("HTTP method: {}", httpRequest.getHttpMethod());
        log.info("URL: {}", httpRequest.getUrl());

        try {

        	var requestSpec = restClient
        	        .method(httpRequest.getHttpMethod())
        	        .uri(httpRequest.getUrl())
        	        .headers((HttpHeaders restClientHeaders) ->
        	                restClientHeaders.addAll(
        	                        httpRequest.getHttpHeaders()));

        	if (httpRequest.getBody() != null) {
        	    requestSpec.body(httpRequest.getBody());
        	}

        	ResponseEntity<String> httpResponse = requestSpec
        	        .retrieve()
        	        .toEntity(String.class);
            log.info("HttpServiceEngine.makeHttpCall response: {}", httpResponse);

            return httpResponse;

        } catch (HttpClientErrorException | HttpServerErrorException e) {

            log.error("Http error occurred while making HTTP call: {}", e.getMessage(), e);

            return ResponseEntity.status(e.getStatusCode())
                    .body(e.getResponseBodyAsString());

        } catch (Exception e) {

            log.error("Exception occurred while making HttpCall: {}", e.getMessage(), e);
            
            //if service inavailable or gateway timeout throw paypal provider exception with error code and message
//if(e.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE || e.getStatusCode() == HttpStatus.GATEWAY_TIMEOUT)
	log.error("Paypal provider service is unavailable or gateway timeout: {}", e.getMessage(), e);
            throw new ProcessingException(
            		ErrorCodeEnum.UNABLE_TO_CONNECT_TO_EXTERNAL_SERVICE.getErrorCode(),
            		ErrorCodeEnum.UNABLE_TO_CONNECT_TO_EXTERNAL_SERVICE.getErrorMessage(),
            		HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
    }
}