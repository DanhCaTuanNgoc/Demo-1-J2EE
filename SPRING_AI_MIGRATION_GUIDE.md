# Hướng Dẫn Migration Sang Spring AI

## 📋 Tổng Quan

Dự án hiện đang sử dụng **REST API thuần** để tích hợp với Gemini. Tôi đã thử migrate sang **Spring AI** nhưng gặp vấn đề sau:

### ⚠️ Vấn Đề Hiện Tại

**Module `spring-ai-google-genai` chưa được release trong Spring AI 1.0.3**

- Artifact `spring-ai-google-genai` không tồn tại trong Maven Central
- Module này là tính năng mới, chỉ có trong các phiên bản development/snapshot
- Spring AI 1.0.3 (stable release) chưa bao gồm module này

## 🔍 Các Module Spring AI Có Sẵn (1.0.3)

### 1️⃣ **Vertex AI Gemini** (spring-ai-vertex-ai-gemini) - ❌ TỐN PHÍ
```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-vertex-ai-gemini</artifactId>
</dependency>
```
- ❌ Sử dụng Google Cloud Platform Vertex AI
- ❌ Cần Google Cloud Project + Service Account  
- ❌ Tính phí theo usage (input/output tokens)
- ✅ Production-ready, enterprise grade

### 2️⃣ **Google GenAI** (spring-ai-google-genai) - ✅ MIỄN PHÍ nhưng ❌ CHƯA RELEASE
```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-google-genai</artifactId>
    <version>1.0.3</version>  <!-- ❌ Không tồn tại -->
</dependency>
```
- ✅ Sử dụng Google AI Studio API (miễn phí)
- ✅ Chỉ cần API key
- ❌ Artifact chưa được release trong version stable
- ⏳ Có thể chỉ có trong SNAPSHOT hoặc version mới hơn

## 💡 Khuyến Nghị & Giải Pháp

### ✅ **PHƯƠNG ÁN 1: GIỮ NGUYÊN REST API** (KHUYẾN NGHỊ)

**Lý do:**
- ✅ **Đã hoạt động ổn định**
- ✅ **Hoàn toàn miễn phí** (dùng Google AI Studio API key)
- ✅ **Đầy đủ tính năng**: memory, function calling, multimodal
- ✅ **Đơn giản, dễ bảo trì**
- ✅ **Không phụ thuộc vào Spring AI release cycle**

**Code hiện tại:**
```java
// GeminiRestService.java - Đã hoạt động tốt
@Service
public class GeminiRestService {
    @Value("${gemini.api.key}")
    private String apiKey;
    
    public String generateText(String userMessage) {
        // REST API call to generativelanguage.googleapis.com
    }
}
```

### 🔄 **PHƯƠNG ÁN 2: CHỜ Spring AI Release Mới**

Nếu muốn dùng Spring AI trong tương lai:

1. **Đợi Spring AI 1.1.x hoặc 2.0.x**
   - Module `spring-ai-google-genai` sẽ được release chính thức
   - Hoặc sử dụng SNAPSHOT version (không khuyến nghị cho production)

2. **Cấu hình khi có:**
```yaml
# application.properties
spring.ai.google.genai.api-key=${GEMINI_API_KEY}
spring.ai.google.genai.chat.options.model=gemini-2.0-flash
spring.ai.google.genai.chat.options.temperature=0.7
```

```java
@Service
public class GeminiAIService {
    @Autowired
    private ChatModel chatModel;  // Auto-configured by Spring AI
    
    public String generateText(String userMessage) {
        ChatResponse response = chatModel.call(new Prompt(userMessage));
        return response.getResult().getOutput().getContent();
    }
}
```

### 💰 **PHƯƠNG ÁN 3: Dùng Vertex AI (Trả Phí)**

Nếu có budget và cần production-grade:

```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-vertex-ai-gemini</artifactId>
</dependency>
```

```yaml
spring.ai.vertex.ai.gemini.project-id=YOUR_PROJECT_ID
spring.ai.vertex.ai.gemini.location=us-central1
spring.ai.vertex.ai.gemini.chat.options.model=gemini-2.0-flash
```

**Chi phí ước tính:**
- Input: ~$0.000125 / 1K characters
- Output: ~$0.000375 / 1K characters

## 🎯 Kết Luận & Hành Động

### ✅ **KHUYẾN NGHỊ CUỐI CÙNG: GIỮ NGUYÊN REST API**

**Lý do:**
1. ✅ Đang hoạt động tốt, không cần sửa
2. ✅ Miễn phí hoàn toàn
3. ✅ Ít phụ thuộc dependency
4. ✅ Hiệu suất tốt (trực tiếp REST call)
5. ✅ Dễ debug và maintain

**Khi nào nên migrate sang Spring AI:**
- ⏰ Khi Spring AI 1.1+ release với module `spring-ai-google-genai`
- 💼 Khi cần features đặc biệt của Spring AI (observability, retry, etc.)
- 🏢 Khi scale lên production với Vertex AI (có budget)

## 📚 Tài Liệu Tham Khảo

- [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/)
- [Spring AI GitHub](https://github.com/spring-projects/spring-ai)
- [Google AI Studio](https://aistudio.google.com/app/apikey) - Lấy API key miễn phí
- [Vertex AI Pricing](https://cloud.google.com/vertex-ai/pricing)

---

## 🔧 Trạng Thái Code Hiện Tại

Dự án đã được chuẩn bị để dễ dàng migrate sang Spring AI khi có phiên bản stable:

✅ `pom.xml` - Đã có Spring AI BOM và repositories  
✅ `GeminiAIService.java` - Service tương thích với Spring AI ChatModel  
✅ `ChatService.java` - Hỗ trợ cả REST API và Spring AI (fallback)  
✅ `application.properties` - Cấu hình sẵn cho cả 2 phương án

**Bạn chỉ cần:**
1. Đợi Spring AI release module `spring-ai-google-genai`
2. Uncomment dependency trong `pom.xml`
3. Restart application → Spring AI sẽ tự động hoạt động!
