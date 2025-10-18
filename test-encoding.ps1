# Test script để kiểm tra UTF-8 encoding
Write-Host "Test UTF-8 encoding..." -ForegroundColor Green

# Thiết lập UTF-8 encoding
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$OutputEncoding = [System.Text.Encoding]::UTF8

# Test hiển thị ký tự tiếng Việt
Write-Host "Test ký tự tiếng Việt:" -ForegroundColor Yellow
Write-Host "Xin chào! Tôi là AI Assistant." -ForegroundColor White
Write-Host "Các ký tự đặc biệt: á e i o u" -ForegroundColor Cyan

Write-Host "`nTest emoji:" -ForegroundColor Yellow
Write-Host "Rocket Star Computer Party Check Cross Warning Light Tool Package Globe Antenna Target" -ForegroundColor Magenta

Write-Host "`nNếu bạn thấy các ký tự trên hiển thị đúng, UTF-8 encoding đã được thiết lập thành công!" -ForegroundColor Green
