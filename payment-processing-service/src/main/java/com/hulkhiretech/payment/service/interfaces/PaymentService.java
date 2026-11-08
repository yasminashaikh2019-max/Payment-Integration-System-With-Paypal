package com.hulkhiretech.payment.service.interfaces;

import com.hulkhiretech.payment.pojo.CreateTxnReq;
import com.hulkhiretech.payment.pojo.InitiateTxnReq;
import com.hulkhiretech.payment.pojo.PaymentResponse;

public interface PaymentService {
	
	PaymentResponse createPayment(CreateTxnReq createTxnReq);
	PaymentResponse initiatePayment(String txnReference, InitiateTxnReq initiateTxnReq);
	PaymentResponse capturePayment(String txnReference);

	
}
