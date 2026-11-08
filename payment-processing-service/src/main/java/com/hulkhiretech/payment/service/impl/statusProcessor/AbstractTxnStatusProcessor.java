
package com.hulkhiretech.payment.service.impl.statusProcessor;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.hulkhiretech.payment.constant.TxnStatusEnum;
import com.hulkhiretech.payment.dto.TransactionDTO;
import com.hulkhiretech.payment.entity.TransactionEntity;
import com.hulkhiretech.payment.entity.TransactionLogEntity;
import com.hulkhiretech.payment.repository.interfaces.TransactionLogRepository;
import com.hulkhiretech.payment.repository.interfaces.TransactionRepository;
import com.hulkhiretech.payment.service.interfaces.TxnStatusProcessor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractTxnStatusProcessor implements TxnStatusProcessor {

    protected final ModelMapper modelMapper;
    protected final TransactionRepository transactionRepository;

    private final List<TxnStatusEnum> finalStates =
            List.of(
                    TxnStatusEnum.SUCCESS,
                    TxnStatusEnum.FAILED
            );
    
    @Autowired
    private TransactionLogRepository transactionLogRepository;

    public AbstractTxnStatusProcessor(
            ModelMapper modelMapper,
            TransactionRepository transactionRepository) {

        this.modelMapper = modelMapper;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public TransactionDTO processStatus(TransactionDTO newDto) {

        log.info(
                "Processing status in AbstractTxnStatusProcessor, dto={}",
                newDto
        );
        
        log.info("transacationLogRepsitory:{}",transactionLogRepository);

        // Step 1: Get existing transaction from DB
        TransactionEntity existingEntity =transactionRepository.getTxnByTxnReference(newDto.getTxnReference()
                );

        // Convert current status ID to enum
        TxnStatusEnum existingStatusEnum =existingEntity != null ?
        		TxnStatusEnum.getById(existingEntity.getTxnStatusId()): null;

        // Convert new status name to enum
        TxnStatusEnum newStatusEnum =TxnStatusEnum.getByName(newDto.getTxnStatus());

        // Step 1: If previous status and new status are same,
        // do not update
        
        checkSameStatus(newDto, existingStatusEnum, newStatusEnum);

        // Step 2: If previous status is SUCCESS or FAILED,
        // do not process further
        checkExistingFinalStatus(newDto, existingStatusEnum);

        // Step 3: Process status-specific logic
        newDto = processStatusInternal(newDto);

        log.info( "STEP 4 completed. Status processed in processStatusInternal, updated dto={}", newDto);

        // Step 4: Insert row into transaction log table
        logTxnStatusChange(newDto, existingStatusEnum, newStatusEnum);
        //Step 5 For final raise kafka event
        log.info("Succesfully processed returning final" + "dto:{}",newDto);

        return newDto;
    }

	private void logTxnStatusChange(TransactionDTO newDto, TxnStatusEnum existingStatusEnum,
			TxnStatusEnum newStatusEnum) {
		TransactionLogEntity logEntity = TransactionLogEntity.builder()
                .transactionId(newDto.getId())
                .txnFromStatus(
                        existingStatusEnum == null
                                ? "NA"
                                : existingStatusEnum.getName()
                )
                .txnToStatus(newStatusEnum.getName())
                .build();

        int logId = transactionLogRepository.insertTxnLog(logEntity);

        log.info(
                "Transaction log inserted successfully. logId={}, transactionId={}",
                logId,
                newDto.getId()
        );
	}

	private void checkExistingFinalStatus(TransactionDTO newDto, TxnStatusEnum existingStatusEnum) {
		if (existingStatusEnum != null && finalStates.contains(existingStatusEnum)) {
        	log.error("Previous status is final. txnReference={}", newDto.getTxnReference());

            throw new RuntimeException(
                    "Previous status is final for txnReference: "
                    +"transaction: "+ newDto.getTxnReference()+ ". No further processing needed");
        }
	}

	private void checkSameStatus(TransactionDTO newDto, TxnStatusEnum existingStatusEnum, TxnStatusEnum newStatusEnum) {
		if (existingStatusEnum != null && existingStatusEnum == newStatusEnum) {

            log.error("Previous status and new status are same. txnReference={}",
                    newDto.getTxnReference());

            throw new RuntimeException("Previous status and new status are same for txnReference: "
                    + newDto.getTxnReference()+ ". No update needed");
        }
	}

    public abstract TransactionDTO processStatusInternal(
            TransactionDTO dto
    );
}

