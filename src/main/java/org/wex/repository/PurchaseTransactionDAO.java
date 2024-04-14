package org.wex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.wex.entity.PurchaseTransaction;

@Repository
public interface PurchaseTransactionDAO extends JpaRepository<PurchaseTransaction, Long> {
}
