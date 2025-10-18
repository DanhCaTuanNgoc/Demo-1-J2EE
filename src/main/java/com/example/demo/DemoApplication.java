package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class DemoApplication {

	private static final Logger logger = LoggerFactory.getLogger(DemoApplication.class);

	@Autowired
	private Environment environment;

	public static void main(String[] args) {
		logger.info("🚀 Đang khởi động ứng dụng Chatbot với Gemini AI...");
		
		// Load .env file trước khi khởi động Spring Boot
		try {
			Dotenv dotenv = Dotenv.configure()
					.directory("./") // Tìm file .env trong thư mục gốc
					.ignoreIfMalformed()
					.ignoreIfMissing()
					.load();
			
			// Set các biến môi trường từ .env vào System properties
			dotenv.entries().forEach(entry -> {
				System.setProperty(entry.getKey(), entry.getValue());
			});
			
			logger.info("✅ Đã load file .env thành công!");
		} catch (Exception e) {
			logger.warn("⚠️ Không thể load file .env: {}", e.getMessage());
			logger.info("💡 Sử dụng biến môi trường hệ thống hoặc giá trị mặc định");
		}
		
		SpringApplication.run(DemoApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void checkConfiguration() {
		logger.info("\n" + "=".repeat(60));
		logger.info("🔍 KIỂM TRA CẤU HÌNH ỨNG DỤNG - REST ONLY MODE");
		logger.info("=".repeat(60));

		try {
			// Kiểm tra Gemini REST API configuration
			String apiKey = environment.getProperty("gemini.api.key", "");
			String apiUrl = environment.getProperty("gemini.api.url", "");
			String appName = environment.getProperty("spring.application.name", "");
			String serverPort = environment.getProperty("server.port", "");
			String activeProfile = environment.getProperty("spring.profiles.active", "");
			
			logger.info("🟢 Chế độ REST-only");
			logger.info("📱 Tên ứng dụng: {}", appName);
			logger.info("🌐 Port: {}", serverPort);
			logger.info("🔧 Profile: {}", activeProfile);
			logger.info("🔑 GEMINI API KEY set: {}", !apiKey.isEmpty());
			logger.info("🌐 GEMINI REST URL: {}", apiUrl.isEmpty() ? "<not set>" : apiUrl);
			
			// Kiểm tra xem có load được từ .env không
			boolean fromEnvFile = System.getProperty("GEMINI_API_KEY") != null;
			logger.info("📄 Load từ file .env: {}", fromEnvFile ? "✅ Có" : "❌ Không");
			
			if (!apiKey.isEmpty()) {
				logger.info("✅ CẤU HÌNH HOÀN CHỈNH - Ứng dụng sẵn sàng chạy!");
				logger.info("🌐 Truy cập: http://localhost:{}", serverPort);
				logger.info("📡 API Endpoint: http://localhost:{}/api/chat/message", serverPort);
				logger.info("💰 Chi phí: Chỉ tính phí theo Gemini REST API (rẻ hơn Vertex AI)");
			} else {
				logger.error("❌ CẤU HÌNH CHƯA HOÀN CHỈNH - Vui lòng cấu hình GEMINI_API_KEY!");
				logger.info("💡 Tạo file .env trong thư mục gốc với nội dung:");
				logger.info("   GEMINI_API_KEY=your_actual_api_key_here");
			}
			
			logger.info("=".repeat(60) + "\n");

		} catch (Exception e) {
			logger.error("❌ LỖI KHI KIỂM TRA CẤU HÌNH: {}", e.getMessage());
			logger.error("Stack trace:", e);
		}
	}
}
