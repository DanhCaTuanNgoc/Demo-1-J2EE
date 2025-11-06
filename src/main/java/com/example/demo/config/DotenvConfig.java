package com.example.demo.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)  // ✅ Chạy ĐẦU TIÊN
public class DotenvConfig {

    // ✅ Dùng static block để chạy TRƯỚC tất cả beans
    static {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .directory("./")
                    .ignoreIfMalformed()
                    .ignoreIfMissing()
                    .load();
            
            // Set all .env variables to System properties
            dotenv.entries().forEach(entry -> {
                System.setProperty(entry.getKey(), entry.getValue());
            });
            
            System.out.println("✅ [STATIC INIT] Loaded .env file successfully");
            String apiKey = System.getProperty("OPENROUTER_API_KEY");
            if (apiKey != null && !apiKey.isEmpty()) {
                System.out.println("   OPENROUTER_API_KEY: ✅ Set (" + apiKey.substring(0, 15) + "...)");
            } else {
                System.err.println("   OPENROUTER_API_KEY: ❌ NOT FOUND");
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to load .env: " + e.getMessage());
        }
    }
    
    // Verification sau khi Spring context khởi tạo
    @PostConstruct
    public void verifyConfig() {
        String apiKey = System.getProperty("OPENROUTER_API_KEY");
        System.out.println("✅ [POST CONSTRUCT] Verifying .env loaded: " + 
            (apiKey != null ? "YES" : "NO"));
    }
}
