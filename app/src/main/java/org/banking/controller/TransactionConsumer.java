package org.banking.controller;

import org.banking.config.RabbitMQConfig;
import org.banking.entities.UserTransaction;
import org.banking.service.TransactionService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransactionConsumer {

    @Autowired
    private TransactionService transactionService;

    @RabbitListener(queues = RabbitMQConfig.TXN_QUEUE)
    public String receiveTransaction(UserTransaction transaction) {
        System.out.println("Transaction received by consumer: " + transaction);
        return transactionService.processTransaction(transaction);
    }
}
