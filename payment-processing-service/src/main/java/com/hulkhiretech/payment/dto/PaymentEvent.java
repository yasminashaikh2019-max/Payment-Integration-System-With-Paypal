package com.hulkhiretech.payment.dto;

import java.math.BigDecimal;

import lombok.Data;
@Data
public class PaymentEvent {

    private String eventId;
    private String eventType;
    private String merchantTxnRef;
    private BigDecimal amount;
    private String currency;
    private String status;


}