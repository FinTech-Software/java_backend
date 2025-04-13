package org.banking.service;

import jakarta.transaction.Transactional;
import org.banking.entities.UserInfo;
import org.banking.entities.UserTransaction;
import org.banking.repository.UserRepository;
import org.banking.repository.UserTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTransactionRepository transactionRepository;

    @Transactional
    public String processTransaction(UserTransaction transaction) {
        try{
            //Fetching all the account balance
            double senderBalance = transaction.getSender().getAccountBalance();
            double receiverBalance = transaction.getReceiver().getAccountBalance();
            double amount = transaction.getAmount();

            //Checking for insufficient balance
            if(senderBalance<amount){
                return "Account balance is lower than the transaction amount.";
            }
            //Starting a transaction
            transaction.getSender().setAccountBalance(senderBalance-amount);
            transaction.getReceiver().setAccountBalance(receiverBalance+amount);

            //Save updated user information back to the repository
            userRepository.save(transaction.getSender());
            userRepository.save(transaction.getReceiver());

            // Save the transaction
            transaction.setStatus(true);
            transactionRepository.save(transaction);
            return "Transaction processed successfully";
        } catch (Exception e) {
            return "Transaction processing failed";
        }
    }

    public Object getBalance(String username) {
        try{
            UserInfo userInfo = userRepository.findByUsername(username);
            return userInfo.getAccountBalance();
        } catch (RuntimeException e) {
            return e.getMessage();
        }
    }

    public Object getTransactions(String username) {
        try{
            UserInfo userInfo = userRepository.findByUsername(username);
            return transactionRepository
                    .findBySenderOrReceiver(userInfo, userInfo);
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
