package com.hulkhiretech.payment.service.impl.statusProcessor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payment.dto.TransactionDTO;
import com.hulkhiretech.payment.entity.TransactionEntity;
import com.hulkhiretech.payment.repository.interfaces.TransactionRepository;
import com.hulkhiretech.payment.service.interfaces.TxnStatusProcessor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ApprovedStatusProcessor extends AbstractTxnStatusProcessor {

    public ApprovedStatusProcessor(
            TransactionRepository transactionRepository,
            ModelMapper modelMapper) {

        super(modelMapper, transactionRepository);
    }

    @Override
    public TransactionDTO processStatusInternal(TransactionDTO dto) {

        log.info("Processing transaction status in ApprovedStatusProcessor. dto={}", dto);

        TransactionEntity entity =
                modelMapper.map(dto, TransactionEntity.class);

        log.info("Mapped TransactionEntity from DTO: {}", entity);

        boolean isUpdate =
                transactionRepository.updateTxnDetails(entity);

        log.info("updateTxnDetails returned: {}", isUpdate);

        if (!isUpdate) {

            throw new RuntimeException(
                    "Failed to update transaction details for txnReference="
                            + dto.getTxnReference());
        }

        return dto;
    }
}