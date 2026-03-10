package com.sromip.fraud.service;

import com.sromip.fraud.dto.MLRequest;
import com.sromip.fraud.dto.MLResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class FraudMLClient {

    private final RestTemplate restTemplate;

    @Value("${ml.base-url}")
    private String mlBaseUrl;

    public MLResponse getRiskScore(MLRequest request) {

        String url = mlBaseUrl + "/predict";

        log.info("📡 Calling ML service: {}", url);

        MLResponse response = restTemplate.postForObject(
                url,
                request,
                MLResponse.class
        );

        log.info("🤖 ML response received: {}", response);

        return response;
    }
}