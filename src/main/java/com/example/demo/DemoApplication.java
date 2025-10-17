package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.File;

@SpringBootApplication
public class DemoApplication {

	@Autowired
	private Environment environment;

	@Autowired
	private ResourceLoader resourceLoader;

	public static void main(String[] args) {
		System.out.println("🚀 Đang khởi động ứng dụng Chatbot với Gemini AI...");
		SpringApplication.run(DemoApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void checkConfiguration() {
		System.out.println("\n" + "=".repeat(60));
		System.out.println("🔍 KIỂM TRA CẤU HÌNH ỨNG DỤNG - REST ONLY MODE");
		System.out.println("=".repeat(60));

		try {
			// Kiểm tra Gemini REST API configuration
			String apiKey = environment.getProperty("gemini.api.key", "");
			String apiUrl = environment.getProperty("gemini.api.url", "");
			
			System.out.println("🟢 Chế độ REST-only (không sử dụng Vertex AI)");
			System.out.println("🔑 GEMINI API KEY set: " + (!apiKey.isEmpty()));
			System.out.println("🌐 GEMINI REST URL: " + (apiUrl.isEmpty() ? "<not set>" : apiUrl));
			
			if (!apiKey.isEmpty()) {
				System.out.println("✅ CẤU HÌNH HOÀN CHỈNH - Ứng dụng sẵn sàng chạy!");
				System.out.println("🌐 Truy cập: http://localhost:8080");
				System.out.println("📡 API Endpoint: http://localhost:8080/api/chat/message");
				System.out.println("💰 Chi phí: Chỉ tính phí theo Gemini REST API (rẻ hơn Vertex AI)");
			} else {
				System.out.println("❌ CẤU HÌNH CHƯA HOÀN CHỈNH - Vui lòng cấu hình gemini.api.key!");
			}
			
			System.out.println("=".repeat(60) + "\n");

		} catch (Exception e) {
			System.out.println("❌ LỖI KHI KIỂM TRA CẤU HÌNH: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
