package com.hulkhiretech.payments.pojo;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)

public class OrderResponse {
	private String orderId;
	private String paypalStatus;
	private String redirectUrl;
	

}
