package com.hulkhiretech.payment.notification;

import com.hulkhiretech.payment.dto.PaymentEvent;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventConsumer {

    @KafkaListener(
        topics = "payment-events",
        groupId = "notification-group"
    )
    public void consumePaymentEvent(PaymentEvent event) {

        System.out.println(
            "Payment Event Received: "
            + event.getMerchantTxnRef()
        );

        if ("SUCCESS".equals(event.getStatus())) {
            System.out.println("Send payment success email");
        }
    }
}