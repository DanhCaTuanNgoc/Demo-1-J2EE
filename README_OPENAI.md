# 🤖 Spring AI ChatBot - Intelligent Conversational Application

## 📝 Giới thiệu

Đây là ứng dụng chatbot thông minh được xây dựng bằng **Spring Boot** và **Spring AI Framework**, tích hợp với **OpenRouter** (hỗ trợ nhiều AI models) hoặc **Google Gemini** làm backup. Ứng dụng có khả năng:
- Ghi nhớ lịch sử hội thoại (Conversation Memory)
- Tự động gọi các hàm Java khi cần (Function Calling)
- Xử lý đa luồng hội thoại độc lập
- Tùy chỉnh System Prompt cho từng use case

## 🚀 Tính năng nổi bật

- ✅ **Conversational Memory**: AI nhớ toàn bộ ngữ cảnh cuộc trò chuyện
- ✅ **Function Calling**: AI tự động gọi Java functions (thời gian, tính toán, v.v.)
- ✅ **Multi-AI Support**: OpenRouter (primary) + Gemini (fallback)
- ✅ **System Prompt**: Tùy chỉnh tính cách và vai trò của AI
- ✅ **REST API**: Dễ dàng tích hợp với frontend/mobile
- ✅ **Web UI**: Giao diện chat responsive, thân thiện

## 📋 Yêu cầu hệ thống

- Java 17+
- Maven 3.6+
- OpenAI API Key (hoặc Gemini API Key)

## ⚙️ Cài đặt nhanh

### 1. Clone repository
```bash
git clone <repository-url>
cd Demo-1-J2EE
```

### 2. Cấu hình API Key

Tạo file `.env` từ template:
```bash
cp .env.example .env
```

Chỉnh sửa `.env` và thêm OpenAI API key:
```env
OPENAI_API_KEY=sk-your-actual-openai-api-key-here
OPENAI_MODEL=gpt-3.5-turbo
OPENAI_TEMPERATURE=0.7
```

**Lấy API Key:**
- OpenAI: https://platform.openai.com/api-keys
- Gemini (backup): https://aistudio.google.com/app/apikey

### 3. Build và chạy
```bash
# Build project
mvn clean install

# Chạy application
mvn spring-boot:run
```

### 4. Truy cập ứng dụng
- Web UI: http://localhost:8080
- API Endpoint: http://localhost:8080/api/chat/message

## 📚 API Documentation

### POST /api/chat/message
Gửi tin nhắn và nhận phản hồi từ AI

**Request:**
```json
{
  "message": "Hello, how are you?"
}
```

**Response:**
```json
{
  "response": "I'm doing well, thank you! How can I help you today?"
}
```

### GET /api/chat/history
Lấy lịch sử trò chuyện

**Response:**
```json
{
  "history": [
    {
      "content": "Hello",
      "type": "USER"
    },
    {
      "content": "Hi! How can I help you?",
      "type": "ASSISTANT"
    }
  ]
}
```

### DELETE /api/chat/memory
Xóa lịch sử trò chuyện

**Response:**
```json
{
  "message": "Conversation memory cleared"
}
```

## 🎯 Function Calling - Mở rộng khả năng AI

### **Cách thức hoạt động**

Function Calling cho phép AI tự động gọi các Java methods khi cần thiết:

```
User: "Mấy giờ rồi?"
  ↓
AI phân tích → Cần thông tin thời gian thực
  ↓
AI gọi: getCurrentTime()
  ↓
Java method execute → Return: "2025-10-30 21:30:45"
  ↓
AI nhận kết quả → Format thành câu trả lời tự nhiên
  ↓
Response: "Bây giờ là 21:30:45 ngày 30 tháng 10 năm 2025"
```

### **Functions hiện có**

#### 1️⃣ **getCurrentTime()** - Lấy thời gian hiện tại
```java
@Bean
@Description("Get the current date and time in Vietnamese format")
public Function<TimeRequest, TimeResponse> getCurrentTime() {
    return request -> {
        LocalDateTime now = LocalDateTime.now();
        String time = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return new TimeResponse(time, "Asia/Ho_Chi_Minh");
    };
}
```

**Ví dụ sử dụng:**
- "Mấy giờ rồi?"
- "Cho tôi biết thời gian hiện tại"
- "What time is it now?"

#### 2️⃣ **calculator()** - Tính toán số học
```java
@Bean
@Description("Calculate basic arithmetic operations: addition, subtraction, multiplication, division")
public Function<CalculatorRequest, CalculatorResponse> calculator() {
    return request -> {
        double result;
        switch (request.operation()) {
            case "add" -> result = request.num1() + request.num2();
            case "subtract" -> result = request.num1() - request.num2();
            case "multiply" -> result = request.num1() * request.num2();
            case "divide" -> result = request.num1() / request.num2();
            default -> throw new IllegalArgumentException("Unknown operation");
        }
        return new CalculatorResponse(result, request.operation());
    };
}
```

**Ví dụ sử dụng:**
- "Tính 15 + 27"
- "123 nhân 456 bằng bao nhiêu?"
- "Calculate 100 divided by 5"

### **Cách thêm Function mới**

**Bước 1:** Định nghĩa Request/Response record trong `FunctionConfig.java`
```java
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonClassDescription("Request to get weather information")
public record WeatherRequest(
    @JsonProperty(required = true, value = "city")
    @JsonPropertyDescription("The city name to get weather for")
    String city
) {}

@JsonInclude(JsonInclude.Include.NON_NULL)
public record WeatherResponse(
    String city,
    String temperature,
    String condition
) {}
```

**Bước 2:** Tạo Bean với annotation `@Description`
```java
@Bean
@Description("Get current weather information for a specific city")
public Function<WeatherRequest, WeatherResponse> getWeather() {
    return request -> {
        // Call weather API here
        String temp = weatherService.getTemperature(request.city());
        String condition = weatherService.getCondition(request.city());
        return new WeatherResponse(request.city(), temp, condition);
    };
}
```

**Bước 3:** Đăng ký function trong `OpenAIService.java`
```java
responseText = openAIService.generateText(
    userMessage, 
    DEFAULT_CONVERSATION_ID,
    "getCurrentTime",
    "calculator",
    "getWeather"  // ✅ Thêm function mới
);
```

**Bước 4:** Test
```
User: "Thời tiết ở Hà Nội thế nào?"
AI → Tự động gọi getWeather("Hà Nội") → Trả về kết quả
```

## 🏗️ Kiến trúc hệ thống

### Tổng quan luồng xử lý

```
┌─────────────────────────────────────────────────────────────────────────┐
│                            CLIENT (Browser)                             │
│                         • index.html (Web UI)                           │
│                         • JavaScript fetch API                          │
└────────────────────────────────┬────────────────────────────────────────┘
                                 │ HTTP POST /api/chat/message
                                 ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                     CONTROLLER LAYER (REST API)                         │
│                                                                          │
│  📌 ChatController.java                                                 │
│     ├─ POST /api/chat/message    → Nhận tin nhắn từ user              │
│     ├─ GET /api/chat/history     → Lấy lịch sử hội thoại              │
│     └─ DELETE /api/chat/memory   → Xóa memory                          │
└────────────────────────────────┬────────────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                      SERVICE LAYER (Business Logic)                     │
│                                                                          │
│  📌 ChatService.java (Orchestrator - điều phối logic chính)            │
│     ├─ Quản lý conversation history                                     │
│     ├─ Quyết định dùng AI service nào (OpenRouter/Gemini)             │
│     └─ Xử lý fallback giữa các AI services                             │
│                                  │                                       │
│         ┌────────────────────────┼────────────────────────┐            │
│         ▼                        ▼                        ▼             │
│  ┌──────────────┐      ┌──────────────────┐    ┌──────────────────┐   │
│  │ OpenAIService│      │ GeminiRestService│    │   TimeService    │   │
│  │  (Primary)   │      │    (Fallback)    │    │  (Utility)       │   │
│  └──────┬───────┘      └──────────────────┘    └──────────────────┘   │
│         │                                                               │
│         │ Sử dụng Spring AI Framework                                  │
│         ▼                                                               │
│  ┌─────────────────────────────────────────────────┐                   │
│  │        Spring AI ChatClient                     │                   │
│  │  ├─ Memory Management (ChatMemory)              │                   │
│  │  ├─ Function Calling (FunctionConfig)           │                   │
│  │  └─ System Prompt Injection                     │                   │
│  └─────────────────────────────────────────────────┘                   │
└────────────────────────────────┬────────────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                    CONFIGURATION LAYER (Spring Beans)                   │
│                                                                          │
│  📌 ChatMemoryConfig.java    → Cấu hình InMemoryChatMemory             │
│  📌 FunctionConfig.java      → Định nghĩa functions AI có thể gọi      │
│  📌 OpenRouterConfig.java    → Cấu hình OpenRouter connection           │
│  📌 CorsConfig.java          → Cấu hình CORS cho API                    │
│  📌 DotenvConfig.java        → Load biến môi trường từ .env             │
└────────────────────────────────┬────────────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                    EXTERNAL AI SERVICES                                 │
│                                                                          │
│  🌐 OpenRouter API (https://openrouter.ai)                              │
│     └─ Hỗ trợ nhiều models: GPT-3.5, GPT-4, Claude, Llama, v.v.       │
│                                                                          │
│  🌐 Google Gemini API (Backup)                                          │
│     └─ Gemini Flash/Pro                                                 │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 📂 Cấu trúc dự án và ý nghĩa từng file

### 🔹 **1. Controller Layer** (`src/main/java/com/example/demo/controller/`)

#### **ChatController.java**
- **Vai trò**: REST API endpoints cho client
- **Chức năng**:
  - `POST /api/chat/message` - Nhận tin nhắn từ user, gửi đến ChatService
  - `GET /api/chat/history` - Trả về lịch sử hội thoại
  - `DELETE /api/chat/memory` - Xóa memory để bắt đầu cuộc trò chuyện mới
- **Xử lý lỗi**: Validation input, exception handling, HTTP status codes

---

### 🔹 **2. Service Layer** (`src/main/java/com/example/demo/service/`)

#### **ChatService.java**
- **Vai trò**: Orchestrator - điều phối logic nghiệp vụ chính
- **Chức năng**:
  - Quản lý `conversationHistory` (lịch sử local)
  - Quyết định sử dụng OpenAIService (ưu tiên) hoặc GeminiRestService (backup)
  - Tự động fallback nếu service chính gặp lỗi
- **Workflow**:
  1. Nhận message từ Controller
  2. Lưu message vào local history
  3. Gọi OpenAIService (nếu enabled)
  4. Nếu fail → fallback sang GeminiRestService
  5. Trả response về Controller

#### **OpenAIService.java** ⭐ (Service chính)
- **Vai trò**: Tích hợp Spring AI Framework với OpenRouter
- **Tính năng**:
  - **Memory Management**: Tự động lưu/lấy lịch sử hội thoại
  - **Function Calling**: AI tự động gọi Java functions khi cần
  - **System Prompt**: Inject system prompt để định hình tính cách AI
  - **Conversation ID**: Quản lý nhiều cuộc hội thoại độc lập
- **Cách hoạt động**:
  ```java
  chatClient.prompt()
    .user(userMessage)                    // Tin nhắn từ user
    .advisors(ChatMemoryAdvisor)          // Tự động thêm context từ memory
    .functions("getCurrentTime", "calc")  // Đăng ký functions
    .call()
    .chatResponse()                       // Nhận response cuối cùng
  ```

#### **GeminiRestService.java**
- **Vai trò**: Backup service khi OpenRouter không khả dụng
- **Đặc điểm**: 
  - Gọi trực tiếp REST API của Google Gemini
  - Không có memory hoặc function calling (đơn giản hơn)
  - Miễn phí (có rate limit)

#### **TimeService.java**
- **Vai trò**: Utility service cung cấp thời gian hiện tại
- **Sử dụng**: Có thể được AI gọi qua Function Calling

---

### 🔹 **3. Configuration Layer** (`src/main/java/com/example/demo/config/`)

#### **ChatMemoryConfig.java**
- **Vai trò**: Cấu hình Chat Memory (bộ nhớ hội thoại)
- **Bean tạo ra**: `InMemoryChatMemory` - lưu trữ lịch sử trong RAM
- **Lợi ích**: Spring AI tự động inject vào ChatClient

#### **FunctionConfig.java** ⭐ (Quan trọng)
- **Vai trò**: Định nghĩa các Java functions mà AI có thể gọi
- **Functions hiện có**:
  - `getCurrentTime()` - Trả về thời gian hiện tại
  - `calculator()` - Tính toán cơ bản (+, -, *, /)
- **Cách hoạt động**:
  1. AI nhận message: "Mấy giờ rồi?"
  2. AI quyết định cần gọi `getCurrentTime()`
  3. Spring AI tự động execute function
  4. AI nhận kết quả và format thành câu trả lời tự nhiên
- **Mở rộng**: Thêm `@Bean` + `@Description` để tạo functions mới

#### **OpenRouterConfig.java**
- **Vai trò**: Cấu hình kết nối với OpenRouter API
- **Thiết lập**: Base URL, model selection, rate limiting

#### **CorsConfig.java**
- **Vai trò**: Cấu hình CORS để frontend có thể gọi API
- **Cho phép**: Tất cả origins, methods (POST, GET, DELETE)

#### **DotenvConfig.java**
- **Vai trò**: Load biến môi trường từ file `.env`
- **Tại sao cần**: Bảo mật API keys, không commit keys lên Git

---

### 🔹 **4. Model Layer** (`src/main/java/com/example/demo/model/`)

#### **Message.java**
- **Vai trò**: POJO (Plain Old Java Object) đại diện cho 1 tin nhắn
- **Properties**:
  - `content` (String) - Nội dung tin nhắn
  - `type` (MessageType) - USER hoặc ASSISTANT
- **Sử dụng**: Lưu trữ trong `conversationHistory`

---

### 🔹 **5. Frontend** (`src/main/resources/static/`)

#### **index.html**
- **Vai trò**: Single-page chat UI
- **Tính năng**:
  - Form input để gửi message
  - Hiển thị lịch sử chat
  - Nút Clear Memory
  - Responsive design
- **API Calls**:
  ```javascript
  fetch('/api/chat/message', {
    method: 'POST',
    body: JSON.stringify({message: userInput})
  })
  ```

---

### 🔹 **6. Configuration Files** (Root level)

#### **application.properties**
- **Vai trò**: Cấu hình Spring Boot application
- **Các thiết lập quan trọng**:
  ```properties
  # OpenRouter Configuration
  spring.ai.openai.api-key=${OPENROUTER_API_KEY}
  spring.ai.openai.base-url=https://openrouter.ai/api/v1
  spring.ai.openai.chat.options.model=openai/gpt-3.5-turbo
  spring.ai.openai.chat.options.temperature=0.7
  
  # System Prompt (MỚI)
  spring.ai.openai.system-prompt=You are a helpful AI assistant...
  
  # Gemini Backup
  gemini.api.key=${GEMINI_API_KEY}
  ```

#### **.env**
- **Vai trò**: Lưu trữ API keys và biến môi trường (KHÔNG commit lên Git)
- **Nội dung**:
  ```env
  OPENROUTER_API_KEY=sk-or-v1-xxxxx
  GEMINI_API_KEY=AIzaSyxxxxx
  ```

#### **pom.xml**
- **Vai trò**: Maven configuration file
- **Dependencies chính**:
  - `spring-boot-starter-web` - REST API support
  - `spring-ai-openai-spring-boot-starter` - Spring AI integration
  - `dotenv-java` - Load .env files

---

## 🔄 Workflow chi tiết

### **Kịch bản 1: User gửi message thông thường**

```
1. User nhập: "Xin chào, bạn là ai?"
   └─> JavaScript gọi: POST /api/chat/message

2. ChatController nhận request
   └─> Validate input → Gọi chatService.chat(message)

3. ChatService xử lý
   └─> Lưu message vào conversationHistory
   └─> Kiểm tra OpenAIService.isEnabled() → TRUE
   └─> Gọi openAIService.generateText(message, conversationId)

4. OpenAIService xử lý
   └─> ChatClient.prompt()
       ├─ .user(message)                     // Message từ user
       ├─ .advisors(ChatMemoryAdvisor)       // Thêm context từ memory
       ├─ .functions("getCurrentTime", ...)  // Đăng ký functions
       └─> Gửi đến OpenRouter API

5. OpenRouter (GPT-3.5) xử lý
   └─> Đọc system prompt: "You are a helpful AI assistant..."
   └─> Đọc conversation history từ memory
   └─> Quyết định KHÔNG cần gọi function
   └─> Trả về: "Xin chào! Tôi là trợ lý AI thông minh..."

6. OpenAIService nhận response
   └─> Spring AI tự động lưu vào ChatMemory
   └─> Trả response về ChatService

7. ChatService
   └─> Lưu AI response vào conversationHistory
   └─> Trả về ChatController

8. ChatController
   └─> Trả JSON: {"response": "Xin chào! Tôi là..."}

9. Frontend nhận response
   └─> Hiển thị trong chat UI
```

---

### **Kịch bản 2: User hỏi về thời gian (Function Calling)**

```
1. User nhập: "Bây giờ là mấy giờ?"
   └─> POST /api/chat/message

2. [Steps 2-4 giống kịch bản 1]

5. OpenRouter (GPT-3.5) xử lý
   └─> Nhận câu hỏi về thời gian
   └─> Quyết định cần gọi function: getCurrentTime()
   └─> Trả về function call request

6. Spring AI nhận function call request
   └─> Tự động execute FunctionConfig.getCurrentTime()
   └─> Nhận kết quả: "2025-10-30 21:30:45"
   └─> Gửi lại kết quả cho AI

7. OpenRouter nhận function result
   └─> Format thành câu trả lời tự nhiên
   └─> Trả về: "Bây giờ là 21:30:45 ngày 30/10/2025"

8. [Steps 6-9 giống kịch bản 1]
```

---

### **Kịch bản 3: OpenRouter fail → Fallback Gemini**

```
1. User nhập message → ChatController → ChatService

2. ChatService gọi OpenAIService
   └─> OpenRouter API error (401/429/timeout)
   └─> Exception được throw

3. ChatService catch exception
   └─> Log warning: "OpenRouter failed, switching to Gemini..."
   └─> Gọi geminiRestService.generateText(message)

4. GeminiRestService
   └─> Gọi trực tiếp Gemini REST API
   └─> Trả response (không có memory/function calling)

5. ChatService nhận response
   └─> Lưu vào conversationHistory
   └─> Trả về Controller → Frontend
```

## 🔧 Configuration Chi tiết

### **application.properties** - Trái tim cấu hình

```properties
# ===============================
# Spring AI OpenRouter Configuration
# ===============================
spring.ai.openai.api-key=${OPENROUTER_API_KEY:your_openrouter_api_key_here}
spring.ai.openai.base-url=${OPENROUTER_BASE_URL:https://openrouter.ai/api/v1}
spring.ai.openai.chat.options.model=${OPENROUTER_MODEL:openai/gpt-3.5-turbo}
spring.ai.openai.chat.options.temperature=${OPENROUTER_TEMPERATURE:0.7}
spring.ai.openai.chat.options.max-tokens=${OPENROUTER_MAX_TOKENS:1000}

# System Prompt - Định hình tính cách AI
spring.ai.openai.system-prompt=${SYSTEM_PROMPT:You are a helpful AI assistant specialized in Java, Spring Boot, and J2EE. \
You can help with code explanations, debugging, and provide technical guidance. \
You have access to function calling capabilities to provide real-time information like current time. \
Always be concise, accurate, and professional in your responses. \
When writing code, follow Java best practices and Spring Boot conventions.}

# ===============================
# Gemini Backup Configuration
# ===============================
gemini.api.key=${GEMINI_API_KEY:}
gemini.api.url=${GEMINI_API_URL:https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent}
```

### **Giải thích các tham số**

| Tham số | Ý nghĩa | Giá trị đề xuất |
|---------|---------|-----------------|
| `api-key` | API key từ OpenRouter/Gemini | Lấy từ biến môi trường |
| `base-url` | Endpoint của AI service | OpenRouter: `https://openrouter.ai/api/v1` |
| `model` | AI model sử dụng | `openai/gpt-3.5-turbo`, `anthropic/claude-3.5-sonnet` |
| `temperature` | Độ sáng tạo (0-1) | `0.7` (cân bằng), `0.3` (chính xác), `0.9` (sáng tạo) |
| `max-tokens` | Giới hạn độ dài response | `1000` (trung bình), `4000` (dài) |
| `system-prompt` | Chỉ dẫn cho AI về vai trò | Tùy chỉnh theo use case |

### **Các model khả dụng trên OpenRouter**

#### **Nhanh & Rẻ (Development)**
```properties
# GPT-3.5 Turbo - Nhanh, rẻ, tốt cho hầu hết use case
spring.ai.openai.chat.options.model=openai/gpt-3.5-turbo
# Chi phí: ~$0.002 per request

# Llama 3.1 8B - Miễn phí (giới hạn rate)
spring.ai.openai.chat.options.model=meta-llama/llama-3.1-8b-instruct:free
```

#### **Thông minh & Mạnh (Production)**
```properties
# GPT-4 Turbo - Cân bằng tốt nhất
spring.ai.openai.chat.options.model=openai/gpt-4-turbo

# Claude 3.5 Sonnet - Tốt cho code, reasoning
spring.ai.openai.chat.options.model=anthropic/claude-3.5-sonnet

# GPT-4o - Mới nhất, nhanh hơn GPT-4
spring.ai.openai.chat.options.model=openai/gpt-4o
```

#### **Gemini (Backup - Miễn phí)**
```properties
gemini.api.url=https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent
# Rate limit: 15 requests/minute, 1M tokens/minute
```

## 💰 Chi phí

### OpenAI (GPT-3.5-Turbo)
- Input: $0.50 / 1M tokens
- Output: $1.50 / 1M tokens
- Credit miễn phí: $5 cho tài khoản mới
- Ước tính: ~$0.002 per request

### Google Gemini
- Gemini Flash: **Miễn phí** (có rate limit: 15 RPM, 1M TPM)
- Gemini Pro: $0.50 / 1M tokens

## 🐛 Troubleshooting

### "No AI service available"
- Kiểm tra file `.env` đã được tạo chưa
- Kiểm tra `OPENAI_API_KEY` đã được set đúng
- Restart application sau khi thay đổi `.env`

### "401 Unauthorized"
- API key không hợp lệ
- API key đã hết hạn
- Kiểm tra lại tại https://platform.openai.com/api-keys

### "429 Rate Limit Exceeded"
- Đã vượt quá giới hạn request
- Chờ một chút rồi thử lại
- Nâng cấp plan OpenAI nếu cần

### Compile errors về Spring AI
```bash
# Xóa cache và build lại
mvn clean install -U
```

---

## 💡 System Prompt - Tùy chỉnh tính cách AI

### **System Prompt là gì?**

System Prompt là chỉ dẫn được gửi cho AI trước mỗi cuộc hội thoại, giúp định hình:
- **Vai trò** của AI (trợ lý, chuyên gia, giáo viên...)
- **Phong cách** giao tiếp (chuyên nghiệp, thân thiện, hài hước...)
- **Giới hạn** kiến thức và hành vi
- **Ngữ cảnh** hoạt động (lĩnh vực, ngôn ngữ...)

### **Cấu hình hiện tại**

File: `application.properties`
```properties
spring.ai.openai.system-prompt=${SYSTEM_PROMPT:You are a helpful AI assistant specialized in Java, Spring Boot, and J2EE. \
You can help with code explanations, debugging, and provide technical guidance. \
You have access to function calling capabilities to provide real-time information like current time. \
Always be concise, accurate, and professional in your responses. \
When writing code, follow Java best practices and Spring Boot conventions.}
```

### **Ví dụ System Prompts theo use case**

#### 1️⃣ **Trợ lý lập trình (Mặc định)**
```properties
spring.ai.openai.system-prompt=You are an expert Java and Spring Boot developer. \
Help users with code, debugging, best practices, and architecture decisions. \
Always provide working code examples with explanations. \
Follow Java coding conventions and design patterns.
```

#### 2️⃣ **Chatbot chăm sóc khách hàng**
```properties
spring.ai.openai.system-prompt=You are a friendly customer support assistant for ABC Company. \
Always greet customers warmly and professionally. \
Help them with product inquiries, order tracking, and technical issues. \
If you don't know the answer, politely ask them to contact human support. \
Never share confidential company information.
```

#### 3️⃣ **Gia sư tiếng Anh**
```properties
spring.ai.openai.system-prompt=You are an experienced English tutor. \
Help students improve their English skills through conversation, grammar correction, and vocabulary building. \
Always correct mistakes gently and provide explanations. \
Use simple language and give examples. \
Encourage students and make learning fun.
```

#### 4️⃣ **Trợ lý y tế (cẩn thận)**
```properties
spring.ai.openai.system-prompt=You are a medical information assistant. \
Provide general health information and wellness tips based on scientific sources. \
IMPORTANT: Always remind users that you are NOT a doctor and cannot provide medical diagnosis. \
Advise users to consult healthcare professionals for medical concerns. \
Never recommend specific medications or treatments.
```

#### 5️⃣ **Bot hài hước**
```properties
spring.ai.openai.system-prompt=You are a witty and humorous AI assistant. \
Make users laugh with clever jokes, puns, and funny responses. \
But always stay respectful and appropriate. \
Help users with their questions while keeping the conversation fun and engaging.
```

### **Cách thay đổi System Prompt**

**Cách 1:** Thông qua biến môi trường `.env`
```env
SYSTEM_PROMPT=You are a helpful coding assistant specializing in Java and Spring Boot...
```

**Cách 2:** Trực tiếp trong `application.properties`
```properties
spring.ai.openai.system-prompt=Your custom prompt here...
```

**Cách 3:** Dynamic change (nâng cao)
```java
// Trong OpenAIService.java
ChatClient customClient = ChatClient.builder(chatModel)
    .defaultSystem("Custom system prompt for this specific conversation")
    .build();
```

### **Tips để viết System Prompt tốt**

✅ **DO:**
- Rõ ràng, cụ thể về vai trò
- Đưa ra ví dụ cụ thể về tone và style
- Nêu rõ những gì AI nên và không nên làm
- Sử dụng ngôn ngữ đơn giản, dễ hiểu

❌ **DON'T:**
- Quá dài, phức tạp (tốn tokens)
- Mơ hồ, không rõ mục đích
- Yêu cầu AI làm điều không thể (truy cập internet, nhớ mãi mãi...)
- Prompt mâu thuẫn nhau

---

## 📖 Tài liệu tham khảo

- [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/)
- [OpenAI API Documentation](https://platform.openai.com/docs)
- [Google Gemini API](https://ai.google.dev/docs)

## 📝 License

MIT License

## 👥 Contributing

Pull requests are welcome! For major changes, please open an issue first.
