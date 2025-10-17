package com.example.demo.controller;

import com.example.demo.model.Message;
import com.example.demo.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/message")
    public ResponseEntity<Map<String, String>> sendMessage(@RequestBody Map<String, String> request) {
        System.out.println("\n🌐 NHẬN API REQUEST: POST /api/chat/message");
        
        String message = request.get("message");
        System.out.println("📥 Tin nhắn từ client: " + message);
        
        if (message == null || message.trim().isEmpty()) {
            System.err.println("❌ Lỗi: Tin nhắn rỗng");
            return ResponseEntity.badRequest().body(Map.of("error", "Message cannot be empty"));
        }

        try {
            String response = chatService.chat(message);
            System.out.println("✅ Gửi phản hồi về client: " + response);
            return ResponseEntity.ok(Map.of("response", response));
        } catch (Exception e) {
            System.err.println("❌ Lỗi API: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Error processing message: " + e.getMessage()));
        }
    }

    @DeleteMapping("/memory")
    public ResponseEntity<Map<String, String>> clearMemory() {
        System.out.println("\n🌐 NHẬN API REQUEST: DELETE /api/chat/memory");
        System.out.println("🗑️  Xóa memory theo yêu cầu API");
        
        chatService.clearMemory();
        
        System.out.println("✅ Memory đã được xóa");
        return ResponseEntity.ok(Map.of("message", "Conversation memory cleared"));
    }

    @GetMapping("/history")
    public ResponseEntity<Map<String, Object>> getHistory() {
        System.out.println("\n🌐 NHẬN API REQUEST: GET /api/chat/history");
        
        List<Message> history = chatService.getConversationHistory();
        
        System.out.println("📋 Trả về " + history.size() + " tin nhắn trong lịch sử");
        return ResponseEntity.ok(Map.of("history", history));
    }

}
