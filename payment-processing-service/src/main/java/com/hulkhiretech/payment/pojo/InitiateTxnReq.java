package com.hulkhiretech.payment.pojo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InitiateTxnReq {
	private String returnUrl;
 private String cancelUrl;
}
