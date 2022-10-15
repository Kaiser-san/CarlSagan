package com.ldjuric.saga.accounting;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Profile({"accounting", "all"})
public interface AccountingTransactionVersionFileRepository  extends CrudRepository<AccountingTransactionVersionFileEntity, Integer> {

    @Query("select at from AccountingTransactionVersionFileEntity at WHERE at.orderID = :orderID AND at.status = :status")
    @Transactional(readOnly = true)
    Optional<AccountingTransactionVersionFileEntity> findByOrderID(@Param("orderID")int orderID, @Param("status")AccountingTransactionStatusEnum rejected);

    @Query("select at from AccountingTransactionVersionFileEntity at WHERE at.orderID = :orderID")
    @Transactional(readOnly = true)
    List<AccountingTransactionVersionFileEntity> findAllByOrderID(@Param("orderID")int orderID);
}
