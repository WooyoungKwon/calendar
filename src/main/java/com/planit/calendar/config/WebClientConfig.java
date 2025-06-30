package com.planit.calendar.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${api.base-url}")
    private String BASE_URL;

    @Bean
    public WebClient webClient(ObjectMapper objectMapper) {
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
            .codecs(configurer -> {
                configurer.defaultCodecs().jackson2JsonDecoder(
                    new Jackson2JsonDecoder(
                        objectMapper,
                        MediaType.APPLICATION_JSON,
                        MediaType.valueOf("text/json") // 일부 데이터에서 text/json 타입을 사용하기 때문에 추가
                    )
                );
            })
            .build();

        return WebClient.builder()
            .baseUrl(BASE_URL)
            .defaultHeader("Accept", "application/json")
            .exchangeStrategies(exchangeStrategies)
            .build();
    }
}
