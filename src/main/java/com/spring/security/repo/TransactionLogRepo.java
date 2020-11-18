package com.spring.security.repo;

import com.spring.security.entity.TransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionLogRepo extends JpaRepository<TransactionLog, Long> {

}
