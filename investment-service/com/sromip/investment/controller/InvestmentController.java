package com.sromip.investment.controller;

import com.sromip.investment.dto.AddInvestmentRequest;
import com.sromip.investment.entity.Investment;
import com.sromip.investment.service.InvestmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/investment")
@RequiredArgsConstructor
public class InvestmentController {

    private final InvestmentService service;

    @PostMapping("/add")
    public String addInvestment(@RequestBody AddInvestmentRequest req) {
        return service.addInvestment(req);
    }

    @GetMapping("/total/{email}")
    public double total(@PathVariable String email) {
        return service.getTotalInvestment(email);
    }

    @GetMapping("/history/{email}")
    public List<Investment> history(@PathVariable String email) {
        return service.getHistory(email);
    }
}


