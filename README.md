# 🤖 Chatbot với Memory + Function Calling sử dụng Spring AI và Gemini

Đây là một chatbot thông minh được xây dựng bằng **Spring Boot** và **Google Gemini AI**, có khả năng **ghi nhớ cuộc trò chuyện** và **thực thi các hành động cụ thể** thông qua Function Calling.

## 🎯 Mục tiêu Demo

Demo này minh họa 2 tính năng chính của AI Chatbot:

### 🧠 **Memory (Khả năng nhớ)**
- Chatbot có thể nhớ thông tin từ cuộc trò chuyện trước đó
- Ví dụ: "Tôi tên Tuấn" → "Bạn biết tôi tên gì?" → "Bạn tên là Tuấn"

### 🔧 **Function Calling (Gọi hàm thực thi)**
- Chatbot có thể thực hiện hành động cụ thể dựa trên lệnh
- Ví dụ: "Tính 2 + 3" → "Kết quả phép cộng là: 5.0"
- Ví dụ: "Bây giờ mấy giờ?" → "Bây giờ là: 2025-10-17 12:00:39"

## ✨ Tính năng

- ✅ **Memory System**: Ghi nhớ toàn bộ cuộc trò chuyện trong session
- ✅ **Function Calling**: Thực thi các hành động cụ thể (tính toán, thời gian, truy vấn memory)
- ✅ **Context Awareness**: Sử dụng lịch sử cuộc trò chuyện để trả lời chính xác
- ✅ **Web Interface**: Giao diện web đơn giản và thân thiện
- ✅ **REST API**: API endpoints để tương tác với chatbot
- ✅ **UTF-8 Support**: Hỗ trợ đầy đủ tiếng Việt
- ✅ **Error Handling**: Xử lý lỗi thân thiện và chi tiết

## 🚀 Cài đặt và Chạy

### 1. Cấu hình Gemini AI

Bạn cần có **Gemini API Key** từ Google AI Studio:

1. Truy cập [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Tạo API key mới
3. Copy API key để sử dụng

### 2. Cấu hình Environment Variables

**Cách 1: Sử dụng file .env (Khuyến nghị)**

1. Copy file `config-template.properties` thành `.env`:
```bash
cp config-template.properties .env
```

2. Cập nhật file `.env` với API key thực:
```bash
GEMINI_API_KEY=your_actual_gemini_api_key_here
GEMINI_API_URL=https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent
```

**Cách 2: Set environment variables trực tiếp**

```bash
# Windows (PowerShell)
$env:GEMINI_API_KEY="your_actual_gemini_api_key_here"

# Linux/Mac
export GEMINI_API_KEY="your_actual_gemini_api_key_here"
```

**Cách 3: Cập nhật trực tiếp trong application.properties**

```properties
gemini.api.key=your_actual_gemini_api_key_here
gemini.api.url=https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent
```

> ⚠️ **Lưu ý bảo mật**: Không commit API key vào Git. File `.env` đã được thêm vào `.gitignore`.

### 3. Chạy ứng dụng

```bash
# Chạy với Maven (Windows)
mvn spring-boot:run

# Chạy với Maven (Linux/Mac)
./mvnw spring-boot:run

# Hoặc build và chạy
mvn clean package
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

### 4. Truy cập ứng dụng

Mở trình duyệt và truy cập: **http://localhost:8080**

## 🧪 Test Demo

Sau khi ứng dụng khởi động, bạn có thể test các tính năng:

### Test Memory (Khả năng nhớ)
```
User: "Tôi tên Tuấn"
Bot: "Chào anh Tuấn! Rất vui được biết tên anh."

User: "Bạn biết tôi tên gì không?"
Bot: "Vâng, tôi nhớ anh tên Tuấn ạ."
```

### Test Function Calling (Gọi hàm thực thi)
```
User: "Tính 2 + 3"
Bot: "Kết quả phép cộng là: 5.0"

User: "Bây giờ mấy giờ?"
Bot: "Bây giờ là: 2025-10-17 12:00:39"
```

### Test Context Awareness (Nhận biết ngữ cảnh)
```
User: "Nhắc tôi về tên của tôi"
Bot: "Bạn tên là Tuấn."
```

## API Endpoints

### POST /api/chat/message
Gửi tin nhắn đến chatbot

```json
{
  "message": "Bây giờ là mấy giờ?"
}
```

Response:
```json
{
  "response": "Bây giờ là 2024-01-15 14:30:25"
}
```

### DELETE /api/chat/memory
Xóa toàn bộ lịch sử cuộc trò chuyện

Response:
```json
{
  "message": "Conversation memory cleared"
}
```

### GET /api/chat/history
Lấy lịch sử cuộc trò chuyện

Response:
```json
{
  "history": [...]
}
```

## Cách sử dụng

1. **Hỏi về thời gian**: 
   - "Bây giờ là mấy giờ?"
   - "Thời gian hiện tại là gì?"
   - "Time" (tiếng Anh)
2. **Trò chuyện thông thường**: Chatbot sẽ nhớ toàn bộ cuộc trò chuyện và sử dụng Gemini AI để trả lời
3. **Xóa memory**: Sử dụng nút "Xóa Memory" để reset cuộc trò chuyện

## 📁 Cấu trúc dự án

```
demo/
├── src/main/java/com/example/demo/
│   ├── config/
│   │   └── CorsConfig.java              # Cấu hình CORS cho web interface
│   ├── controller/
│   │   └── ChatController.java          # REST API endpoints
│   ├── model/
│   │   └── Message.java                 # Model cho tin nhắn cuộc trò chuyện
│   ├── service/
│   │   ├── ChatService.java             # Core logic: Memory + Function Calling
│   │   ├── GeminiRestService.java       # Service gọi Gemini REST API
│   │   └── TimeService.java             # Service lấy thời gian hiện tại
│   └── DemoApplication.java             # Main Spring Boot application
├── src/main/resources/
│   ├── static/
│   │   └── index.html                   # Web interface (HTML/CSS/JS)
│   └── application.properties           # Cấu hình ứng dụng
├── config-template.properties           # Template cho file .env
├── .gitignore                          # Git ignore file
├── pom.xml                             # Maven dependencies
└── README.md                           # Tài liệu dự án
```

## 🔧 Nhiệm vụ từng file

### **Core Application**
- **`DemoApplication.java`**: Main class khởi động Spring Boot, kiểm tra cấu hình
- **`application.properties`**: Cấu hình Gemini API, encoding UTF-8, logging

### **Controller Layer**
- **`ChatController.java`**: REST API endpoints
  - `POST /api/chat/message`: Gửi tin nhắn đến chatbot
  - `DELETE /api/chat/memory`: Xóa lịch sử cuộc trò chuyện
  - `GET /api/chat/history`: Lấy lịch sử cuộc trò chuyện

### **Service Layer**
- **`ChatService.java`**: **Core logic** của demo
  - **Memory System**: Lưu trữ và sử dụng lịch sử cuộc trò chuyện
  - **Function Calling**: Phát hiện và thực thi các hành động cụ thể
  - **Context Building**: Tạo prompt với context cho Gemini API
  - **Calculator**: Thực hiện phép tính toán
  - **Memory Query**: Tìm kiếm thông tin trong lịch sử

- **`GeminiRestService.java`**: Gọi Gemini REST API
  - Gửi request đến Gemini API
  - Xử lý response và error handling
  - Quản lý API key và URL

- **`TimeService.java`**: Lấy thời gian hiện tại
  - Format thời gian theo định dạng yyyy-MM-dd HH:mm:ss
  - Xử lý lỗi và trả về thông báo thân thiện

### **Model Layer**
- **`Message.java`**: Model cho tin nhắn
  - `content`: Nội dung tin nhắn
  - `type`: Loại tin nhắn (USER/ASSISTANT)
  - `MessageType`: Enum định nghĩa loại tin nhắn

### **Configuration**
- **`CorsConfig.java`**: Cấu hình CORS cho phép web interface gọi API
- **`config-template.properties`**: Template cho file .env
- **`.gitignore`**: Bảo vệ file .env và các file nhạy cảm

### **Frontend**
- **`index.html`**: Web interface đơn giản
  - Gửi tin nhắn đến chatbot
  - Hiển thị cuộc trò chuyện
  - Nút xóa memory

## 🔄 Luồng hoạt động

1. **User gửi tin nhắn** → `ChatController`
2. **ChatController** → `ChatService.chat()`
3. **ChatService** kiểm tra:
   - **Function Calling**: Có phải câu hỏi về thời gian/tính toán/memory?
   - **Memory**: Sử dụng lịch sử cuộc trò chuyện
4. **Nếu Function Calling**: Thực thi hàm tương ứng
5. **Nếu không**: Gọi `GeminiRestService` với context
6. **Trả về kết quả** cho user

## 📦 Dependencies chính

- **Spring Boot 3.5.6**: Framework chính
- **Spring Web**: REST API và web interface
- **Java 17**: Runtime environment
- **Maven**: Build tool và dependency management

## 🛠️ Troubleshooting

### ❌ Lỗi API Key
```
Error: Gemini REST service is not available
```
**Giải pháp:**
- Kiểm tra `GEMINI_API_KEY` trong file `.env`
- Đảm bảo API key hợp lệ từ Google AI Studio
- Kiểm tra kết nối internet

### ❌ Lỗi Encoding (Ký tự tiếng Việt hiển thị sai)
```
Hiển thị: "TÃ´i tÃªn Tuáº¥n" thay vì "Tôi tên Tuấn"
```
**Giải pháp:**
- Chạy ứng dụng với JVM arguments: `-Dfile.encoding=UTF-8`
- Hoặc sử dụng script: `mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dfile.encoding=UTF-8"`

### ❌ Lỗi CORS
```
Access to fetch at 'http://localhost:8080/api/chat/message' from origin 'http://localhost:8080' has been blocked by CORS policy
```
**Giải pháp:**
- Ứng dụng đã được cấu hình CORS tự động
- Nếu vẫn gặp lỗi, kiểm tra browser console và network tab

### ❌ Lỗi Function Calling
```
Function Calling không hoạt động
```
**Giải pháp:**
- Kiểm tra logs console để xem chi tiết
- Đảm bảo từ khóa được nhận diện đúng (ví dụ: "tính", "thời gian", "nhắc")

### ❌ Lỗi Memory
```
Chatbot không nhớ thông tin
```
**Giải pháp:**
- Kiểm tra `conversationHistory` có được lưu trữ không
- Xem logs để kiểm tra context có được gửi đến Gemini API

## 📝 Logs và Debug

Ứng dụng có logging chi tiết để debug:

```
💬 NHẬN TIN NHẮN: Tôi tên Tuấn
📝 Đã thêm tin nhắn vào lịch sử cuộc trò chuyện
🧠 Sử dụng context từ 0 tin nhắn trước
🤖 Đang gọi Gemini REST API với context...
✅ Nhận phản hồi từ Gemini REST API
📤 Phản hồi: Chào anh Tuấn! Rất vui được biết tên anh.
```

## 🎯 Kết luận

Demo này minh họa thành công 2 tính năng chính của AI Chatbot:
- **Memory**: Khả năng nhớ thông tin trong cuộc trò chuyện
- **Function Calling**: Khả năng thực thi các hành động cụ thể

Đây là foundation tốt để phát triển các chatbot phức tạp hơn với nhiều function calling và memory management nâng cao.
