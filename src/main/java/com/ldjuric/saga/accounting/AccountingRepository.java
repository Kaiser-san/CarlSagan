package com.ldjuric.saga.accounting;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountingRepository extends CrudRepository<AccountingEntity, Integer> {
    @Query("select a from AccountingEntity a WHERE a.username = :username")
    Optional<AccountingEntity> findByUsername(@Param("username") String username);
}