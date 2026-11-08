package com.hulkhiretech.payment.repository.interfaces;

import org.springframework.boot.autoconfigure.pulsar.PulsarProperties.Transaction;

import com.hulkhiretech.payment.entity.TransactionEntity;
import com.hulkhiretech.payment.entity.TransactionLogEntity;

public interface TransactionRepository {
    int createTransaction(TransactionEntity entity);
    
    TransactionEntity getTxnByTxnReference(String txnReference);
    
    boolean updateTxnDetails(TransactionEntity enity);



}
