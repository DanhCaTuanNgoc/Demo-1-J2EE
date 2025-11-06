package com.example.demo.service;

import com.example.demo.model.Message;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {

    @Autowired(required = false)
    private OpenAIService openAIService;
    
    @Autowired
    private ChatMemory chatMemory;

    private static final String DEFAULT_CONVERSATION_ID = "default-session";

    /**
     * ✅ Chat thuần Spring AI - tự động Memory + Function Calling
     */
    public String chat(String userMessage) {
        if (openAIService == null || !openAIService.isEnabled()) {
            throw new RuntimeException("OpenAI service is not configured. Please set OPENROUTER_API_KEY in .env file");
        }

        try {
            System.out.println("\n" + "=".repeat(80));
            System.out.println("💬 NEW MESSAGE");
            System.out.println("=".repeat(80));
            
            // ✅ Spring AI tự động xử lý:
            // 1. Lưu user message vào ChatMemory
            // 2. Retrieve context từ memory (10 messages gần nhất)
            // 3. Gửi request đến OpenRouter với functions registered
            // 4. AI quyết định có cần gọi function không
            // 5. Nếu cần → Spring AI tự động execute function → gọi API lần 2
            // 6. Trả về response cuối cùng
            // 7. Lưu assistant response vào ChatMemory
            
            String responseText = openAIService.generateText(
                userMessage, 
                DEFAULT_CONVERSATION_ID,
                "getCurrentTime",  // ⚙️ Functions AI có thể sử dụng
                "calculator"
            );

            System.out.println("=".repeat(80));
            System.out.println();
            
            return responseText;

        } catch (Exception e) {
            System.err.println("❌ Error in chat: " + e.getMessage());
            e.printStackTrace();
            return "Xin lỗi, tôi gặp lỗi khi xử lý tin nhắn: " + e.getMessage();
        }
    }

    /**
     * ✅ Clear memory - sử dụng Spring AI ChatMemory
     */
    public void clearMemory() {
        System.out.println("🗑️  Clearing conversation memory...");
        
        if (openAIService != null) {
            openAIService.clearMemory(DEFAULT_CONVERSATION_ID);
        }
        
        System.out.println("✅ Memory cleared successfully");
    }

    /**
     * ✅ Get history từ Spring AI ChatMemory
     */
    public List<Message> getConversationHistory() {
        try {
            var messages = chatMemory.get(DEFAULT_CONVERSATION_ID, 100);
            
            List<Message> history = new ArrayList<>();
            messages.forEach(msg -> {
                // Convert Spring AI Message to our Message model
                String messageType = msg.getMessageType().getValue();
                Message.MessageType type;
                
                switch (messageType.toLowerCase()) {
                    case "user" -> type = Message.MessageType.USER;
                    case "assistant" -> type = Message.MessageType.ASSISTANT;
                    default -> type = Message.MessageType.ASSISTANT;
                }
                
                history.add(new Message(msg.getContent(), type));
            });
            
            System.out.println("📋 Retrieved " + history.size() + " messages from memory");
            return history;
            
        } catch (Exception e) {
            System.err.println("❌ Error getting history: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
