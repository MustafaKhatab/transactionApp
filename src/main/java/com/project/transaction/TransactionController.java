
package com.project.transaction;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/transactions")
@CacheConfig(cacheNames = "transactions")

public class TransactionController {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/{id}")
    //@Cacheable(value = "transactions", key = "#id")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable("id") int id) {
        Optional<Transaction> transaction = transactionRepository.findById(id);
        if (transaction.isPresent()) {
            return ResponseEntity.ok(transaction.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/seller_id/{id}")
    //@Cacheable(value = "transactions", key = "#seller_id")
    public ResponseEntity<Transaction> getTransactionBySellerId(@PathVariable("id") int id) {
        Optional<Transaction> transaction = transactionRepository.findBySeller(id);
        if (transaction.isPresent()) {
            return ResponseEntity.ok(transaction.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/buyer_id/{id}")
    //@Cacheable(value = "transactions", key = "#buyer_id")
    public ResponseEntity<Transaction> getTransactionByBuyerId(@PathVariable("id") int id) {
        Optional<Transaction> transaction = transactionRepository.findByBuyer(id);
        if (transaction.isPresent()) {
            return ResponseEntity.ok(transaction.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) {
        Transaction savedTransaction = transactionRepository.save(transaction);
        String message = savedTransaction.getAd_id()+":"+savedTransaction.getStatus();
        rabbitTemplate.convertAndSend("transaction-queue", message);
        return ResponseEntity.created(URI.create("/transactions/" + savedTransaction.getId())).body(savedTransaction);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<String> updateTransactionStatus(
            @PathVariable("id") int id,
            @RequestParam("status") String status) {

        Optional<Transaction> optionalTransaction = transactionRepository.findById(id);
        if (optionalTransaction.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Transaction transaction = optionalTransaction.get();
        int adId = transaction.getAd_id();

        transactionRepository.updateStatusById(status, id);

        String message = adId + ":" + status;

        rabbitTemplate.convertAndSend("transaction-queue", message);

        return ResponseEntity.ok("Transaction status updated successfully");
    }

}

