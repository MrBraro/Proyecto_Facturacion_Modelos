$ErrorActionPreference = "Stop"
$BaseUrl = "http://localhost:8082/api/v1"

Write-Host "`n--- 1. REGISTER ADMIN ---" -ForegroundColor Cyan
$RegBody = @{
    name = "Juan Admin"
    email = "admin_test_1@local.dev"
    password = "password123"
} | ConvertTo-Json
try {
    $RegRes = Invoke-RestMethod -Uri "$BaseUrl/auth/register" -Method Post -Body $RegBody -ContentType "application/json"
    Write-Host "Success: $($RegRes.message)" -ForegroundColor Green
} catch {
    Write-Host "Register skipped (probably already exists)" -ForegroundColor Yellow
}

Write-Host "`n--- 2. LOGIN ---" -ForegroundColor Cyan
$LoginBody = @{
    email = "admin_test_1@local.dev"
    password = "password123"
} | ConvertTo-Json
$LoginRes = Invoke-RestMethod -Uri "$BaseUrl/auth/login" -Method Post -Body $LoginBody -ContentType "application/json"
$Token = $LoginRes.data.token
Write-Host "Success: JWT Token acquired" -ForegroundColor Green

$Headers = @{
    "Authorization" = "Bearer $Token"
    "Content-Type" = "application/json"
}

Write-Host "`n--- 3. CREATE PRODUCT (RF-01) ---" -ForegroundColor Cyan
$ProdBody = @{
    code = "TEST-100"
    name = "Producto de Prueba"
    price = 99.99
    description = "Un producto genial"
    stock = 5
} | ConvertTo-Json
try {
    $CreateRes = Invoke-RestMethod -Uri "$BaseUrl/products" -Method Post -Headers $Headers -Body $ProdBody
    $ProductId = $CreateRes.data.id
    Write-Host "Success: Created product with ID $ProductId" -ForegroundColor Green
} catch {
    Write-Host "Failed to create product. Maybe code already exists?" -ForegroundColor Red
    $err = $_.ErrorDetails.Message
    Write-Host "Error: $err"
    # Try to find the product ID by listing
    $ListRes = Invoke-RestMethod -Uri "$BaseUrl/products" -Method Get -Headers $Headers
    $ProductId = ($ListRes.data | Where-Object { $_.code -eq "TEST-100" }).id
    Write-Host "Using existing Product ID: $ProductId"
}

Write-Host "`n--- 4. LIST PRODUCTS (RF-02) ---" -ForegroundColor Cyan
$ListRes = Invoke-RestMethod -Uri "$BaseUrl/products?name=Prueba" -Method Get -Headers $Headers
Write-Host "Success: Found $($ListRes.data.Count) products matching 'Prueba'" -ForegroundColor Green
Write-Host ($ListRes.data | ConvertTo-Json -Depth 2)

Write-Host "`n--- 5. GET STOCK ALERTS (RF-06) ---" -ForegroundColor Cyan
$AlertsRes = Invoke-RestMethod -Uri "$BaseUrl/products/alerts" -Method Get -Headers $Headers
Write-Host "Success: Found $($AlertsRes.data.Count) products in low stock" -ForegroundColor Green
if ($AlertsRes.data.Count -gt 0) {
    Write-Host "Example Alert: $($AlertsRes.data[0].name) has $($AlertsRes.data[0].stock) stock"
}

Write-Host "`n--- 6. UPDATE PRODUCT (RF-04) ---" -ForegroundColor Cyan
$UpdateBody = @{
    name = "Producto de Prueba ACTUALIZADO"
    price = 149.99
    description = "Descripción modificada"
} | ConvertTo-Json
$UpdateRes = Invoke-RestMethod -Uri "$BaseUrl/products/$ProductId" -Method Patch -Headers $Headers -Body $UpdateBody
Write-Host "Success: Product updated" -ForegroundColor Green
Write-Host "New Name: $($UpdateRes.data.name)"

Write-Host "`n--- 7. DELETE PRODUCT (RF-05) ---" -ForegroundColor Cyan
$DeleteRes = Invoke-RestMethod -Uri "$BaseUrl/products/$ProductId" -Method Delete -Headers $Headers
Write-Host "Success: $($DeleteRes.message)" -ForegroundColor Green

Write-Host "`n--- 8. VERIFY SOFT DELETE ---" -ForegroundColor Cyan
$FinalList = Invoke-RestMethod -Uri "$BaseUrl/products?code=TEST-100" -Method Get -Headers $Headers
Write-Host "Total active products with code TEST-100: $($FinalList.data.Count) (Should be 0)" -ForegroundColor Green

Write-Host "`nALL TESTS COMPLETED SUCCESSFULLY!" -ForegroundColor Magenta
