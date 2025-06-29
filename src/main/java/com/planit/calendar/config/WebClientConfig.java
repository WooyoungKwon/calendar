package com.planit.calendar.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${api.base-url}")
    private String BASE_URL;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
            .baseUrl(BASE_URL)
            .defaultHeader("Accept", "application/json")
            .build();
    }
}
