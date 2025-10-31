package com.example.demo.service;

import com.example.demo.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {

    @Autowired(required = false)
    private OpenAIService openAIService;
    
    @Autowired(required = false)
    private GeminiRestService geminiRestService;

    private List<Message> conversationHistory = new ArrayList<>();
    private static final String DEFAULT_CONVERSATION_ID = "default-session";

    /**
     * Chat với Spring AI - tự động xử lý Memory và Function Calling
     */
    public String chat(String userMessage) {
        try {
            // Add user message to local history (for backward compatibility)
            conversationHistory.add(new Message(userMessage, Message.MessageType.USER));

            String responseText;
            
            // Sử dụng OpenRouter với Spring AI (có Memory và Function Calling)
            if (openAIService != null && openAIService.isEnabled()) {
                // Spring AI tự động:
                // 1. Lưu message vào memory
                // 2. Quyết định có cần gọi function không
                // 3. Gọi function nếu cần
                // 4. Trả về response cuối cùng
                responseText = openAIService.generateText(
                    userMessage, 
                    DEFAULT_CONVERSATION_ID,
                    "getCurrentTime",  // Đăng ký function có thể gọi
                    "calculator"       // Đăng ký function có thể gọi
                );
                
                if (responseText == null || responseText.isBlank()) {
                    throw new RuntimeException("Empty response from OpenRouter");
                }
                conversationHistory.add(new Message(responseText, Message.MessageType.ASSISTANT));
                
            } else if (geminiRestService != null && geminiRestService.isEnabled()) {
                // Fallback: Gemini không hỗ trợ Spring AI, dùng cách cũ
                String contextPrompt = buildContextPrompt(userMessage);
                responseText = geminiRestService.generateText(contextPrompt);
                
                if (responseText == null || responseText.isBlank()) {
                    throw new RuntimeException("Empty response from Gemini");
                }
                conversationHistory.add(new Message(responseText, Message.MessageType.ASSISTANT));
                
            } else {
                throw new RuntimeException("No AI service available. Please configure OpenRouter or Gemini API key.");
            }

            return responseText;

        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
            
            // Trả về thông báo lỗi thân thiện
            return "Xin lỗi, tôi gặp lỗi khi xử lý tin nhắn của bạn: " + e.getMessage();
        }
    }

    /**
     * Clear memory - sử dụng Spring AI memory nếu có
     */
    public void clearMemory() {
        System.out.println("🗑️  Xóa toàn bộ lịch sử cuộc trò chuyện");
        
        // Clear Spring AI memory
        if (openAIService != null && openAIService.isEnabled()) {
            openAIService.clearMemory(DEFAULT_CONVERSATION_ID);
        }
        
        // Clear local history
        conversationHistory.clear();
        System.out.println("✅ Đã xóa lịch sử cuộc trò chuyện");
    }

    public List<Message> getConversationHistory() {
        System.out.println("📋 Lấy lịch sử cuộc trò chuyện: " + conversationHistory.size() + " tin nhắn");
        return new ArrayList<>(conversationHistory);
    }

    /**
     * Build context prompt with conversation history for OpenRouter (fallback only)
     */
    private String buildContextPrompt(String currentMessage) {
        StringBuilder context = new StringBuilder();
        
        context.append("Bạn là một chatbot thông minh có khả năng nhớ thông tin trong cuộc trò chuyện. ");
        context.append("Hãy sử dụng thông tin từ lịch sử cuộc trò chuyện để trả lời chính xác và có ngữ cảnh.\n\n");
        
        // Add conversation history (last 10 messages to avoid token limit)
        int startIndex = Math.max(0, conversationHistory.size() - 10);
        for (int i = startIndex; i < conversationHistory.size() - 1; i++) {
            Message msg = conversationHistory.get(i);
            if (msg.getType() == Message.MessageType.USER) {
                context.append("Người dùng: ").append(msg.getContent()).append("\n");
            } else {
                context.append("Chatbot: ").append(msg.getContent()).append("\n");
            }
        }
        
        context.append("Người dùng: ").append(currentMessage).append("\n");
        context.append("Chatbot: ");
        
        return context.toString();
    }
}
