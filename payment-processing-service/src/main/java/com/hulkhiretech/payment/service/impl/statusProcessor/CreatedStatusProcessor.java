package com.hulkhiretech.payment.service.impl.statusProcessor;

import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.pulsar.PulsarProperties.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSuspensionNotSupportedException;

import com.hulkhiretech.payment.dto.TransactionDTO;
import com.hulkhiretech.payment.entity.TransactionEntity;
import com.hulkhiretech.payment.repository.interfaces.TransactionRepository;
import com.hulkhiretech.payment.service.interfaces.TxnStatusProcessor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service

@Slf4j
public class CreatedStatusProcessor extends AbstractTxnStatusProcessor {

	public CreatedStatusProcessor(
			TransactionRepository transactionRepository, ModelMapper modelMapper) {
		super(modelMapper,transactionRepository);
	}
	
	@Override
	public TransactionDTO processStatusInternal(TransactionDTO dto) {
		log.info("Processing transaction status in CreatedStatusProcessor for txnId: , dto ={}",  dto);
		
		//map DTO to entity using  moddelMapper and log the entity
		TransactionEntity entity = modelMapper.map(dto, TransactionEntity.class);
		log.info("Mapped TransactionEntity from DTO:{}", entity);
		
		
		int pkId = transactionRepository.createTransaction(entity );
		log.info("createTransaction returned:{}", pkId);
		
		// Set the primary key ID returned from repository
		dto.setId(pkId);
		log.info("Transaction created with ID: {}", pkId);
		
		return dto;
	}

}
