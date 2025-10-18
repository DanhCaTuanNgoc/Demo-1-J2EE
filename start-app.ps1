# Script khởi động ứng dụng với UTF-8 encoding
Write-Host "Khởi động AI Assistant với UTF-8 encoding..." -ForegroundColor Green

# Thiết lập UTF-8 encoding cho PowerShell
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
[Console]::InputEncoding = [System.Text.Encoding]::UTF8
$OutputEncoding = [System.Text.Encoding]::UTF8

# Thiết lập environment variables cho Java
$env:JAVA_TOOL_OPTIONS = "-Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8 -Duser.timezone=Asia/Ho_Chi_Minh"

Write-Host "Đã thiết lập UTF-8 encoding!" -ForegroundColor Green
Write-Host "Khởi động ứng dụng Spring Boot..." -ForegroundColor Yellow

# Khởi động ứng dụng Spring Boot
if (Test-Path "mvnw.cmd") {
    Write-Host "Sử dụng Maven Wrapper..." -ForegroundColor Cyan
    & .\mvnw.cmd spring-boot:run
} elseif (Test-Path "mvnw") {
    Write-Host "Sử dụng Maven Wrapper (Unix)..." -ForegroundColor Cyan
    & .\mvnw spring-boot:run
} else {
    Write-Host "Sử dụng Maven system..." -ForegroundColor Cyan
    mvn spring-boot:run
}

Write-Host "`nỨng dụng đã được khởi động!" -ForegroundColor Green
Write-Host "Truy cập: http://localhost:8080" -ForegroundColor Cyan
Write-Host "API: http://localhost:8080/api/chat/message" -ForegroundColor Cyan
