package com.ldjuric.saga.accounting;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface AccountingTransactionRepository extends CrudRepository<AccountingTransactionEntity, Integer> {

    @Query("select at from AccountingTransactionEntity at WHERE at.orderID = :orderID")
    Optional<AccountingTransactionEntity> findByOrderID(@Param("orderID")int orderID);
}
