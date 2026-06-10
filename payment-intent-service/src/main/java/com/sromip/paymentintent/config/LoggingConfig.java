package com.sromip.paymentintent.config;

import com.sromip.paymentintent.filter.CorrelationIdFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingConfig {

    @Bean
    public FilterRegistrationBean<CorrelationIdFilter> correlationIdFilter() {

        FilterRegistrationBean<CorrelationIdFilter> registrationBean =
                new FilterRegistrationBean<>();

        registrationBean.setFilter(new CorrelationIdFilter());
        registrationBean.addUrlPatterns("/*");

        // ✅ FIX: ensure execution order
        registrationBean.setOrder(1);

        return registrationBean;
    }
}