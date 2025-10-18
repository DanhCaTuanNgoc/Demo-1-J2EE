# Hướng dẫn sử dụng biến môi trường với file .env

## Cách tạo file .env

1. Tạo file `.env` trong thư mục gốc của project (cùng cấp với pom.xml)
2. Thêm các biến môi trường sau vào file `.env`:

```env
# ===============================
# Environment Variables
# ===============================
# Gemini API Configuration
GEMINI_API_KEY=your_actual_gemini_api_key_here
GEMINI_API_URL=https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent

# Application Configuration
SPRING_APPLICATION_NAME=demo
SPRING_PROFILES_ACTIVE=dev

# Server Configuration
SERVER_PORT=8080

# Logging Configuration
LOGGING_LEVEL_COM_EXAMPLE_DEMO=INFO
```

## Các biến môi trường được hỗ trợ

- `GEMINI_API_KEY`: API key của Gemini (bắt buộc)
- `GEMINI_API_URL`: URL endpoint của Gemini API
- `SPRING_APPLICATION_NAME`: Tên ứng dụng
- `SPRING_PROFILES_ACTIVE`: Profile Spring Boot đang hoạt động
- `SERVER_PORT`: Port của server (mặc định: 8080)
- `LOGGING_LEVEL_COM_EXAMPLE_DEMO`: Level logging cho package com.example.demo

## Lưu ý bảo mật

- **KHÔNG** commit file `.env` vào Git
- Thêm `.env` vào `.gitignore`
- Sử dụng file `.env.example` làm template cho các developer khác

## Cách hoạt động

Ứng dụng sẽ tự động load file `.env` khi khởi động và chuyển đổi các biến thành System Properties.
Spring Boot sau đó sẽ đọc các biến này từ `application.properties`.

**Quy trình hoạt động:**
1. `DemoApplication.main()` load file `.env` bằng `dotenv-java`
2. Chuyển đổi các biến từ `.env` thành System Properties
3. Spring Boot đọc các biến từ `application.properties` với cú pháp `${VARIABLE_NAME:default_value}`

**Ví dụ:** `${GEMINI_API_KEY:your_gemini_api_key_here}` có nghĩa là:
- Nếu có biến môi trường `GEMINI_API_KEY` (từ file `.env` hoặc hệ thống), sử dụng giá trị đó
- Nếu không có, sử dụng giá trị mặc định `your_gemini_api_key_here`

**Kiểm tra cấu hình:**
Khi khởi động ứng dụng, bạn sẽ thấy thông báo:
- ✅ "Đã load file .env thành công!" - nếu file `.env` tồn tại và hợp lệ
- ⚠️ "Không thể load file .env" - nếu file không tồn tại hoặc có lỗi
- 📄 "Load từ file .env: ✅ Có" - trong phần kiểm tra cấu hình

## 🔧 Khắc phục vấn đề Encoding (Windows Terminal)

Nếu bạn thấy các ký tự tiếng Việt hiển thị bị lỗi (như `?` thay vì các ký tự có dấu), đây là vấn đề phổ biến với Windows PowerShell. Hãy làm theo các bước sau:

### 🎯 **Giải pháp tốt nhất: Sử dụng Windows Terminal**

#### Bước 1: Cài đặt Windows Terminal
1. Mở **Microsoft Store**
2. Tìm kiếm "Windows Terminal"
3. Cài đặt **Windows Terminal** (miễn phí)
4. Hoặc tải từ: https://www.microsoft.com/store/productId/9N0DX20HK701

#### Bước 2: Thiết lập UTF-8
```powershell
# Chạy script thiết lập Windows Terminal
.\setup-windows-terminal.ps1
```

#### Bước 3: Khởi động ứng dụng
```powershell
# Sử dụng Windows Terminal
.\start-with-terminal.ps1

# Hoặc sử dụng batch file
.\start-with-terminal.bat
```

### 🔧 **Các cách khác:**

#### Cách 1: Sử dụng PowerShell với encoding
```powershell
# Thiết lập UTF-8 encoding cho PowerShell
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
[Console]::InputEncoding = [System.Text.Encoding]::UTF8
$OutputEncoding = [System.Text.Encoding]::UTF8

# Thiết lập environment variables
$env:JAVA_TOOL_OPTIONS = "-Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8"

# Khởi động ứng dụng
.\mvnw.cmd spring-boot:run
```

#### Cách 2: Sử dụng Command Prompt với UTF-8
```cmd
REM Thiết lập UTF-8 cho Command Prompt
chcp 65001

REM Thiết lập environment variables
set JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8

REM Khởi động ứng dụng
mvnw.cmd spring-boot:run
```

#### Cách 3: Sử dụng VS Code Terminal
1. Mở project trong **VS Code**
2. Mở Terminal trong VS Code (`Ctrl + ``)
3. VS Code Terminal hỗ trợ UTF-8 tốt hơn
4. Chạy: `.\mvnw.cmd spring-boot:run`

### 🔍 **Kiểm tra encoding hiện tại**
```powershell
# Kiểm tra encoding của console
[Console]::OutputEncoding.EncodingName
[Console]::InputEncoding.EncodingName
$OutputEncoding.EncodingName

# Kiểm tra code page
chcp
```

### ⚠️ **Lưu ý quan trọng:**
- **Windows PowerShell** mặc định không hỗ trợ UTF-8 tốt
- **Windows Terminal** là giải pháp tốt nhất cho UTF-8
- **VS Code Terminal** cũng hỗ trợ UTF-8 tốt
- **Command Prompt** với `chcp 65001` có thể hoạt động
