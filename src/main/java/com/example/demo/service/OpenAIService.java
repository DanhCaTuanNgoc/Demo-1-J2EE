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

    public OpenAIService(
            ChatModel chatModel,
            ChatMemory chatMemory,
            @Value("${spring.ai.openai.api-key:}") String apiKey,
            @Value("${spring.ai.openai.system-prompt:You are a helpful AI assistant.}") String systemPrompt) {
        
        this.chatMemory = chatMemory;
        this.enabled = apiKey != null && !apiKey.isEmpty() && !apiKey.equals("your_openrouter_api_key_here");
        
        if (enabled) {
            this.chatClient = ChatClient.builder(chatModel)
                .defaultSystem(systemPrompt)
                .defaultAdvisors(
                    new MessageChatMemoryAdvisor(chatMemory)
                )
                .build();
            System.out.println("✅ OpenAIService initialized (Spring AI)");
            System.out.println("   � System Prompt: " + systemPrompt);
            System.out.println("   🧠 Memory: Enabled");
            System.out.println("   ⚙️  Function Calling: Enabled");
        } else {
            this.chatClient = null;
            System.out.println("⚠️  OpenAIService disabled - no API key configured");
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    /**
     * ✅ Generate text với Memory và Function Calling
     */
    public String generateText(String userMessage, String conversationId, String... functionNames) {
        if (!enabled) {
            throw new IllegalStateException("OpenAI service is not enabled");
        }

        try {
            System.out.println("📤 Sending to AI:");
            System.out.println("   User: " + userMessage);
            System.out.println("   Conversation ID: " + conversationId);
            System.out.println("   Functions: " + String.join(", ", functionNames));

            var response = chatClient.prompt()
                .user(userMessage)
                .advisors(a -> a
                    .param(CHAT_MEMORY_CONVERSATION_ID_KEY, conversationId)
                    .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10)  // ✅ Lấy 10 messages gần nhất
                )
                .functions(functionNames)
                .call()
                .chatResponse();

            String aiResponse = response.getResult().getOutput().getContent();
            
            System.out.println("📥 Received from AI:");
            System.out.println("   Assistant: " + aiResponse);
            
            // Log metadata (tokens, model, etc.)
            var metadata = response.getMetadata();
            if (metadata != null && metadata.getUsage() != null) {
                System.out.println("   📊 Usage: " + metadata.getUsage());
            }
            
            return aiResponse;

        } catch (Exception e) {
            System.err.println("❌ Error in OpenAIService: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to generate response: " + e.getMessage(), e);
        }
    }

    /**
     * ✅ Clear memory cho một conversation
     */
    public void clearMemory(String conversationId) {
        if (chatMemory != null) {
            chatMemory.clear(conversationId);
            System.out.println("🗑️  Cleared memory for conversation: " + conversationId);
        }
    }
}