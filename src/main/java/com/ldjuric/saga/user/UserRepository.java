package com.ldjuric.saga.user;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Profile({"user", "all"})
@Repository
public interface UserRepository extends CrudRepository<UserEntity, Integer> {
    @Query("select u from UserEntity u WHERE u.username = :username")
    UserEntity findByUsername(@Param("username") String username);
}