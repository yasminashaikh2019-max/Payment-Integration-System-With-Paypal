package com.hulkhiretech.payment.service.interfaces;

import com.hulkhiretech.payment.dto.TransactionDTO;

public interface TxnStatusProcessor {

    /**
     * Process transaction status for the given transaction DTO.
     * @param dto transaction data transfer object
     * @return processed transaction DTO
     */
    TransactionDTO processStatus(TransactionDTO dto);

}
