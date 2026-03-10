package com.sromip.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication app =
                new SpringApplication(NotificationServiceApplication.class);

        app.setDefaultProperties(
                java.util.Map.of("spring.application.name", "NOTIFICATION-SERVICE")
        );

        app.run(args);
    }
}
