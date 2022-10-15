package com.ldjuric.saga.accounting;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Profile({"accounting", "all"})
@Repository
@Transactional
public interface AccountingRepository extends CrudRepository<AccountingEntity, Integer> {
    @Query("select a from AccountingEntity a WHERE a.username = :username")
    Optional<AccountingEntity> findByUsername(@Param("username") String username);
}