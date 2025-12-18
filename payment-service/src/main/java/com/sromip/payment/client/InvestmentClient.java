package com.sromip.payment.client;

import com.sromip.payment.dto.AddInvestmentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
@RequiredArgsConstructor
public class InvestmentClient {

    private final RestTemplate restTemplate;

    public void addInvestment(String email, double spareAmount, String jwt) {

        AddInvestmentRequest req = new AddInvestmentRequest();
        req.setUserEmail(email);
        req.setSpareAmount(spareAmount);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwt);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AddInvestmentRequest> entity =
                new HttpEntity<>(req, headers);

        restTemplate.postForEntity(
                "http://localhost:8083/investment/add",
                entity,
                String.class
        );
    }
}
