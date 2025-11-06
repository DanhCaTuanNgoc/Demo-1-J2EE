package com.example.demo.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

@Service
public class TimeService {

    /**
     * ✅ Spring AI Function Bean - AI có thể tự động gọi function này để tính toán
     * Khi app chạy, Spring tạo instance và bỏ vào IoC Container.
     * Khi bạn đăng ký Function Calling, Spring AI sẽ lấy chính các bean này để thực thi function.
     */
    @Bean
    @Description("Get current date and time in Vietnam timezone")
    public Function<Request, Response> getCurrentTime() {
        return request -> {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss EEEE", java.util.Locale.forLanguageTag("vi"));
            String currentTime = now.format(formatter);
            
            System.out.println("⏰ Function called: getCurrentTime()");
            System.out.println("   Result: " + currentTime);
            
            return new Response(currentTime);
        };
    }

    public record Request() {}
    
    public record Response(String currentTime) {}
}
