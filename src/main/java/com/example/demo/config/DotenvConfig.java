package com.example.demo.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DotenvConfig {

    @Bean
    public Dotenv dotenv() {
        return Dotenv.configure()
                .directory("./") // Tìm file .env trong thư mục gốc của project
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();
    }
}
