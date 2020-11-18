package com.bns.platotrain.service;

import com.bns.platotrain.dto.TransactionLogDto;
import com.bns.platotrain.entity.TransactionLog;
import com.bns.platotrain.repo.TransactionLogRepo;
import com.bns.platotrain.util.ConversionUtil;
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
