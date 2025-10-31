package com.example.demo.config;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Spring AI Chat Memory
 * Cấu hình bộ nhớ cho chatbot sử dụng Spring AI
 */
@Configuration
public class ChatMemoryConfig {

    /**
     * Tạo InMemoryChatMemory bean
     * Spring AI sẽ tự động quản lý conversation history
     */
    @Bean
    public ChatMemory chatMemory() {
        System.out.println("🧠 Initializing InMemoryChatMemory...");
        return new InMemoryChatMemory();
    }
}
