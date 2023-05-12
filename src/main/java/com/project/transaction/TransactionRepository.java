package com.project.transaction;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
public interface TransactionRepository  extends JpaRepository<Transaction,String> {
    @Query(value = "SELECT t FROM Transaction t WHERE t.seller_id = :seller_id")
    Optional<Transaction> findBySeller(@Param("seller_id") int seller_id);


    @Query(value = "SELECT t FROM Transaction t WHERE t.id=:id")
    Optional<Transaction> findById(@Param("id")int id);

    @Query(value = "SELECT t FROM Transaction t WHERE t.buyer_id=:id")
    Optional<Transaction> findByBuyer(@Param("id")int id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE transactions SET status = :status WHERE id = :id", nativeQuery = true)
    void updateStatusById(@Param("status") String status, @Param("id") int id);

}
