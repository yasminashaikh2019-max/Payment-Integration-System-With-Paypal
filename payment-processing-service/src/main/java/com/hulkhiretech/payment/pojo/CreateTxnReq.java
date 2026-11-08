package com.hulkhiretech.payment.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreateTxnReq {

    private Integer id;
    private String userId;
    private String paymentMethod;
    private String provider;
    private String paymentType;
    private BigDecimal amount;
    private String currency;
    private String merchantTransactionReference;
}