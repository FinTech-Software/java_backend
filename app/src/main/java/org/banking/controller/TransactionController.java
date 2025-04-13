package org.banking.controller;

import org.banking.entities.UserInfo;
import org.banking.entities.UserTransaction;
import org.banking.repository.UserRepository;
import org.banking.request.TransactionRequestDTO;
import org.banking.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("v1/transaction/send-money")
    public ResponseEntity<?> sendMoney(@RequestBody TransactionRequestDTO request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No JWT token provided or token is invalid.");
            }

            UserInfo sender = userRepository.findById(request.getSender())
                    .orElseThrow(() -> new RuntimeException("Sender not found"));

            UserInfo receiver = userRepository.findById(request.getReceiver())
                    .orElseThrow(() -> new RuntimeException("Receiver not found"));

            String senderId = sender.getId().toString().substring(0,8);
            String timestamp = LocalDateTime.now().toString().replaceAll("[-T:.]", ""); // Clean format
            String customId = "TXN_" + senderId + "_" + timestamp;
            // Build UserTransaction
            UserTransaction transaction = new UserTransaction();
            transaction.setId(customId);
            transaction.setSender(sender);
            transaction.setReceiver(receiver);
            transaction.setAmount(request.getAmount());
            transaction.setStatus(request.getStatus());
            transaction.setDate(LocalDateTime.now());

            String transaction_status = String.valueOf(transactionService.processTransaction(transaction));
            return ResponseEntity.ok(transaction_status);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Exception occurred in the Transaction service: " + e.getMessage());
        }
    }

    @GetMapping("v1/transaction/request-balance")
    public ResponseEntity<?> requestBalance() {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No JWT token provided or token is invalid.");
            }
            String username = authentication.getName();

            Object acc_balance = transactionService.getBalance(username);
            return ResponseEntity.ok(acc_balance);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Exception occurred in the Transaction service: " + e.getMessage());
        }
    }

    @GetMapping("v1/transaction/getTransactionList")
    public ResponseEntity<?> getTransactionList() {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No JWT token provided or token is invalid.");
            }
            String username = authentication.getName();

            Object acc_balance = transactionService.getTransactions(username);
            return ResponseEntity.ok(acc_balance);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Exception occurred in the Transaction service: " + e.getMessage());
        }
    }
}
