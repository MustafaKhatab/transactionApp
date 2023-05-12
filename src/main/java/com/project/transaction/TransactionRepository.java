package com.project.transaction;

import jakarta.persistence.Table;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
public interface TransactionRepository  extends JpaRepository<Transaction,String> {
    @Query(value = "SELECT t FROM Transaction t WHERE t.seller_id=1")
    Optional<Transaction> findBySeller(int seller_id);

    @Query(value = "SELECT t FROM Transaction t WHERE t.id=1")
    Optional<Transaction> findById(int id);

}
