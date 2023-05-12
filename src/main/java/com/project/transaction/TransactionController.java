package com.project.transaction;


import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.net.URI;


@RestController
    @RequestMapping("/transactions")
    public class TransactionController {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable("id") int id) {
        Optional<Transaction> transaction = transactionRepository.findById(id);
        if (transaction.isPresent()) {
            return ResponseEntity.ok(transaction.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/seller_id/{id}")
    public ResponseEntity<Transaction> getTransactionBySellerId(@PathVariable("id") int id) {
        Optional<Transaction> transaction = transactionRepository.findBySeller(id);
        if (transaction.isPresent()) {
            return ResponseEntity.ok(transaction.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) {
        Transaction savedTransaction = transactionRepository.save(transaction);
        return ResponseEntity.created(URI.create("/transactions/" + savedTransaction.getId())).body(savedTransaction);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<String> updateTransactionStatus(
            @PathVariable("id") int id,
            @RequestParam("status") String status) {

        transactionRepository.updateStatusById(status,id);
        String message = "Transaction with ID " + id + " updated with status " + status;

        rabbitTemplate.convertAndSend("transaction-queue", message);

        return ResponseEntity.ok("Transaction status updated successfully");
    }

    }



