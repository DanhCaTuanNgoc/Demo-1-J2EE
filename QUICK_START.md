# Hướng dẫn chạy nhanh

## Bước 1: Cấu hình Gemini AI

1. Tạo tài khoản Google Cloud và project
2. Kích hoạt Vertex AI API
3. Tạo service account và download credentials JSON
4. Set environment variables:

```bash
export GEMINI_PROJECT_ID=your-project-id
export GEMINI_LOCATION=us-central1
export GEMINI_CREDENTIALS_PATH=/path/to/credentials.json
```

## Bước 2: Chạy ứng dụng

```bash
# Chạy với Maven
./mvnw spring-boot:run

# Hoặc build và chạy
./mvnw clean package
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

## Bước 3: Test chatbot

1. Mở trình duyệt: `http://localhost:8080`
2. Thử các câu hỏi:
   - "Bây giờ là mấy giờ?"
   - "Xin chào, bạn có khỏe không?"
   - "Bạn nhớ tôi đã hỏi gì không?"

## API Test

```bash
# Test API trực tiếp
curl -X POST http://localhost:8080/api/chat/message \
  -H "Content-Type: application/json" \
  -d '{"message": "Bây giờ là mấy giờ?"}'

# Xóa memory
curl -X DELETE http://localhost:8080/api/chat/memory

# Xem lịch sử
curl http://localhost:8080/api/chat/history
```
