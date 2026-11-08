package com.hulkhiretech.payment.entity;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionLogEntity {
	private Integer id;
	private Integer transactionId;
	private String txnFromStatus;
	private String txnToStatus; 
	private LocalDateTime createdAt; 
	private LocalDateTime updatedAt;

}
