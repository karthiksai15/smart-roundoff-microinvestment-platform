package com.sromip.fraud.service;

import com.sromip.fraud.dto.FraudCheckRequest;
import com.sromip.fraud.dto.FraudCheckResponse;
import org.springframework.stereotype.Service;

@Service
public class FraudService {

    public FraudCheckResponse check(FraudCheckRequest req) {

        FraudCheckResponse res = new FraudCheckResponse();

        if (req.getAmount() > 50000) {
            res.setAllow(false);
            res.setReason("High amount risk");
            return res;
        }

        if (req.getDeviceId() == null || req.getIpAddress() == null) {
            res.setAllow(false);
            res.setReason("Missing device/IP");
            return res;
        }

        res.setAllow(true);
        res.setReason("OK");
        return res;
    }
}
