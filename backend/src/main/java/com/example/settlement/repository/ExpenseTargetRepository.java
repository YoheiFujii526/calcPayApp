package com.example.settlement.repository;

import com.example.settlement.entity.ExpenseTarget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseTargetRepository extends JpaRepository<ExpenseTarget, Long> {
    List<ExpenseTarget> findByExpenseId(Long expenseId);

    void deleteByExpenseId(Long expenseId);
}
