package com.hulkhiretech.payments.pojo;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateOrderReq {

	public String returnUrl;
	public String cancelUrl;
	public String currencyCode;
	public BigDecimal amount;

}
