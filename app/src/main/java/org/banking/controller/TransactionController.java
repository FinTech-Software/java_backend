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
@RequestMapping("v1/transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/send-money")
    public ResponseEntity<?> sendMoney(@RequestBody TransactionRequestDTO request) {
        try {
            // Transaction received from client
            UserInfo sender = userRepository.findById(request.getSender())
                    .orElseThrow(() -> new RuntimeException("Sender not found"));
            UserInfo receiver = userRepository.findById(request.getReceiver())
                    .orElseThrow(() -> new RuntimeException("Receiver not found"));

            // Create custom transaction ID
            String customId = "TXN_" + sender.getId().toString() + "_" + LocalDateTime.now().toString().replaceAll("[-T:.]", "");

            // Prepare transaction object
            UserTransaction transaction = new UserTransaction();
            transaction.setId(customId);
            transaction.setSender(sender);
            transaction.setReceiver(receiver);
            transaction.setAmount(request.getAmount());
            transaction.setStatus(false);
            transaction.setDate(LocalDateTime.now());

            // Send transaction to RabbitMQ for asynchronous processing
            String message = transactionService.queueTransaction(transaction);

            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Exception occurred: " + e.getMessage());
        }
    }


    @GetMapping("/request-balance")
    public ResponseEntity<?> requestBalance() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("No JWT token provided or token is invalid.");
            }

            String username = authentication.getName();
            Object balance = transactionService.getBalance(username);
            return ResponseEntity.ok(balance);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Exception occurred while fetching balance: " + e.getMessage());
        }
    }

    @GetMapping("/getTransactionList")
    public ResponseEntity<?> getTransactionList() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("No JWT token provided or token is invalid.");
            }

            String username = authentication.getName();
            Object transactions = transactionService.getTransactions(username);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Exception occurred while fetching transactions: " + e.getMessage());
        }
    }
}
