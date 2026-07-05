package com.bmad.expensetracker.repository;

import com.bmad.expensetracker.entity.Transaction;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findTopByOrderByTransactionDateDescIdDesc();
}
