package com.hulkhiretech.payment.constant;

public enum ErrorCodeEnum {

    INVALID_NAME("20001", "Name cannot be null"),
    GENERIC_ERROR("20000", "Unable to process the request. Please try again later."),
    UNABLE_TO_CONNECT_TO_EXTERNAL_SERVICE("20002", "Unable to connect to external service."),
    PAYPAL_CREATE_ORDER_FAILED("20003", "Failed to create order with PayPal. Please try again."),
    PAYPAL_CREATE_ORDER_ERROR("20004", "An error occurred while creating the PayPal order."),
    PAYPAL_CAPTURE_ORDER_FAILED("20005", "Failed to capture order with PayPal. Please try again."),
    PAYPAL_CAPTURE_ORDER_ERROR("20006", "An error occurred while capturing the PayPal order."),
    ERROR_CALLING_CREATE_ORDER("20007", "Error occurred while calling create order."),
    UNABLE_TO_CONNECT_TO_PAYPAL_PROVIDER("20008", "Unable to connect to PayPal provider.");

    private final String errorCode;
    private final String errorMessage;

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