# Script test demo cho Memory và Function Calling
Write-Host "🧪 DEMO SCRIPT - Memory & Function Calling" -ForegroundColor Green
Write-Host "===============================================" -ForegroundColor Green

# Đợi ứng dụng khởi động
Write-Host "⏳ Đợi ứng dụng khởi động..." -ForegroundColor Yellow
Start-Sleep -Seconds 15

$baseUrl = "http://localhost:8080/api/chat/message"

# Function để gửi tin nhắn
function Send-Message {
    param($message)
    
    $body = @{
        message = $message
    } | ConvertTo-Json
    
    try {
        $response = Invoke-RestMethod -Uri $baseUrl -Method Post -Body $body -ContentType "application/json"
        return $response.response
    } catch {
        Write-Host "❌ Lỗi: $($_.Exception.Message)" -ForegroundColor Red
        return $null
    }
}

Write-Host "`n🎯 TEST 1: Memory - Chatbot nhớ tên" -ForegroundColor Cyan
Write-Host "----------------------------------------" -ForegroundColor Cyan

$response1 = Send-Message "Chào bạn!"
Write-Host "User: Chào bạn!" -ForegroundColor White
Write-Host "Bot: $response1" -ForegroundColor Green

$response2 = Send-Message "Tôi tên Tuấn"
Write-Host "User: Tôi tên Tuấn" -ForegroundColor White
Write-Host "Bot: $response2" -ForegroundColor Green

$response3 = Send-Message "Bạn biết tôi tên gì không?"
Write-Host "User: Bạn biết tôi tên gì không?" -ForegroundColor White
Write-Host "Bot: $response3" -ForegroundColor Green

Write-Host "`n🎯 TEST 2: Function Calling - Tính toán" -ForegroundColor Cyan
Write-Host "----------------------------------------" -ForegroundColor Cyan

$response4 = Send-Message "Hãy tính tổng 2 + 3"
Write-Host "User: Hãy tính tổng 2 + 3" -ForegroundColor White
Write-Host "Bot: $response4" -ForegroundColor Green

$response5 = Send-Message "Tính 10 * 5"
Write-Host "User: Tính 10 * 5" -ForegroundColor White
Write-Host "Bot: $response5" -ForegroundColor Green

Write-Host "`n🎯 TEST 3: Function Calling - Thời gian" -ForegroundColor Cyan
Write-Host "----------------------------------------" -ForegroundColor Cyan

$response6 = Send-Message "Bây giờ mấy giờ?"
Write-Host "User: Bây giờ mấy giờ?" -ForegroundColor White
Write-Host "Bot: $response6" -ForegroundColor Green

Write-Host "`n🎯 TEST 4: Context Awareness" -ForegroundColor Cyan
Write-Host "----------------------------------------" -ForegroundColor Cyan

$response7 = Send-Message "Nhắc tôi về tên của tôi"
Write-Host "User: Nhắc tôi về tên của tôi" -ForegroundColor White
Write-Host "Bot: $response7" -ForegroundColor Green

Write-Host "`n✅ DEMO HOÀN THÀNH!" -ForegroundColor Green
Write-Host "===============================================" -ForegroundColor Green
