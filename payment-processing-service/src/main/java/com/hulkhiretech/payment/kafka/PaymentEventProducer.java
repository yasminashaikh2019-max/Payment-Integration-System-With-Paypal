package com.hulkhiretech.payment.kafka;

import com.hulkhiretech.payment.dto.PaymentEvent;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventProducer {

    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    public PaymentEventProducer(
            KafkaTemplate<String, PaymentEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishPaymentEvent(PaymentEvent event) {

        kafkaTemplate.send(
            "payment-events",
            event.getMerchantTxnRef(),
            event
        );
    }
}