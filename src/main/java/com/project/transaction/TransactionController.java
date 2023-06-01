//package com.project.transaction;
//
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Optional;
//import java.net.URI;
//
//
//@RestController
//    @RequestMapping("/transactions")
//    public class TransactionController {
//
//    @Autowired
//    private TransactionRepository transactionRepository;
//    @Autowired
//    private RabbitTemplate rabbitTemplate;
//
//    @GetMapping("/{id}")
//    public ResponseEntity<Transaction> getTransactionById(@PathVariable("id") int id) {
//        Optional<Transaction> transaction = transactionRepository.findById(id);
//        if (transaction.isPresent()) {
//            return ResponseEntity.ok(transaction.get());
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//    @GetMapping("/seller_id/{id}")
//    public ResponseEntity<Transaction> getTransactionBySellerId(@PathVariable("id") int id) {
//        Optional<Transaction> transaction = transactionRepository.findBySeller(id);
//        if (transaction.isPresent()) {
//            return ResponseEntity.ok(transaction.get());
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//    @GetMapping("/buyer_id/{id}")
//    public ResponseEntity<Transaction> getTransactionByBuyerId(@PathVariable("id") int id) {
//        Optional<Transaction> transaction = transactionRepository.findByBuyer(id);
//        if (transaction.isPresent()) {
//            return ResponseEntity.ok(transaction.get());
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//    @PostMapping
//    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) {
//        Transaction savedTransaction = transactionRepository.save(transaction);
//        return ResponseEntity.created(URI.create("/transactions/" + savedTransaction.getId())).body(savedTransaction);
//    }
//
//    @PatchMapping("/{id}/status")
//    public ResponseEntity<String> updateTransactionStatus(
//            @PathVariable("id") int id,
//            @RequestParam("status") String status) {
//
//        transactionRepository.updateStatusById(status,id);
//        String message = "Transaction with ID " + id + " updated with status " + status;
//
//        rabbitTemplate.convertAndSend("transaction-queue", message);
//
//        return ResponseEntity.ok("Transaction status updated successfully");
//    }
//
//    }
//
//
//

package com.project.transaction;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/{id}")
    @Cacheable(value = "transactions", key = "#id")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable("id") int id) {
        Optional<Transaction> transaction = transactionRepository.findById(id);
        if (transaction.isPresent()) {
            return ResponseEntity.ok(transaction.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/seller_id/{id}")
    @Cacheable(value = "transactions", key = "'seller:' + #id")
    public ResponseEntity<Transaction> getTransactionBySellerId(@PathVariable("id") int id) {
        Optional<Transaction> transaction = transactionRepository.findBySeller(id);
        if (transaction.isPresent()) {
            return ResponseEntity.ok(transaction.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/buyer_id/{id}")
    @Cacheable(value = "transactions", key = "'buyer:' + #id")
    public ResponseEntity<Transaction> getTransactionByBuyerId(@PathVariable("id") int id) {
        Optional<Transaction> transaction = transactionRepository.findByBuyer(id);
        if (transaction.isPresent()) {
            return ResponseEntity.ok(transaction.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @CachePut(value = "transactions", key = "#result.id")
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) {
        Transaction savedTransaction = transactionRepository.save(transaction);
        return ResponseEntity.created(URI.create("/transactions/" + savedTransaction.getId())).body(savedTransaction);
    }

    @PatchMapping("/{id}/status")
    @CacheEvict(value = "transactions", key = "#id")
    public ResponseEntity<String> updateTransactionStatus(
            @PathVariable("id") int id,
            @RequestParam("status") String status) {

        transactionRepository.updateStatusById(status, id);
        String message = "Transaction with ID " + id + " updated with status " + status;

        rabbitTemplate.convertAndSend("transaction-queue", message);

        return ResponseEntity.ok("Transaction status updated successfully");
    }

}

