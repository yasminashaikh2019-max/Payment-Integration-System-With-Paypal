package com.hulkhiretech.payment.paypalprovider;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateOrderReq {
	private String returnUrl;
	private String cancelUrl;
	private String currencyCode;
	private BigDecimal amount;

}
