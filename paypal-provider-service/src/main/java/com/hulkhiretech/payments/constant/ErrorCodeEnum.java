package com.hulkhiretech.payments.constant;

public enum ErrorCodeEnum {
	
	INVALID_NAME("30001", "Name cannot be null"),
	GENERIC_ERROR("30000", "Unable to process the request please try again later"),
	UNABLE_TO_CONNECT_TO_EXTERNAL_SERVICE("30002","Unable to connect to external service."),
	PAYPAL_CREATE_ORDER_FAILED("30003","fAILED TO CREATE ORDER WITH PAYAPL.Please try again"),
	PAYPAL_ERROR("30004","Paypal Error.Prepare mesag dynamically from paypal response"),
	PAYPAL_CAPTURE_ORER_ERROR("30006","<PREPARED MESSAGE Dynamically.>");
	private String errorCode;
	private String errorMessage;
	
	ErrorCodeEnum(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
	
	public String getErrorCode() {
		return errorCode;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
}
