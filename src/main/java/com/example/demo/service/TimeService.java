package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class TimeService {

    public String getCurrentTime() {
        System.out.println("⏰ TimeService: Lấy thời gian hiện tại");
        
        try {
            LocalDateTime now = LocalDateTime.now();
            String formattedTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            System.out.println("✅ TimeService: Thời gian hiện tại = " + formattedTime);
            return formattedTime;
            
        } catch (Exception e) {
            System.err.println("❌ TimeService: Lỗi khi lấy thời gian - " + e.getMessage());
            e.printStackTrace();
            return "Không thể lấy thời gian hiện tại";
        }
    }

}
