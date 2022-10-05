package com.ldjuric.saga.accounting;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountingTransactionRepository extends CrudRepository<AccountingTransactionEntity, Integer> {
}
