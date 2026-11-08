package com.hulkhiretech.payments.constant;

public class Constant {
	public static final String PAYER_ACTION_REQUIRED =
	        "PAYER_ACTION_REQUIRED";
	public  static final String CLIENT_CREDENTIALS = "client_credentials";

	public  static final String GRANT_TYPE = "grant_type";

	// HTTP header names
	public static final String PAYPAL_REQUEST_ID_HEADER = "PayPal-Request-Id";

	// Property keys (in application-*.properties)
	public static final String PAYPAL_CREATE_ORDER_URL_KEY = "paypal.create.order.url";

	// Defaults
	public static final String PAYPAL_CREATE_ORDER_URL_DEFAULT = "https://api-m.sandbox.paypal.com/v2/checkout/orders";
	public static final String INTENT_CAPTURE = "CAPTURE";
	public static final String PAYMENT_METHOD_PREFERENCE = "IMMEDIATE_PAYMENT_REQUIRED";
	public static final String LANDING_PAGE = "LOGIN";
	public static final String SHIPPING_PREFERENCE = "NO_SHIPPING";
	public static final String USER_ACTION = "PAY_NOW";
	public static final String DEFAULT_INVOICE_ID = "90210";


}
