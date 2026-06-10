package com.sromip.paymentintent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.sromip.paymentintent")
@EnableJpaRepositories("com.sromip.paymentintent")
public class PaymentIntentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentIntentServiceApplication.class, args);
    }
}