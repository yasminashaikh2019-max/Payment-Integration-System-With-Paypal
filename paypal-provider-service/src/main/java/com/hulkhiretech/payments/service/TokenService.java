package com.hulkhiretech.payments.service;

import java.util.concurrent.ScheduledExecutorService;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hulkhiretech.payments.constant.Constant;
import com.hulkhiretech.payments.http.HttpRequest;
import com.hulkhiretech.payments.http.HttpServiceEngine;
import com.hulkhiretech.payments.paypal.PaypalOAuthToken;
import com.hulkhiretech.payments.util.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    private static String accessToken;

    private final HttpServiceEngine httpServiceEngine;
    private final ScheduledExecutorService scheduler;

    @Value("${paypal.client.id}")
    private String paypalClientId;

    @Value("${paypal.client.secret}")
    private String paypalClientSecret;

    @Value("${paypal.oauth.url}")
    private String oauthurl;
    
    
    private final JsonUtil jsonUtil;

    public String getAccessToken() {

        log.info("getAccessToken called");

        // 1. Check whether token already exists
        if (accessToken != null) {

            log.info("Access token already exists.");

            return accessToken;
        }

        log.info("Access token does not exist, generating a new one...");

        HttpRequest httpRequest = prepareOAuthRequest();

        // 5. Make HTTP call
        ResponseEntity<String> response = httpServiceEngine.makeHttpCall(httpRequest);

        log.info(
                "HttpServiceEngine.makeHttpCall response: {}",
                response.getBody());
        
        PaypalOAuthToken token = jsonUtil.convertJsonToObject(response.getBody(), PaypalOAuthToken.class);
        
       

        
accessToken = token.getAccessToken();
       
        scheduledExpiryOfAccessToken(token);

       log.info("Successfully generated PayPal access token.");
        return accessToken;
    }

	private void scheduledExpiryOfAccessToken(PaypalOAuthToken token) {
		// -15 min is to ensure that hte token is cleared before it actually 
        //this will afvoid issue where we say token  is valid ,but paypal reject //
        // so we will clear the token 15 min before is actually expires.
        //put this 15 min in property file
        long reducedExpireInSec = token.getExpiresIn()-(15 * 60);

        // 6. Store access token
        accessToken = token.getAccessToken();

        // 7. Schedule token expiration
        scheduler.schedule(() -> {log.info("Access token expired, clearing the token");

            accessToken = null;

        }, reducedExpireInSec, TimeUnit.SECONDS);

        log.info("Access token will expire in {} seconds",
                reducedExpireInSec
        );
	}

	private HttpRequest prepareOAuthRequest() {
		// 2. Create HTTP headers
        HttpHeaders customHttpHeaders = new HttpHeaders();

        customHttpHeaders.setBasicAuth(
                paypalClientId,
                paypalClientSecret
        );

        customHttpHeaders.setContentType(
                MediaType.APPLICATION_FORM_URLENCODED
        );

        // 3. Create request body
        MultiValueMap<String, String> body =
                new LinkedMultiValueMap<>();

        body.add(
                Constant.GRANT_TYPE,
                Constant.CLIENT_CREDENTIALS
        );

        // 4. Create HttpRequest
        HttpRequest httpRequest = HttpRequest.builder()
                .httpMethod(HttpMethod.POST)
                .url(oauthurl)
                .httpHeaders(customHttpHeaders)
                .body(body)
                .build();
		return httpRequest;
	}
}