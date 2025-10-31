# OpenRouter Setup Guide 🚀

## Tổng quan
OpenRouter là một API gateway cho phép bạn truy cập nhiều mô hình AI khác nhau (GPT-4, Claude, Gemini, Llama, v.v.) thông qua một API duy nhất tương thích với OpenAI.

## Ưu điểm của OpenRouter
- ✅ Truy cập nhiều mô hình AI từ các nhà cung cấp khác nhau
- ✅ API tương thích với OpenAI (dễ dàng migrate)
- ✅ Giá cả cạnh tranh, thanh toán theo usage
- ✅ Không cần nhiều API key cho từng provider
- ✅ Hỗ trợ fallback tự động giữa các mô hình

## Bước 1: Tạo API Key

1. Truy cập [OpenRouter](https://openrouter.ai)
2. Đăng ký/Đăng nhập tài khoản
3. Vào [API Keys](https://openrouter.ai/keys)
4. Click "Create Key" để tạo API key mới
5. Copy API key (bắt đầu với `sk-or-v1-...`)

## Bước 2: Nạp Credits (Optional)

1. Vào [Credits](https://openrouter.ai/credits)
2. Chọn số tiền muốn nạp
3. Thanh toán qua card hoặc crypto

💡 **Lưu ý**: Một số mô hình miễn phí có rate limit thấp.

## Bước 3: Cấu hình Project

### 3.1. Tạo file `.env`

Copy file template và điền thông tin:

```bash
# Windows PowerShell
Copy-Item config-template.properties .env

# Hoặc thủ công copy/paste
```

### 3.2. Cấu hình `.env`

```properties
# OpenRouter API Configuration
OPENROUTER_API_KEY=sk-or-v1-your-actual-api-key-here
OPENROUTER_BASE_URL=https://openrouter.ai/api/v1
OPENROUTER_MODEL=openai/gpt-3.5-turbo
OPENROUTER_TEMPERATURE=0.7
OPENROUTER_MAX_TOKENS=1000
```

### 3.3. Các mô hình phổ biến trên OpenRouter

| Mô hình | ID | Giá (per 1M tokens) | Mô tả |
|---------|----|--------------------|-------|
| **GPT-3.5 Turbo** | `openai/gpt-3.5-turbo` | $0.50 / $1.50 | Nhanh, rẻ |
| **GPT-4 Turbo** | `openai/gpt-4-turbo` | $10 / $30 | Mạnh mẽ |
| **GPT-4o** | `openai/gpt-4o` | $2.50 / $10 | Cân bằng |
| **Claude 3.5 Sonnet** | `anthropic/claude-3.5-sonnet` | $3 / $15 | Code tốt |
| **Llama 3.1 70B** | `meta-llama/llama-3.1-70b-instruct` | $0.60 / $0.80 | Open source |
| **Gemini Pro** | `google/gemini-pro` | $0.50 / $1.50 | Google AI |

Xem danh sách đầy đủ tại: [OpenRouter Models](https://openrouter.ai/models)

## Bước 4: Chạy ứng dụng

### 4.1. Build project

```bash
# Windows PowerShell
.\mvnw clean package

# Hoặc nếu đã cài Maven
mvn clean package
```

### 4.2. Chạy ứng dụng

```bash
# Cách 1: Spring Boot Maven Plugin
.\mvnw spring-boot:run

# Cách 2: Chạy JAR file
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

### 4.3. Kiểm tra

Mở trình duyệt: `http://localhost:8080`

## Bước 5: Test API

### Test với curl (PowerShell)

```powershell
# Gửi tin nhắn
$body = @{message = "Hello OpenRouter!"} | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8080/api/chat/message" -Method POST -Body $body -ContentType "application/json"

# Xem lịch sử
Invoke-RestMethod -Uri "http://localhost:8080/api/chat/history" -Method GET

# Xóa memory
Invoke-RestMethod -Uri "http://localhost:8080/api/chat/memory" -Method DELETE
```

## Cấu trúc files đã thay đổi

```
Demo-1-J2EE/
├── config-template.properties     (✏️ Updated - OpenRouter template)
├── OPENROUTER_SETUP_GUIDE.md     (✨ New - Hướng dẫn này)
├── src/main/resources/
│   └── application.properties    (✏️ Updated - OpenRouter config)
└── src/main/java/com/example/demo/service/
    ├── OpenAIService.java        (✏️ Updated - Comments về OpenRouter)
    └── ChatService.java          (✏️ Updated - Log messages)
```

## Troubleshooting

### ❌ Lỗi: "No AI service is available"

**Nguyên nhân**: API key chưa được cấu hình

**Giải pháp**:
1. Kiểm tra file `.env` có tồn tại không
2. Kiểm tra biến `OPENROUTER_API_KEY` có giá trị đúng
3. Restart ứng dụng sau khi thay đổi `.env`

### ❌ Lỗi: "401 Unauthorized"

**Nguyên nhân**: API key không hợp lệ

**Giải pháp**:
1. Kiểm tra API key có đúng format `sk-or-v1-...`
2. Tạo API key mới tại [OpenRouter Keys](https://openrouter.ai/keys)
3. Đảm bảo không có khoảng trắng thừa trong API key

### ❌ Lỗi: "429 Too Many Requests"

**Nguyên nhân**: Vượt quá rate limit

**Giải pháp**:
1. Đợi một lúc trước khi thử lại
2. Nạp credits vào tài khoản
3. Chuyển sang mô hình có rate limit cao hơn

### ❌ Lỗi: "Insufficient credits"

**Nguyên nhân**: Hết credits

**Giải pháp**:
1. Nạp thêm credits tại [OpenRouter Credits](https://openrouter.ai/credits)
2. Sử dụng mô hình miễn phí (nếu có)

## Advanced Configuration

### Sử dụng nhiều mô hình

Bạn có thể thay đổi mô hình trong runtime bằng cách cập nhật biến môi trường:

```properties
# Mô hình mạnh cho tác vụ phức tạp
OPENROUTER_MODEL=openai/gpt-4o

# Mô hình nhanh cho tác vụ đơn giản
OPENROUTER_MODEL=openai/gpt-3.5-turbo

# Mô hình open source
OPENROUTER_MODEL=meta-llama/llama-3.1-70b-instruct
```

### Custom Headers (Optional)

OpenRouter hỗ trợ các custom headers để tracking:

```java
// Có thể thêm vào OpenAIService.java nếu cần
headers.put("HTTP-Referer", "https://your-app.com");
headers.put("X-Title", "Your App Name");
```

## Tài liệu tham khảo

- 📚 [OpenRouter Documentation](https://openrouter.ai/docs)
- 🔑 [API Keys Management](https://openrouter.ai/keys)
- 💰 [Pricing](https://openrouter.ai/models)
- 💬 [Discord Community](https://discord.gg/openrouter)

## So sánh với OpenAI Direct

| Tiêu chí | OpenAI Direct | OpenRouter |
|----------|--------------|------------|
| Số mô hình | ~5 | 100+ |
| Giá cả | Cố định | Cạnh tranh |
| Fallback | Không | Có |
| Setup | Đơn giản | Đơn giản |
| API format | OpenAI | OpenAI-compatible |

## Migration từ OpenAI

Nếu bạn đang sử dụng OpenAI trực tiếp, migration rất đơn giản:

1. ✅ Giữ nguyên code (API tương thích 100%)
2. ✅ Chỉ cần đổi `base-url` và `api-key`
3. ✅ Có thể thử nhiều mô hình khác nhau

---

**Chúc bạn thành công! 🎉**

Nếu có vấn đề, hãy tạo issue trên GitHub hoặc tham gia Discord community của OpenRouter.
