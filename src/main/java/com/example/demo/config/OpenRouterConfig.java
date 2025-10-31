package com.example.demo.config;

import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenRouter Configuration
 * Cấu hình để tương thích với OpenRouter API với các headers đặc biệt
 * OpenRouter yêu cầu HTTP-Referer và X-Title headers
 */
@Configuration
public class OpenRouterConfig {

    /**
     * Thêm custom headers cho OpenRouter API
     * OpenRouter yêu cầu headers này để tracking và analytics
     */
    @Bean
    public RestClientCustomizer openRouterRestClientCustomizer() {
        return restClientBuilder -> restClientBuilder
                .defaultHeader("HTTP-Referer", "http://localhost:8080")
                .defaultHeader("X-Title", "Spring AI Chat Demo");
    }
}
