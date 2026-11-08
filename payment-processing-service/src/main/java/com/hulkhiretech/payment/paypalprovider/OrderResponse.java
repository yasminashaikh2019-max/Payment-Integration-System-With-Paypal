package com.hulkhiretech.payment.paypalprovider;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data

public class OrderResponse {
	private String orderId;
	private String paypalStatus;
	private String redirectUrl;
	

}
