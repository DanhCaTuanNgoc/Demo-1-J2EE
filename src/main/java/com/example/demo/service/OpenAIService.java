package com.example.demo.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Service
public class OpenAIService {
    
    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final boolean enabled;
    private final String systemPrompt;

    public OpenAIService(
            ChatModel chatModel,
            ChatMemory chatMemory,
            @Value("${spring.ai.openai.api-key:}") String apiKey,
            @Value("${spring.ai.openai.system-prompt:You are a helpful AI assistant.}") String systemPrompt) {
        
        this.chatMemory = chatMemory;
        this.systemPrompt = systemPrompt;
        this.enabled = apiKey != null && !apiKey.isEmpty() && !apiKey.equals("your_openrouter_api_key_here");
        
        if (enabled) {
            // Khởi tạo ChatClient với Memory Advisor và System Prompt
            this.chatClient = ChatClient.builder(chatModel)
                .defaultSystem(systemPrompt)  // ✅ Thêm system prompt
                .defaultAdvisors(
                    new MessageChatMemoryAdvisor(chatMemory)
                )
                .build();
            System.out.println("✅ OpenAIService initialized with Spring AI Memory and Function Calling");
            System.out.println("📋 System Prompt: " + systemPrompt);
        } else {
            this.chatClient = null;
            System.out.println("⚠️  OpenAIService disabled - no API key configured");
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Generate text với Memory và Function Calling
     * @param userMessage Tin nhắn từ user
     * @param conversationId ID của cuộc hội thoại (để phân biệt memory)
     * @param functionNames Danh sách functions mà AI có thể gọi
     */
    public String generateText(String userMessage, String conversationId, String... functionNames) {
        if (!enabled) {
            throw new IllegalStateException("OpenAI service is not enabled");
        }

        try {
            System.out.println("\n🤖 Processing message with Spring AI...");
            System.out.println("📝 User: " + userMessage);
            System.out.println("🔗 Conversation ID: " + conversationId);
            System.out.println("⚙️  Available functions: " + String.join(", ", functionNames));

            // ✅ SỬA: Dùng chatResponse() để lấy full response
            var response = chatClient.prompt()
                .user(userMessage)
                .advisors(a -> a
                    .param(CHAT_MEMORY_CONVERSATION_ID_KEY, conversationId)
                    .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100)
                )
                .functions(functionNames)  // Đăng ký functions
                .call()
                .chatResponse();  // ✅ Lấy ChatResponse đầy đủ

            // Lấy response cuối cùng từ AI (sau khi xử lý function calls)
            String aiResponse = response.getResult().getOutput().getContent();
            
            System.out.println("✅ AI Response: " + aiResponse);
            System.out.println("📊 Metadata: " + response.getMetadata());
            
            return aiResponse;

        } catch (Exception e) {
            System.err.println("❌ Error in OpenAIService: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to generate response: " + e.getMessage(), e);
        }
    }

    /**
     * Clear memory cho một conversation
     */
    public void clearMemory(String conversationId) {
        if (chatMemory != null) {
            chatMemory.clear(conversationId);
            System.out.println("🗑️  Cleared memory for conversation: " + conversationId);
        }
    }
}