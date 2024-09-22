package org.example.buggybank.repository;

import org.example.buggybank.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgedRepository extends JpaRepository<Budget, Long> {
}
