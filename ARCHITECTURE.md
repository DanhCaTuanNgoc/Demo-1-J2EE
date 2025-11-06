# ARCHITECTURE — Demo-1-J2EE

Tài liệu này mô tả tổng quan kiến trúc dự án, workflow của memory và function-calling khi dùng Spring AI, nhiệm vụ chính của các file trong repo, và trách nhiệm các lớp/framework (như `ChatClient`, `ChatModel`, `ChatMemory`, `MessageChatMemoryAdvisor`, v.v.).

## Mục tiêu
- Mô tả rõ cách Spring AI được tích hợp để xử lý: memory (lưu/thu hồi context) và function calling (model có thể gọi function trên server).
- Liệt kê nhiệm vụ từng file/cụm class trong dự án để dễ bảo trì và mở rộng.
- Cung cấp sequence workflow (text) cho hai luồng chính: normal response và function-calling flow.

---

## Tóm tắt kiến trúc (high-level)

- Client (front-end / `index.html`) gửi request tới `ChatController`.
- `ChatController` gọi `ChatService.chat(userMessage)`.
- `ChatService` dùng `OpenAIService` (nếu được cấu hình) để xử lý bằng Spring AI. Nếu `OpenAIService` không bật thì fallback sang `GeminiRestService`.
- `OpenAIService` sử dụng một `ChatClient` được build từ `ChatModel` (Spring AI auto-config hoặc bean do app cung cấp). `ChatClient` chịu trách nhiệm gọi provider (OpenAI/OpenRouter) sử dụng cấu hình trong `ChatModel` (api-key, endpoint, model id).
- `ChatMemory` (InMemory) lưu message theo conversationId; `MessageChatMemoryAdvisor` được thêm vào `ChatClient` để advisor tự động chèn/khôi phục memory khi gọi API.
- Function definitions (beans trong `FunctionConfig`) được đăng ký với Spring context; `ChatClient` có thể nhận danh sách tên function (strings) để model biết function nào có thể được gọi. Khi model trả về function call, Spring AI sẽ map tên function sang bean và gọi.

---

## File & responsibility map (the repo)

- `src/main/java/com/example/demo/config/ChatMemoryConfig.java`
  - Tạo `ChatMemory` bean (In-Memory). Chịu trách nhiệm cung cấp storage cho cuộc hội thoại (conversationId -> list messages).

- `src/main/java/com/example/demo/config/FunctionConfig.java`
  - Định nghĩa function-beans có thể được gọi bởi model (ví dụ `getCurrentTime`, `calculator`).
  - Mỗi function trả về kiểu dữ liệu/record chuẩn để Spring AI map và serialize kết quả.

- `src/main/java/com/example/demo/service/OpenAIService.java`
  - Build `ChatClient` từ `ChatModel` (injected). Thêm `MessageChatMemoryAdvisor` để dùng memory.
  - Expose `generateText(userMessage, conversationId, functionNames...)` — đăng ký functions, set advisor params (conversationId, retrieve size) và gọi `.call().chatResponse()`.
  - `clearMemory(conversationId)` gọi `chatMemory.clear`.
  - Kiểm tra `enabled` dựa vào property `spring.ai.openai.api-key`.

- `src/main/java/com/example/demo/service/ChatService.java`
  - Orchestrator phía ứng dụng: quản lý một local `conversationHistory` (fallback/history local), quyết định dùng `OpenAIService` hay `GeminiRestService`.
  - Đăng ký function names khi gọi `openAIService.generateText(...)`.

- `src/main/java/com/example/demo/service/TimeService.java`
  - Implementation của logic trả về thời gian hiện tại. Nên được gọi bởi function-bean `getCurrentTime` hoặc function-bean nên delegate tới service này để tránh duplication.

- `src/main/java/com/example/demo/service/GeminiRestService.java`
  - Fallback non-Spring-AI flow: tạo prompt từ `conversationHistory`, gọi Gemini REST API theo cũ.

- `src/main/java/com/example/demo/controller/ChatController.java`
  - Expose REST endpoint để client gửi tin nhắn.

- `src/main/resources/application.properties`
  - Nơi cấu hình `spring.ai.openai.api-key`, `spring.ai.openai.model` hoặc cấu hình OpenRouter. `OpenAIService` dùng property để bật/tắt.

- `pom.xml`
  - Kiểm tra dependency của Spring AI để đảm bảo API tương thích với code (nhiều method tên/return shape thay đổi giữa versions).

---

## Các lớp / component Spring AI và trách nhiệm (conceptual)

- ChatModel
  - Bean mô tả provider/model thông tin: endpoint, api-key, model id, provider-specific config.
  - Thường được Spring AI auto-config tạo ra dựa trên `application.properties` (`spring.ai.openai.*`).

- ChatClient
  - Client cao cấp để thao tác chat: builder(chatModel) -> config system prompt, advisors, default behavior.
  - Khi gọi `.call()` sẽ thực hiện HTTP request tới provider (OpenAI/OpenRouter) sử dụng thông tin từ `ChatModel`.
  - Kết quả can be polled via `.chatResponse()` (full ChatResponse) hoặc `.singleText()` tùy API.

- ChatMemory
  - Abstraction cho lưu/khôi phục messages cho conversation. Methods: `append(conversationId, entry)`, `retrieve(conversationId)`, `clear(conversationId)`.
  - `MessageChatMemoryAdvisor` sử dụng `ChatMemory` để lúc prompt generation chèn remembered messages và/hoặc cập nhật memory khi nhận response.

- MessageChatMemoryAdvisor
  - Advisor được gắn vào ChatClient để tự động thực hiện memory retrieval trước call và memory update sau call.
  - Thường sử dụng advisor params: `CHAT_MEMORY_CONVERSATION_ID_KEY` và `CHAT_MEMORY_RETRIEVE_SIZE_KEY`.

- Function-beans (app-defined)
  - Đăng ký functions như Spring beans (ví dụ methods ở `FunctionConfig`). Khi model trả về function call, Spring AI mapping tên function -> bean và gọi bean, passing parsed argument(s).
  - Bean trả kết quả (có thể JSON string) mà Spring AI sẽ feed lại vào model (model có thể produce final response using function output).

---

## Workflow: Normal response (no function call)

1. Client -> `ChatController` -> `ChatService.chat(userMessage)`.
2. `ChatService` xác định conversationId (ví dụ `default-session`).
3. `OpenAIService.generateText(userMessage, conversationId, functionNames...)` được gọi.
4. `OpenAIService` build một `prompt()` call:
   - `user(userMessage)`
   - `.advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, conversationId).param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))` -> `MessageChatMemoryAdvisor` sẽ retrieve stored messages (nếu có) và chèn vào prompt.
   - `.functions(functionNames)` -> model biết function nào có thể gọi.
5. `chatClient.call().chatResponse()` gửi request tới provider.
6. Provider trả về text response (không gọi function).
7. `MessageChatMemoryAdvisor` (hoặc code bên client) lưu message/user/assistant vào `ChatMemory`.
8. `OpenAIService` trả `aiResponse` cho `ChatService` -> controller -> client.

---

## Workflow: Function-calling (model requests to call a function)

1. Steps 1-4 giống trên.
2. Provider trả về một message có `function_call` (tên function + arguments JSON).
3. Spring AI runtime maps function name -> Spring bean (registered in `FunctionConfig`).
4. Spring AI invokes the bean method (mapping args -> parameters). The bean executes server-side logic. Example: `getCurrentTime` calls `TimeService.getCurrentTime()`.
5. The result of the function call is captured and then re-submitted to the model as a new assistant message (function result). The model may then produce a final assistant message using that result.
6. Final assistant message returned in `chatResponse().getResult()`; memory advisor records full conversation including function call and function result.
7. `OpenAIService` returns final output text to `ChatService`.

ASCII sequence (simplified):

Client -> ChatController -> ChatService -> OpenAIService -> ChatClient -> Provider
                                 <- Provider (function_call)
                                -> Spring AI invokes function bean
                                <- Function result
                                 -> Provider (re-run to get final assistant message)
                                 <- Final assistant message -> OpenAIService -> ChatService -> Client

---

## Contract / small spec (inputs/outputs)

- OpenAIService.generateText(userMessage: String, conversationId: String, functionNames: String...) -> String
  - Input: user message text, conversation id, optional list of function names (must match bean method-names)
  - Output: final assistant text
  - Error modes: If OpenAIService not enabled -> IllegalStateException; network/parse errors -> RuntimeException

- Function bean contract
  - Name must match the functionName passed to `.functions(...)` registration. Use explicit bean name if necessary (e.g., `@Bean("getCurrentTime")`).
  - Accept JSON-mapped args or empty args; return serializable object (String/JSON).

Edge cases to handle
- Name mismatch: model asks to call function `foo` but no bean `foo` exists -> must log and return graceful message.
- Response content-shape: `response.getResult().getOutput().getContent()` may be structured (blocks/list) depending on Spring AI version; always null-check and support alternative extraction.
- Large memory: retrieved messages may exceed token limit. Use `CHAT_MEMORY_RETRIEVE_SIZE_KEY` and trim by recency.
- Concurrent conversationId usage: ensure ChatMemory implementation is thread-safe, or prefer per-user session conversationId.

---

## Recommendations / Best practices for this project

1. Single source for function logic: Move `TimeService` logic into a single bean and let `FunctionConfig` delegate to it. Tránh duplicate logic in function bean.
2. Explicit function bean names: Use `@Bean("getCurrentTime")` to ensure mapping by name is stable.
3. Robust response parsing: Wrap `response.getResult()` extraction with helper that inspects output type (String vs List vs Block) and logs raw response on debug.
4. Tests: Add an integration test that stubs ChatModel/provider (or use a test double) to simulate a function_call response and assert server executes function bean and returns correct assistant message. Also add unit tests for ChatMemory behaviors.
5. Config validation at startup: Fail-fast or warn clearly when `spring.ai.openai.api-key` missing but `OpenAIService` expected to be used. Consider health-check endpoint.

---

## Quick verification checklist (how to validate function-calling + memory end-to-end)

1. Ensure `application.properties` contains:

```properties
spring.ai.openai.api-key=YOUR_KEY
spring.ai.openai.model=<model-id>
```

2. Start the app and watch logs (OpenAIService prints enabled/disabled and system prompt).
3. Send a message that should trigger the function (e.g., "Bây giờ là mấy giờ?") via `ChatController` endpoint.
4. Observe logs:
   - `MessageChatMemoryAdvisor` retrieved previous messages (if any)
   - Provider returned `function_call` and the app invoked corresponding bean
   - Final assistant message returned, and memory saved

Example minimal test (manual) using curl to local server (adjust port if needed):

```bash
curl -s -X POST "http://localhost:8080/chat" -H "Content-Type: application/json" -d '{"message":"Bây giờ là mấy giờ?"}'
```

---

## Next steps (low-risk improvements)

- Refactor `FunctionConfig` to delegate to `TimeService` and other services for implementation logic.
- Add a small integration test simulating function_call behavior (use mocking of ChatClient/ChatModel or Spring AI test helpers if available).
- Add runtime checks that function bean names passed to `generateText` are present in the context and log a warning otherwise.

---

## Notes / References
- `OpenAIService` builds `ChatClient` from injected `ChatModel` — `ChatModel` is the place Spring AI auto-config reads API key and endpoint from `application.properties`.
- Keep an eye on Spring AI version compatibility: method names like `.chatResponse().getResult().getOutput().getContent()` may differ by version; add robust parsing.

---

If muốn, tôi có thể:
- Tạo một integration test skeleton để simulate function-calling.
- Refactor `FunctionConfig` to use `TimeService` and update bean names explicitly.
- Tạo mẫu `application.properties.example` với các keys cần thiết.

