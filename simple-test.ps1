# Simple test script
Write-Host "🧪 SIMPLE TEST - Memory & Function Calling" -ForegroundColor Green

# Đợi ứng dụng khởi động
Write-Host "⏳ Đợi ứng dụng khởi động..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

$baseUrl = "http://localhost:8080/api/chat/message"

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

Write-Host "`n🎯 TEST: Memory" -ForegroundColor Cyan
$response1 = Send-Message "Tôi tên Tuấn"
Write-Host "User: Tôi tên Tuấn" -ForegroundColor White
Write-Host "Bot: $response1" -ForegroundColor Green

$response2 = Send-Message "Bạn biết tôi tên gì không?"
Write-Host "User: Bạn biết tôi tên gì không?" -ForegroundColor White
Write-Host "Bot: $response2" -ForegroundColor Green

Write-Host "`n🎯 TEST: Function Calling" -ForegroundColor Cyan
$response3 = Send-Message "Tính 2 + 3"
Write-Host "User: Tính 2 + 3" -ForegroundColor White
Write-Host "Bot: $response3" -ForegroundColor Green

Write-Host "`n✅ TEST HOÀN THÀNH!" -ForegroundColor Green
