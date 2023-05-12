package com.project.transaction;


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

        @GetMapping("/{id}")
        public ResponseEntity<Transaction> getTransactionById(@PathVariable("id") int id) {
            Optional<Transaction> transaction = transactionRepository.findById(id);
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

        // Other endpoints for retrieving, updating, and deleting transactions

    }



