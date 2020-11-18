package com.bns.platotrain.repo;

import com.bns.platotrain.entity.TransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionLogRepo extends JpaRepository<TransactionLog, Long> {

}
