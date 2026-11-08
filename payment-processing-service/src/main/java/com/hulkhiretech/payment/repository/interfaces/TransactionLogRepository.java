package com.hulkhiretech.payment.repository.interfaces;

import com.hulkhiretech.payment.entity.TransactionLogEntity;

public interface TransactionLogRepository {
	int insertTxnLog(TransactionLogEntity entity);

}
