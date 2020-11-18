package com.bns.platotrain.controllers;

import com.bns.platotrain.dto.TransactionLogDto;
import com.bns.platotrain.service.TransactionLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class TransactionLogController {

    private TransactionLogService transactionLogService;

    public TransactionLogController(TransactionLogService transactionLogService) {
        this.transactionLogService = transactionLogService;
    }

    @GetMapping("/logs/{id}")
    @PreAuthorize("hasAnyRole(@roles.EMPLOYEE)")
    public TransactionLogDto getLogById(@PathVariable("id") Long id) throws Exception {
        return transactionLogService.getLogById(id);
    }

    @GetMapping("/logs/getAll")
    @PreAuthorize("hasAnyRole(@roles.ADMIN)")
    public List<TransactionLogDto> getAllLogs() {
        return transactionLogService.getAllLogs();
    }

    @PostMapping("/logs/add")
    public Long addLog(@RequestBody TransactionLogDto logDto) {
        return transactionLogService.addLog(logDto);
    }

    @GetMapping("/actuator/health")
    public Map healthCheck() {
        Map map = new HashMap<>();
        map.put("status","up");
        return map;
    }

}
