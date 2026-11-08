package com.hulkhiretech.payments.config;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.TimeValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class AppConfig {

    @Bean
    public ScheduledExecutorService scheduledExecutorService() {

        return Executors.newSingleThreadScheduledExecutor();
    }

    @Bean
    public RestClient restClient(RestClient.Builder builder) {

        PoolingHttpClientConnectionManager connectionManager =
                new PoolingHttpClientConnectionManager();

        connectionManager.setMaxTotal(200);
        connectionManager.setDefaultMaxPerRoute(50);

        connectionManager.setConnectionConfigResolver(
                route -> ConnectionConfig.custom()
                        .setValidateAfterInactivity(TimeValue.ofSeconds(5))
                        .build()
        );

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .evictIdleConnections(TimeValue.ofSeconds(30))
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory(httpClient);

        requestFactory.setConnectTimeout(10000);
        requestFactory.setReadTimeout(15000);
        requestFactory.setConnectionRequestTimeout(10000);

        return builder
                .requestFactory(requestFactory)
                .build();
    }
}

