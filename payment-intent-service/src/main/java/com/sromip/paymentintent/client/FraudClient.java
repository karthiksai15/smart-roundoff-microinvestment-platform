package com.sromip.paymentintent.client;

import com.sromip.common.event.FraudCheckRequest;
import com.sromip.common.event.FraudCheckResponse;
import com.sromip.paymentintent.event.FraudRetryEventPublisher;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class FraudClient {

    private final RestTemplate restTemplate;
    private final FraudRetryEventPublisher retryPublisher;

    @CircuitBreaker(name = "fraudServiceCB", fallbackMethod = "fallbackFraud")
    @Retry(name = "fraudServiceRetry")
    @Bulkhead(name = "fraudServiceBulkhead")
   // @TimeLimiter(name = "fraudServiceTimeout") // ✅ FIX ADDED
    public FraudCheckResponse checkFraud(FraudCheckRequest request) {

        log.info("📡 Calling Fraud Service...");

        FraudCheckResponse response = restTemplate.postForObject(
                "http://FRAUD-SERVICE/api/fraud/check",
                request,
                FraudCheckResponse.class
        );

        if (response == null) {
            throw new RuntimeException("Fraud response is null");
        }

        log.info("✅ Fraud response received: {}", response.getReason());

        return response;
    }

    public FraudCheckResponse fallbackFraud(
            FraudCheckRequest request,
            Throwable ex
    ) {

        log.error("🚨 Fraud service failed → HYBRID FALLBACK", ex);

        try {
            retryPublisher.publish(request);
        } catch (Exception e) {
            log.error("❌ Failed to publish retry event", e);
        }

        FraudCheckResponse fallback = new FraudCheckResponse();

        if (request.getAmount() > 100000) {
            fallback.setAllow(false);
            fallback.setRiskScore(0.95);
            fallback.setReason("FAIL_SAFE_BLOCK_HIGH_AMOUNT");
        } else {
            fallback.setAllow(true);
            fallback.setRiskScore(0.5);
            fallback.setReason("FAIL_SAFE_OTP_REQUIRED");
        }

        return fallback;
    }
}