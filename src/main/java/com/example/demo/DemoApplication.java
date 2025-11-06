package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class DemoApplication {

	private static final Logger logger = LoggerFactory.getLogger(DemoApplication.class);

	@Autowired
	private Environment environment;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void onApplicationReady() {
		String appName = environment.getProperty("spring.application.name", "demo");
		String serverPort = environment.getProperty("server.port", "8080");
		String openRouterKey = environment.getProperty("spring.ai.openai.api-key", "");
		boolean hasApiKey = openRouterKey != null && !openRouterKey.isEmpty() 
							&& !openRouterKey.equals("your_openrouter_api_key_here");
		
		logger.info("\n" + "=".repeat(70));
		logger.info("� Application Started Successfully");
		logger.info("=".repeat(70));
		logger.info("� Application: {}", appName);
		logger.info("🌐 Server Port: {}", serverPort);
		logger.info("🔑 OpenRouter API Key: {}", hasApiKey ? "✅ Configured" : "❌ Not configured");
		logger.info("🧠 Spring AI Memory: ✅ Enabled");
		logger.info("⚙️  Function Calling: ✅ Enabled (getCurrentTime, calculator)");
		
		if (hasApiKey) {
			logger.info("\n✅ Application is ready!");
			logger.info("🌐 Access: http://localhost:{}", serverPort);
			logger.info("📡 API Endpoint: http://localhost:{}/api/chat/message", serverPort);
		} else {
			logger.warn("\n⚠️  OpenRouter API Key not configured!");
			logger.info("💡 Please set OPENROUTER_API_KEY in .env file");
			logger.info("   Example: OPENROUTER_API_KEY=sk-or-v1-xxx");
		}
		
		logger.info("=".repeat(70) + "\n");
	}
}
