package com.sromip.payment.client;

import com.sromip.payment.dto.FraudCheckRequest;
import com.sromip.payment.dto.FraudCheckResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class FraudServiceClient {

    private final RestTemplate restTemplate;

    public FraudCheckResponse check(FraudCheckRequest req) {
        return restTemplate.postForObject(
                "http://FRAUD-SERVICE/fraud/check",
                req,
                FraudCheckResponse.class
        );
    }
}
