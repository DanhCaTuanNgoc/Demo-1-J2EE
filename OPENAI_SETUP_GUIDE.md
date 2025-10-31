# Spring AI OpenAI Configuration Guide

## 📝 Tổng quan
Dự án này đã được chuyển đổi từ Google Gemini sang **Spring AI OpenAI**.

## 🔑 Lấy API Key

### OpenAI API Key (Primary)
1. Truy cập: https://platform.openai.com/api-keys
2. Đăng nhập/Đăng ký tài khoản OpenAI
3. Tạo API key mới
4. Copy API key (bắt đầu với `sk-...`)

### Gemini API Key (Backup - Optional)
1. Truy cập: https://aistudio.google.com/app/apikey
2. Đăng nhập Google Account
3. Tạo API key mới
4. Copy API key

## ⚙️ Cấu hình

### Bước 1: Tạo file `.env`
Tạo file `.env` trong thư mục root của project:

```env
# OpenAI Configuration (Primary)
OPENAI_API_KEY=sk-your-openai-api-key-here
OPENAI_MODEL=gpt-3.5-turbo
OPENAI_TEMPERATURE=0.7
OPENAI_MAX_TOKENS=1000

# Gemini Configuration (Backup - Optional)
GEMINI_API_KEY=your-gemini-api-key-here

# Server Configuration
SERVER_PORT=8080
```

### Bước 2: Cài đặt Dependencies
```bash
# Trong thư mục project
mvn clean install
```

### Bước 3: Chạy Application
```bash
mvn spring-boot:run
```

## 🎯 Kiến trúc

### Services
1. **OpenAIService** (Primary)
   - Sử dụng Spring AI OpenAI Spring Boot Starter
   - Tự động configure qua `application.properties`
   - Ưu tiên được sử dụng nếu có API key

2. **GeminiRestService** (Backup)
   - Sử dụng REST API trực tiếp
   - Fallback nếu OpenAI không available
   - Không cần Spring AI module

### Priority Flow
```
User Request
    ↓
ChatService
    ↓
├─→ OpenAIService (if API key exists) → Success ✅
│
└─→ GeminiRestService (fallback) → Success ✅
    │
    └─→ Error ❌ "No AI service available"
```

## 📊 API Models

### OpenAI Models
- `gpt-3.5-turbo` (default, nhanh, rẻ)
- `gpt-4` (thông minh hơn, chậm hơn, đắt hơn)
- `gpt-4-turbo` (cân bằng)

### Gemini Models (Backup)
- `gemini-2.5-flash` (nhanh, miễn phí)
- `gemini-1.5-pro` (thông minh hơn)

## 🔧 Troubleshooting

### Lỗi: "No AI service available"
- Kiểm tra file `.env` có đúng không
- Kiểm tra `OPENAI_API_KEY` hoặc `GEMINI_API_KEY` đã được set
- Restart application sau khi thay đổi `.env`

### Lỗi: "401 Unauthorized"
- API key không hợp lệ
- API key đã hết hạn
- Kiểm tra lại tại https://platform.openai.com/api-keys

### Lỗi: "429 Rate Limit"
- Đã vượt quá giới hạn request
- Chờ một chút rồi thử lại
- Nâng cấp plan OpenAI nếu cần

## 💰 Chi phí

### OpenAI Pricing (GPT-3.5-Turbo)
- Input: $0.50 / 1M tokens (~$0.0005/request)
- Output: $1.50 / 1M tokens (~$0.0015/request)
- Credit miễn phí: $5 cho tài khoản mới

### Google Gemini Pricing
- Gemini Flash: Miễn phí (có rate limit)
- Gemini Pro: $0.50 / 1M tokens

## 📚 Tài liệu tham khảo
- Spring AI: https://docs.spring.io/spring-ai/reference/
- OpenAI API: https://platform.openai.com/docs
- Gemini API: https://ai.google.dev/docs
