package com.hulkhiretech.payment.service.factory;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.hulkhiretech.payment.constant.TxnStatusEnum;
import com.hulkhiretech.payment.service.impl.statusProcessor.ApprovedStatusProcessor;
import com.hulkhiretech.payment.service.impl.statusProcessor.CreatedStatusProcessor;
import com.hulkhiretech.payment.service.impl.statusProcessor.FailedStatusProcessor;
import com.hulkhiretech.payment.service.impl.statusProcessor.InitiatedStatusProcessor;
import com.hulkhiretech.payment.service.impl.statusProcessor.PendingStatusProcessor;
import com.hulkhiretech.payment.service.impl.statusProcessor.SuccessStatusProcessor;
import com.hulkhiretech.payment.service.interfaces.TxnStatusProcessor;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TxnStatusFactory {

    private final ApplicationContext appContext;

    public TxnStatusProcessor getStatusProcessor(TxnStatusEnum statusEnum) {

        if (statusEnum == null) {
            throw new IllegalArgumentException("Status must not be null");
        }

        switch (statusEnum) {

            case CREATED:
                return appContext.getBean(CreatedStatusProcessor.class);

            case INITIATED:
                return appContext.getBean(InitiatedStatusProcessor.class);

            case PENDING:
                return appContext.getBean(PendingStatusProcessor.class);

            case APPROVED:
                return appContext.getBean(ApprovedStatusProcessor.class);

            case SUCCESS:
                return appContext.getBean(SuccessStatusProcessor.class);

            case FAILED:
                return appContext.getBean(FailedStatusProcessor.class);

            default:
                throw new IllegalArgumentException(
                        "Invalid transaction status: " + statusEnum);
        }
    }
}