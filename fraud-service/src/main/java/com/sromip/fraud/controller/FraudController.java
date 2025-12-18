package com.sromip.fraud.controller;

import com.sromip.fraud.dto.FraudCheckRequest;
import com.sromip.fraud.dto.FraudCheckResponse;
import com.sromip.fraud.service.FraudService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fraud")
@RequiredArgsConstructor
public class FraudController {

    private final FraudService service;

    @PostMapping("/check")
    public FraudCheckResponse check(@RequestBody FraudCheckRequest req) {
        return service.check(req);
    }
}
