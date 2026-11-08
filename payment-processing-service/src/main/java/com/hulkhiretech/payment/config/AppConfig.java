package com.hulkhiretech.payment.config;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.TimeValue;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import com.hulkhiretech.payment.dto.TransactionDTO;
import com.hulkhiretech.payment.entity.TransactionEntity;
import com.hulkhiretech.payment.util.modelMapper.converter.idtoname.PaymentMethodEnumIdToNameConverter;
import com.hulkhiretech.payment.util.modelMapper.converter.idtoname.PaymentTypeEnumIdToNameConverter;
import com.hulkhiretech.payment.util.modelMapper.converter.idtoname.ProviderEnumIdToNameConverter;
import com.hulkhiretech.payment.util.modelMapper.converter.idtoname.TxnStatusEnumIdToNameConverter;
import com.hulkhiretech.payment.util.modelMapper.converter.nametoid.PaymentMethodEnumConverter;
import com.hulkhiretech.payment.util.modelMapper.converter.nametoid.PaymentTypeEnumConverter;
import com.hulkhiretech.payment.util.modelMapper.converter.nametoid.ProviderEnumConverter;
import com.hulkhiretech.payment.util.modelMapper.converter.nametoid.TxnStatusEnumConverter;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {

        ModelMapper mapper = new ModelMapper();

        mapper.getConfiguration()
              .setMatchingStrategy(MatchingStrategies.STRICT);

        /*
         * DTO -> ENTITY
         */

        Converter<String, Integer> paymentMethodConverter =
                new PaymentMethodEnumConverter();

        Converter<String, Integer> providerConverter =
                new ProviderEnumConverter();

        Converter<String, Integer> paymentTypeConverter =
                new PaymentTypeEnumConverter();

        Converter<String, Integer> txnStatusConverter =
                new TxnStatusEnumConverter();

        mapper.addMappings(
                new PropertyMap<TransactionDTO, TransactionEntity>() {

                    @Override
                    protected void configure() {

                        using(paymentMethodConverter)
                                .map(source.getPaymentMethod())
                                .setPaymentMethodId(null);

                        using(providerConverter)
                                .map(source.getProvider())
                                .setProviderId(null);

                        using(paymentTypeConverter)
                                .map(source.getPaymentType())
                                .setPaymentTypeId(null);

                        using(txnStatusConverter)
                                .map(source.getTxnStatus())
                                .setTxnStatusId(null);
                    }
                });

        /*
         * ENTITY -> DTO
         */

        Converter<Integer, String> paymentMethodIdToNameConverter =
                new PaymentMethodEnumIdToNameConverter();

        Converter<Integer, String> providerIdToNameConverter =
                new ProviderEnumIdToNameConverter();

        Converter<Integer, String> paymentTypeIdToNameConverter =
                new PaymentTypeEnumIdToNameConverter();

        Converter<Integer, String> txnStatusIdToNameConverter =
                new TxnStatusEnumIdToNameConverter();

        mapper.addMappings(
                new PropertyMap<TransactionEntity, TransactionDTO>() {

                    @Override
                    protected void configure() {

                        using(paymentMethodIdToNameConverter)
                                .map(source.getPaymentMethodId())
                                .setPaymentMethod(null);

                        using(providerIdToNameConverter)
                                .map(source.getProviderId())
                                .setProvider(null);

                        using(paymentTypeIdToNameConverter)
                                .map(source.getPaymentTypeId())
                                .setPaymentType(null);

                        using(txnStatusIdToNameConverter)
                                .map(source.getTxnStatusId())
                                .setTxnStatus(null);
                    }
                });

        return mapper;
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