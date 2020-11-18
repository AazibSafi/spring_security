package com.spring.security.service;

import com.spring.security.dto.TransactionLogDto;
import com.spring.security.entity.TransactionLog;
import com.spring.security.repo.TransactionLogRepo;
import com.spring.security.util.ConversionUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionLogService {

    private TransactionLogRepo transactionLogRepo;
    private ConversionUtil conversionUtil;

    public TransactionLogService(TransactionLogRepo transactionLogRepo, ConversionUtil conversionUtil) {
        this.transactionLogRepo = transactionLogRepo;
        this.conversionUtil = conversionUtil;
    }

    public TransactionLogDto getLogById(Long id) throws Exception {
        TransactionLog returnedLog = transactionLogRepo.findById(id)
                .orElseThrow(() ->
                    new Exception("Transaction Log not found: " + id)
                );
        return conversionUtil.mapSourceToDestinationType(returnedLog,TransactionLogDto.class);
    }

    public List<TransactionLogDto> getAllLogs() {
        List<TransactionLog> fetchedLogs = transactionLogRepo.findAll();
        return conversionUtil.mapSourceListToDestinationType(fetchedLogs,TransactionLogDto.class);
    }

    public Long addLog(TransactionLogDto logDto) {
        TransactionLog transactionLog = conversionUtil.mapSourceToDestinationType(logDto,TransactionLog.class);
        TransactionLog save = transactionLogRepo.save(transactionLog);
        return save.id();
    }

}
