package org.banking.service;

import jakarta.transaction.Transactional;
import org.banking.config.RabbitMQConfig;
import org.banking.entities.UserInfo;
import org.banking.entities.UserTransaction;
import org.banking.repository.UserRepository;
import org.banking.repository.UserTransactionRepository;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTransactionRepository transactionRepository;

    @Autowired
    private AmqpTemplate rabbitTemplate;

    /**
     * Queue transaction for asynchronous processing
     */
    public String queueTransaction(UserTransaction transaction) {
        Object response = rabbitTemplate.convertSendAndReceive(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.TXN_ROUTING_KEY,
                transaction
        );
        if (response != null) {
            return response.toString();  // This will be your final result message
        } else {
            return "Transaction failed or timed out.";
        }
    }


    /**
     * Actual transaction logic executed by the consumer
     */
    @Transactional
    public String processTransaction(UserTransaction transaction) {
        try {
            double amount = transaction.getAmount();
            UserInfo sender = transaction.getSender();
            UserInfo receiver = transaction.getReceiver();

            if (sender.getAccountBalance() < amount) {
                return "Account balance is lower than the transaction amount.";
            }

            // Update balances
            sender.setAccountBalance(sender.getAccountBalance() - amount);
            receiver.setAccountBalance(receiver.getAccountBalance() + amount);

            // Persist updates
            userRepository.save(sender);
            userRepository.save(receiver);

            transaction.setStatus(true);
            transactionRepository.save(transaction);

            return "Transaction processed successfully";
        } catch (Exception e) {
            e.printStackTrace();
            return "Transaction processing failed";
        }
    }

    /**
     * Get account balance of logged-in user
     */
    public Object getBalance(String username) {
        try {
            UserInfo userInfo = userRepository.findByUsername(username);
            return userInfo.getAccountBalance();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    /**
     * Get all transactions where user is sender or receiver
     */
    public Object getTransactions(String username) {
        try {
            UserInfo userInfo = userRepository.findByUsername(username);
            return transactionRepository.findBySenderOrReceiver(userInfo, userInfo);
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
