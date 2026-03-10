package com.sromip.dashboard.controller;

import com.sromip.dashboard.dto.PipelineStatusResponse;
import com.sromip.dashboard.service.DashboardUpdateService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import java.util.List;
import com.sromip.dashboard.dto.PipelineView;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardUpdateService service;

    public DashboardController(DashboardUpdateService service) {
        this.service = service;
    }

    // ===============================
    // PIPELINE STATUS BY PAYMENT ID
    // ===============================
    @GetMapping("/transaction/{paymentId}")
    public PipelineStatusResponse getPipeline(@PathVariable Long paymentId) {
        return service.getPipelineStatus(paymentId);
    }

    // ===============================
    // TOTAL TRANSACTIONS
    // ===============================
    @GetMapping("/metrics/transactions")
    public long totalTransactions() {
        return service.getTotalTransactions();
    }

    // ===============================
    // FRAUD METRICS
    // ===============================
    @GetMapping("/metrics/fraud")
    public Map<String, Long> fraudMetrics() {

        Map<String, Long> res = new HashMap<>();

        res.put("approved", service.getApprovedTransactions());
        res.put("blocked", service.getBlockedTransactions());

        return res;
    }

    // ===============================
    // INVESTMENT TOTAL
    // ===============================
    @GetMapping("/metrics/investments")
    public Double totalInvested() {
        return service.getTotalInvested();
    }
    // ===============================
// ALL PIPELINES
// ===============================

    @GetMapping("/pipelines")
    public List<PipelineView> pipelines() {
        return service.getAllPipelines();
    }
}