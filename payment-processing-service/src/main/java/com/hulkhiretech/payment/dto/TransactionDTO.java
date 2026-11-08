package com.hulkhiretech.payment.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionDTO {

    private Integer id;
    private String userId;
    private String paymentMethod;
    private String provider;
    private String paymentType;
    private String txnStatus;
    private BigDecimal amount;
    private String currency;
    private String merchantTransactionReference;
    private String txnReference;
    private String providerReference;
    private String errorCode;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer retryCount;
}