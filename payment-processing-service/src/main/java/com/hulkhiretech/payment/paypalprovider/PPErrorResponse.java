package com.hulkhiretech.payment.paypalprovider;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PPErrorResponse {

    private String errorCode;

    private String errorMessage;

}