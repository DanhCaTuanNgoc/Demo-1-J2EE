package com.example.demo.service;

import com.example.demo.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {

    @Autowired(required = false)
    private GeminiRestService geminiRestService;

    @Autowired
    private TimeService timeService;

    private List<Message> conversationHistory = new ArrayList<>();

    public String chat(String userMessage) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("💬 NHẬN TIN NHẮN: " + userMessage);
        System.out.println("=".repeat(50));

        try {
            // Add user message to conversation history first
            conversationHistory.add(new Message(userMessage, Message.MessageType.USER));
            System.out.println("📝 Đã thêm tin nhắn vào lịch sử cuộc trò chuyện");

            // Check for function calling requests
            String functionResponse = checkAndExecuteFunction(userMessage);
            if (functionResponse != null) {
                conversationHistory.add(new Message(functionResponse, Message.MessageType.ASSISTANT));
                System.out.println("✅ Function executed: " + functionResponse);
                return functionResponse;
            }

            // Build context from conversation history for memory
            String contextPrompt = buildContextPrompt(userMessage);
            System.out.println("🧠 Sử dụng context từ " + (conversationHistory.size() - 1) + " tin nhắn trước");

            String responseText;
            // Use Gemini REST API with context
            if (geminiRestService != null && geminiRestService.isEnabled()) {
                System.out.println("🤖 Đang gọi Gemini REST API với context...");
                responseText = geminiRestService.generateText(contextPrompt);
                if (responseText == null || responseText.isBlank()) {
                    throw new RuntimeException("Empty response from Gemini REST");
                }
                // Track conversation
                conversationHistory.add(new Message(responseText, Message.MessageType.ASSISTANT));
                System.out.println("✅ Nhận phản hồi từ Gemini REST API");
            } else {
                throw new RuntimeException("Gemini REST service is not available. Please check your API key configuration.");
            }
            System.out.println("📤 Phản hồi: " + responseText);
            System.out.println("=".repeat(50) + "\n");

            return responseText;

        } catch (Exception e) {
            System.err.println("\n" + "=".repeat(50));
            System.err.println("❌ LỖI KHI XỬ LÝ TIN NHẮN");
            System.err.println("=".repeat(50));
            System.err.println("🐛 Chi tiết lỗi: " + e.getMessage());
            System.err.println("📍 Loại lỗi: " + e.getClass().getSimpleName());
            
            // In stack trace để debug
            e.printStackTrace();
            
            // Kiểm tra loại lỗi cụ thể
            if (e.getMessage().contains("credentials")) {
                System.err.println("🔑 LỖI CREDENTIALS: Kiểm tra file vertex-credentials.json");
                System.err.println("   - File có tồn tại không?");
                System.err.println("   - File có đúng format JSON không?");
                System.err.println("   - Project ID có đúng không?");
            } else if (e.getMessage().contains("permission") || e.getMessage().contains("access")) {
                System.err.println("🚫 LỖI QUYỀN TRUY CẬP: Kiểm tra quyền của Service Account");
                System.err.println("   - Service Account có quyền Vertex AI User không?");
                System.err.println("   - API có được kích hoạt không?");
            } else if (e.getMessage().contains("network") || e.getMessage().contains("connection")) {
                System.err.println("🌐 LỖI MẠNG: Kiểm tra kết nối internet");
                System.err.println("   - Có thể truy cập Google Cloud không?");
                System.err.println("   - Firewall có chặn không?");
            }
            
            System.err.println("=".repeat(50) + "\n");
            
            // Trả về thông báo lỗi thân thiện
            return "Xin lỗi, tôi gặp lỗi khi xử lý tin nhắn của bạn. Vui lòng kiểm tra console để xem chi tiết lỗi.";
        }
    }

    public void clearMemory() {
        System.out.println("🗑️  Xóa toàn bộ lịch sử cuộc trò chuyện");
        conversationHistory.clear();
        System.out.println("✅ Đã xóa " + conversationHistory.size() + " tin nhắn");
    }

    public List<Message> getConversationHistory() {
        System.out.println("📋 Lấy lịch sử cuộc trò chuyện: " + conversationHistory.size() + " tin nhắn");
        return new ArrayList<>(conversationHistory);
    }

    /**
     * Build context prompt with conversation history for memory
     */
    private String buildContextPrompt(String currentMessage) {
        StringBuilder context = new StringBuilder();
        
        // Add system instruction for memory awareness
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
        
        // Add current message
        context.append("Người dùng: ").append(currentMessage).append("\n");
        context.append("Chatbot: ");
        
        return context.toString();
    }

    /**
     * Check for function calling requests and execute them
     */
    private String checkAndExecuteFunction(String userMessage) {
        String lowerMessage = userMessage.toLowerCase();
        
        // Function 1: Time service
        if (lowerMessage.contains("thời gian") || 
            lowerMessage.contains("mấy giờ") ||
            lowerMessage.contains("time")) {
            
            System.out.println("⏰ Function Calling: getCurrentTime()");
            String currentTime = timeService.getCurrentTime();
            return "Bây giờ là: " + currentTime;
        }
        
        // Function 2: Calculator
        if (lowerMessage.contains("tính") || lowerMessage.contains("cộng") || 
            lowerMessage.contains("trừ") || lowerMessage.contains("nhân") || 
            lowerMessage.contains("chia") || lowerMessage.contains("+") || 
            lowerMessage.contains("-") || lowerMessage.contains("*") || 
            lowerMessage.contains("/")) {
            
            System.out.println("🧮 Function Calling: calculate()");
            return executeCalculator(userMessage);
        }
        
        // Function 3: Memory query (only for specific memory queries)
        if ((lowerMessage.contains("nhắc") || lowerMessage.contains("nhớ")) && 
            (lowerMessage.contains("tên") || lowerMessage.contains("biết"))) {
            
            System.out.println("🧠 Function Calling: queryMemory()");
            return queryMemory(userMessage);
        }
        
        return null; // No function to execute
    }

    /**
     * Execute calculator function
     */
    private String executeCalculator(String userMessage) {
        try {
            // Extract numbers and operation from message
            double result = 0;
            String operation = "";
            String expression = userMessage.replaceAll("[^0-9+\\-*/.]", " ").trim();
            
            // Simple calculation parsing
            if (expression.contains("+")) {
                String[] parts = expression.split("\\+");
                if (parts.length == 2) {
                    double a = Double.parseDouble(parts[0].trim());
                    double b = Double.parseDouble(parts[1].trim());
                    result = a + b;
                    operation = "cộng";
                }
            } else if (expression.contains("-") && !expression.startsWith("-")) {
                String[] parts = expression.split("-");
                if (parts.length == 2) {
                    double a = Double.parseDouble(parts[0].trim());
                    double b = Double.parseDouble(parts[1].trim());
                    result = a - b;
                    operation = "trừ";
                }
            } else if (expression.contains("*")) {
                String[] parts = expression.split("\\*");
                if (parts.length == 2) {
                    double a = Double.parseDouble(parts[0].trim());
                    double b = Double.parseDouble(parts[1].trim());
                    result = a * b;
                    operation = "nhân";
                }
            } else if (expression.contains("/")) {
                String[] parts = expression.split("/");
                if (parts.length == 2) {
                    double a = Double.parseDouble(parts[0].trim());
                    double b = Double.parseDouble(parts[1].trim());
                    if (b != 0) {
                        result = a / b;
                        operation = "chia";
                    } else {
                        return "Lỗi: Không thể chia cho 0!";
                    }
                }
            }
            
            if (!operation.isEmpty()) {
                return "Kết quả phép " + operation + " là: " + result;
            }
            
        } catch (Exception e) {
            System.err.println("Lỗi tính toán: " + e.getMessage());
        }
        
        return "Tôi không thể hiểu phép tính này. Vui lòng viết rõ hơn, ví dụ: 'tính 2 + 3'";
    }

    /**
     * Query memory for specific information
     */
    private String queryMemory(String userMessage) {
        // Look for name in conversation history
        for (Message msg : conversationHistory) {
            if (msg.getType() == Message.MessageType.USER) {
                String content = msg.getContent().toLowerCase();
                if (content.contains("tên") && (content.contains("tôi") || content.contains("mình"))) {
                    // Extract name from message like "tôi tên tuấn" or "mình tên tuấn"
                    String[] words = msg.getContent().split("\\s+");
                    for (int i = 0; i < words.length - 1; i++) {
                        if (words[i].toLowerCase().equals("tên")) {
                            String name = words[i + 1];
                            return "Bạn tên là " + name + ".";
                        }
                    }
                }
            }
        }
        
        return "Tôi không tìm thấy thông tin này trong cuộc trò chuyện trước đó.";
    }

}
