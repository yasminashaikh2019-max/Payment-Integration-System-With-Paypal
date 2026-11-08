package com.hulkhiretech.payment.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentResponse {

    private String txnReference;
    private String TxnStatus;
    
    private String providerReference;
    private String redirectUrl;

}
