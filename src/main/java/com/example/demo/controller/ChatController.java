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
        String message = request.get("message");
        
        if (message == null || message.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Message cannot be empty"));
        }

        try {
            String response = chatService.chat(message);
            return ResponseEntity.ok(Map.of("response", response));
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Error processing message: " + e.getMessage()));
        }
    }

    @DeleteMapping("/memory")
    public ResponseEntity<Map<String, String>> clearMemory() {
        chatService.clearMemory();
        return ResponseEntity.ok(Map.of("message", "Conversation memory cleared"));
    }

    @GetMapping("/history")
    public ResponseEntity<Map<String, Object>> getHistory() {
        List<Message> history = chatService.getConversationHistory();
        
        System.out.println("📋 Trả về " + history.size() + " tin nhắn trong lịch sử");
        return ResponseEntity.ok(Map.of("history", history));
    }

}
