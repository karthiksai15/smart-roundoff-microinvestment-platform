package com.sromip.investment.service;

import com.sromip.investment.dto.AddInvestmentRequest;
import com.sromip.investment.entity.Investment;
import com.sromip.investment.entity.Portfolio;
import com.sromip.investment.repository.InvestmentRepository;
import com.sromip.investment.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InvestmentService {

    private final InvestmentRepository investmentRepo;
    private final PortfolioRepository portfolioRepo;

    public String addInvestment(AddInvestmentRequest req) {

        // Save investment history entry
        Investment inv = new Investment();
        inv.setUserEmail(req.getUserEmail());
        inv.setAmount(req.getSpareAmount());
        investmentRepo.save(inv);

        // Update portfolio (total savings)
        Portfolio p = portfolioRepo.findByUserEmail(req.getUserEmail())
                .orElseGet(() -> {
                    Portfolio newP = new Portfolio();
                    newP.setUserEmail(req.getUserEmail());
                    newP.setTotalInvestment(0);
                    return newP;
                });

        p.setTotalInvestment(p.getTotalInvestment() + req.getSpareAmount());
        portfolioRepo.save(p);

        return "Investment added successfully!";
    }

    public double getTotalInvestment(String email) {
        return portfolioRepo.findByUserEmail(email)
                .map(Portfolio::getTotalInvestment)
                .orElse(0.0);
    }

    public List<Investment> getHistory(String email) {
        return investmentRepo.findByUserEmail(email);
    }
}


