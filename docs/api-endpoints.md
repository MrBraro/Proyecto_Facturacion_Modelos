# API Endpoints — ProyectoFacturacion (Spring)

This document lists the actual REST endpoints implemented in the backend, grouped by controller. For DTOs and controller source, follow the links to the code.

**Common:** responses are wrapped in `ApiResponse` ([src/main/java/com/modelosgr86e1eq6/proyectofacturacion/common/dto/ApiResponse.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/common/dto/ApiResponse.java)).

---

**Authentication & Audit** — [src/main/java/com/modelosgr86e1eq6/proyectofacturacion/auth/controllers/AuthController.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/auth/controllers/AuthController.java)

- POST /api/v1/auth/register
  - Description: Register a new administrator
  - Request: `RegisterRequest` ([src/main/java/com/modelosgr86e1eq6/proyectofacturacion/auth/dto/RegisterRequest.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/auth/dto/RegisterRequest.java))
  - Response: 201, ApiResponse<Void>
  - Auth: none

- POST /api/v1/auth/login
  - Description: Authenticate and receive JWT
  - Request: `LoginRequest` ([src/main/java/com/modelosgr86e1eq6/proyectofacturacion/auth/dto/LoginRequest.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/auth/dto/LoginRequest.java))
  - Response: 200, `LoginResponse` ([src/main/java/com/modelosgr86e1eq6/proyectofacturacion/auth/dto/LoginResponse.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/auth/dto/LoginResponse.java))
  - Auth: none

- POST /api/v1/auth/logout
  - Description: Invalidate the provided Bearer JWT
  - Request: `Authorization: Bearer <token>` header
  - Response: 200, ApiResponse<Void>
  - Auth: Bearer token required

- POST /api/v1/auth/forgot-password
  - Description: Send password-reset instructions (always returns same message)
  - Request: `ForgotPasswordRequest` ([src/main/java/com/modelosgr86e1eq6/proyectofacturacion/auth/dto/ForgotPasswordRequest.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/auth/dto/ForgotPasswordRequest.java))
  - Response: 200, ApiResponse<Void>

- POST /api/v1/auth/reset-password
  - Description: Reset password using token/code
  - Request: `ResetPasswordRequest` ([src/main/java/com/modelosgr86e1eq6/proyectofacturacion/auth/dto/ResetPasswordRequest.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/auth/dto/ResetPasswordRequest.java))
  - Response: 200, ApiResponse<Void>

- PUT /api/v1/auth/change-password
  - Description: Change password for authenticated user
  - Request: `ChangePasswordRequest` ([src/main/java/com/modelosgr86e1eq6/proyectofacturacion/auth/dto/ChangePasswordRequest.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/auth/dto/ChangePasswordRequest.java))
  - Headers: `Authorization: Bearer <token>` required
  - Response: 200, ApiResponse<Void>

- GET /api/v1/auth/me
  - Description: Get the current authenticated user summary
  - Response: 200, `MeResponse` ([src/main/java/com/modelosgr86e1eq6/proyectofacturacion/auth/dto/MeResponse.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/auth/dto/MeResponse.java))
  - Auth: authenticated (JWT)

- GET /api/v1/audit
  - Description: Query audit records (paginated)
  - Query params: `userId`, `action`, `from` (ISO date-time), `to` (ISO date-time), pageable params
  - Response: 200, `Page<AuditDTO>` ([src/main/java/com/modelosgr86e1eq6/proyectofacturacion/audits/dto/AuditDTO.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/audits/dto/AuditDTO.java))
  - Auth: ADMIN only

---

**Users** — [src/main/java/com/modelosgr86e1eq6/proyectofacturacion/users/controllers/UserController.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/users/controllers/UserController.java)

Note: All endpoints in this controller require role `ADMIN` (class-level `@PreAuthorize`).

- GET /api/v1/users
  - Description: List users (paginated)
  - Query params: `role` (optional), pageable params
  - Response: 200, `Page/UserSummaryResponse` ([src/main/java/com/modelosgr86e1eq6/proyectofacturacion/users/dto/UserSummaryResponse.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/users/dto/UserSummaryResponse.java))

- GET /api/v1/users/{id}
  - Description: Get user by id
  - Response: 200, `UserSummaryResponse`

- POST /api/v1/users
  - Description: Create an employee user
  - Request: `CreateUserRequest` ([src/main/java/com/modelosgr86e1eq6/proyectofacturacion/users/dto/CreateUserRequest.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/users/dto/CreateUserRequest.java))
  - Response: 201, `UserSummaryResponse`

- PUT /api/v1/users/{id}
  - Description: Update user
  - Request: `UpdateUserRequest` ([src/main/java/com/modelosgr86e1eq6/proyectofacturacion/users/dto/UpdateUserRequest.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/users/dto/UpdateUserRequest.java))
  - Response: 200, `UserSummaryResponse`

- PATCH /api/v1/users/{id}/deactivate
  - Description: Deactivate user
  - Response: 200, ApiResponse<Void>

- PATCH /api/v1/users/{id}/activate
  - Description: Activate user
  - Response: 200, ApiResponse<Void>

---

**Products** — [src/main/java/com/modelosgr86e1eq6/proyectofacturacion/products/controllers/ProductController.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/products/controllers/ProductController.java)

- POST /api/v1/products
  - Description: Create product
  - Request: `CreateProductRequest` ([src/main/java/com/modelosgr86e1eq6/proyectofacturacion/products/dto/CreateProductRequest.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/products/dto/CreateProductRequest.java))
  - Response: 201, `ProductResponse` ([src/main/java/com/modelosgr86e1eq6/proyectofacturacion/products/dto/ProductResponse.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/products/dto/ProductResponse.java))
  - Auth: ADMIN

- GET /api/v1/products
  - Description: List products (optional filters)
  - Query params: `name`, `code`
  - Response: 200, `List<ProductResponse>`
  - Auth: ADMIN or EMPLOYEE

- GET /api/v1/products/alerts
  - Description: Products with low stock / alerts
  - Response: 200, `List<ProductResponse>`
  - Auth: ADMIN or EMPLOYEE

- GET /api/v1/products/{id}
  - Description: Get product by id
  - Response: 200, `ProductResponse`
  - Auth: ADMIN or EMPLOYEE

- PATCH /api/v1/products/{id}
  - Description: Update product (partial)
  - Request: `UpdateProductRequest` ([src/main/java/com/modelosgr86e1eq6/proyectofacturacion/products/dto/UpdateProductRequest.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/products/dto/UpdateProductRequest.java))
  - Response: 200, `ProductResponse`
  - Auth: ADMIN

- DELETE /api/v1/products/{id}
  - Description: Deactivate product
  - Response: 200, ApiResponse<Void>
  - Auth: ADMIN

---
**Clients** — [src/main/java/com/modelosgr86e1eq6/proyectofacturacion/clients/controllers/ClientController.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/clients/controllers/ClientController.java)

- POST /api/v1/clients
  - Description: Register a new client (RF-07)
  - Request: `CreateClientRequest` ([src/main/java/com/modelosgr86e1eq6/proyectofacturacion/clients/dto/CreateClientRequest.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/clients/dto/CreateClientRequest.java))
  - Response: 201, `ApiResponse<ClientResponse>` ([src/main/java/com/modelosgr86e1eq6/proyectofacturacion/clients/dto/ClientResponse.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/clients/dto/ClientResponse.java))
  - Auth: Bearer token (ADMIN, EMPLOYEE)

- GET /api/v1/clients
  - Description: List all clients with pagination (RF-08)
  - Request: Standard pagination params (`page`, `size` — default 10, `sort` — default by `name`)
  - Response: 200, `ApiResponse<Page<ClientResponse>>` ([src/main/java/com/modelosgr86e1eq6/proyectofacturacion/clients/dto/ClientResponse.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/clients/dto/ClientResponse.java))
  - Auth: Bearer token (ADMIN, EMPLOYEE)

- GET /api/v1/clients/{idClient}
  - Description: Get client details by ID (RF-09)
  - Request: Path variable `idClient` (Integer) — Client ID
  - Response: 200, `ApiResponse<ClientResponse>` ([src/main/java/com/modelosgr86e1eq6/proyectofacturacion/clients/dto/ClientResponse.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/clients/dto/ClientResponse.java))
  - Auth: Bearer token (ADMIN, EMPLOYEE)

- PATCH /api/v1/clients/{idClient}
  - Description: Update client information partially (RF-10)
  - Request: Path variable `idClient` (Integer) — Client ID, `UpdateClientRequest` ([src/main/java/com/modelosgr86e1eq6/proyectofacturacion/clients/dto/UpdateClientRequest.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/clients/dto/UpdateClientRequest.java))
  - Response: 200, `ApiResponse<ClientResponse>` ([src/main/java/com/modelosgr86e1eq6/proyectofacturacion/clients/dto/ClientResponse.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/clients/dto/ClientResponse.java))
  - Auth: Bearer token (ADMIN, EMPLOYEE)

- DELETE /api/v1/clients/{idClient}
  - Description: Soft delete a client (RF-11)
  - Request: Path variable `idClient` (Integer) — Client ID
  - Response: 200, `ApiResponse<Void>`
  - Auth: Bearer token (ADMIN only)
---
**Sales** — [src/main/java/com/modelosgr86e1eq6/proyectofacturacion/sales/controllers/SaleController.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/sales/controllers/SaleController.java)

- POST /api/v1/sales
  - Description: Create a new sale (RF-12)
  - Request: `CreateSaleRequest` ([src/main/java/com/modelosgr86e1eq6/proyectofacturacion/sales/dto/CreateSaleRequest.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/sales/dto/CreateSaleRequest.java))
  - Response: 201, `ApiResponse<SaleDetailResponse>` ([src/main/java/com/modelosgr86e1eq6/proyectofacturacion/sales/dto/SaleDetailResponse.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/sales/dto/SaleDetailResponse.java))
  - Auth: Bearer token (ADMIN, EMPLOYEE)

- POST /api/v1/sales/{id}/products
  - Description: Add a product to an existing sale (RF-13 + RF-15)
  - Request: Path variable `id` (Integer) — Sale ID, `CreateSaleDetailRequest` ([src/main/java/com/modelosgr86e1eq6/proyectofacturacion/sales/dto/CreateSaleDetailRequest.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/sales/dto/CreateSaleDetailRequest.java))
  - Response: 201, `ApiResponse<SaleItemResponse>` ([src/main/java/com/modelosgr86e1eq6/proyectofacturacion/sales/dto/SaleItemResponse.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/sales/dto/SaleItemResponse.java))
  - Auth: Bearer token (ADMIN, EMPLOYEE)

- PUT /api/v1/sales/{id}/confirm
  - Description: Confirm a sale, changing its status to completed (RF-14)
  - Request: Path variable `id` (Integer) — Sale ID
  - Response: 200, `ApiResponse<SaleDetailResponse>` ([src/main/java/com/modelosgr86e1eq6/proyectofacturacion/sales/dto/SaleDetailResponse.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/sales/dto/SaleDetailResponse.java))
  - Auth: Bearer token (ADMIN, EMPLOYEE)

- GET /api/v1/sales
  - Description: List sales with optional filters and pagination (RF-16)
  - Request: Query params `clientId` (Integer, optional), `status` (SaleStatus, optional) — PENDING, CONFIRMED, CANCELLED, `from` (LocalDateTime, optional, ISO format), `to` (LocalDateTime, optional, ISO format), plus standard pagination params (`page`, `size`, `sort` — default sort by `saleDate`)
  - Response: 200, `ApiResponse<Page<SaleSummaryResponse>>` ([src/main/java/com/modelosgr86e1eq6/proyectofacturacion/sales/dto/SaleSummaryResponse.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/sales/dto/SaleSummaryResponse.java))
  - Auth: Bearer token (ADMIN, EMPLOYEE)

- GET /api/v1/sales/{id}
  - Description: Get complete sale detail including all product lines (RF-17)
  - Request: Path variable `id` (Integer) — Sale ID
  - Response: 200, `ApiResponse<SaleDetailResponse>` ([src/main/java/com/modelosgr86e1eq6/proyectofacturacion/sales/dto/SaleDetailResponse.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/sales/dto/SaleDetailResponse.java))
  - Auth: Bearer token (ADMIN, EMPLOYEE)

- PATCH /api/v1/sales/{id}/cancel
  - Description: Cancel a sale (RF-18)
  - Request: Path variable `id` (Integer) — Sale ID
  - Response: 200, `ApiResponse<SaleDetailResponse>` ([src/main/java/com/modelosgr86e1eq6/proyectofacturacion/sales/dto/SaleDetailResponse.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/sales/dto/SaleDetailResponse.java))
  - Auth: Bearer token (ADMIN, EMPLOYEE)

- PATCH /api/v1/sales/{id}/products/{detailId}
  - Description: Update quantity of a product in a sale (RF-19)
  - Request: Path variable `id` (Integer) — Sale ID, Path variable `detailId` (Integer) — Sale detail ID, `UpdateSaleDetailRequest` ([src/main/java/com/modelosgr86e1eq6/proyectofacturacion/sales/dto/UpdateSaleDetailRequest.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/sales/dto/UpdateSaleDetailRequest.java))
  - Response: 200, `ApiResponse<SaleItemResponse>` ([src/main/java/com/modelosgr86e1eq6/proyectofacturacion/sales/dto/SaleItemResponse.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/sales/dto/SaleItemResponse.java))
  - Auth: Bearer token (ADMIN, EMPLOYEE)

- DELETE /api/v1/sales/{id}/products/{detailId}
  - Description: Remove a product from a sale (RF-20)
  - Request: Path variable `id` (Integer) — Sale ID, Path variable `detailId` (Integer) — Sale detail ID
  - Response: 200, `ApiResponse<Void>`
  - Auth: Bearer token (ADMIN, EMPLOYEE)
---

**Invoices** — [src/main/java/com/modelosgr86e1eq6/proyectofacturacion/invoices/controllers/InvoiceController.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/invoices/controllers/InvoiceController.java)

- POST /api/v1/invoices
  - Description: Generate an invoice from an existing sale (RF-18/RF-19). Invoice number is auto-assigned by database trigger. Invoice type (`SIMPLE` or `DETAILED`) determines which sections the Builder pattern constructs.
  - Request: `CreateInvoiceRequest` ([src/main/java/com/modelosgr86e1eq6/proyectofacturacion/invoices/dto/CreateInvoiceRequest.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/invoices/dto/CreateInvoiceRequest.java))
  - Response: 201, `ApiResponse<InvoiceResponse>` ([src/main/java/com/modelosgr86e1eq6/proyectofacturacion/invoices/dto/InvoiceResponse.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/invoices/dto/InvoiceResponse.java))
  - Auth: Bearer token (ADMIN)

- GET /api/v1/invoices
  - Description: List all invoices ordered by creation date descending
  - Request: none
  - Response: 200, `ApiResponse<List<InvoiceResponse>>` ([src/main/java/com/modelosgr86e1eq6/proyectofacturacion/invoices/dto/InvoiceResponse.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/invoices/dto/InvoiceResponse.java))
  - Auth: Bearer token (ADMIN, EMPLOYEE)

- GET /api/v1/invoices/{id}
  - Description: Get complete invoice detail including product lines
  - Request: Path variable `id` (Integer) — Invoice PK
  - Response: 200, `ApiResponse<InvoiceResponse>` ([src/main/java/com/modelosgr86e1eq6/proyectofacturacion/invoices/dto/InvoiceResponse.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/invoices/dto/InvoiceResponse.java))
  - Auth: Bearer token (ADMIN, EMPLOYEE)

- GET /api/v1/invoices/{id}/export
  - Description: Export invoice as PDF file
  - Request: Path variable `id` (Integer) — Invoice PK, Query param `format` (String, optional, default: "pdf")
  - Response: 200, `application/pdf` (binary file download with filename `invoice-{invoiceNumber}.pdf`)
  - Auth: Bearer token (ADMIN, EMPLOYEE)

---
**Payments** — [src/main/java/com/modelosgr86e1eq6/proyectofacturacion/payments/controllers/PaymentController.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/payments/controllers/PaymentController.java)

- POST /api/v1/payments
  - Description: Process a payment for an invoice (RF-25)
  - Request: `CreatePaymentRequest` ([src/main/java/com/modelosgr86e1eq6/proyectofacturacion/payments/dto/CreatePaymentRequest.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/payments/dto/CreatePaymentRequest.java))
  - Response: 201, `ApiResponse<PaymentResponse>` ([src/main/java/com/modelosgr86e1eq6/proyectofacturacion/payments/dto/PaymentResponse.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/payments/dto/PaymentResponse.java))
  - Auth: Bearer token (ADMIN, EMPLOYEE)

- POST /api/v1/payments/webhook
  - Description: Receive payment gateway webhook notifications (RF-26)
  - Request: Header `X-Signature` (String) — Signature for payload verification, Body `Map<String, Object>` — Webhook payload from payment provider
  - Response: 200, Empty body
  - Auth: none (validated via signature header)

- GET /api/v1/payments/{id}
  - Description: Get payment details by ID (RF-27)
  - Request: Path variable `id` (Long) — Payment PK
  - Response: 200, `ApiResponse<PaymentResponse>` ([src/main/java/com/modelosgr86e1eq6/proyectofacturacion/payments/dto/PaymentResponse.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/payments/dto/PaymentResponse.java))
  - Auth: Bearer token (ADMIN, EMPLOYEE)

- POST /api/v1/payments/manual
  - Description: Register a manual payment (cash, transfer, etc.) (RF-28)
  - Request: `ManualPaymentRequest` ([src/main/java/com/modelosgr86e1eq6/proyectofacturacion/payments/dto/ManualPaymentRequest.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/payments/dto/ManualPaymentRequest.java))
  - Response: 201, `ApiResponse<PaymentResponse>` ([src/main/java/com/modelosgr86e1eq6/proyectofacturacion/payments/dto/PaymentResponse.java](src/main/java/com/modelosgr86e1eq6/proyectofacturacion/payments/dto/PaymentResponse.java))
  - Auth: Bearer token (ADMIN, EMPLOYEE)

- GET /api/v1/payments
  - Description: List payments with optional filters and pagination
  - Request: Query params `invoiceId` (Integer, optional), `method` (PaymentMethod, optional) — CASH, CREDIT_CARD, DEBIT_CARD, TRANSFER, `status` (PaymentStatus, optional) — PENDING, COMPLETED, FAILED, REFUNDED, plus standard pagination params (`page`, `size`, `sort`)
  - Response: 200, `ApiResponse<Page<PaymentResponse>>` — Paginated list of payments
  - Auth: Bearer token (ADMIN, EMPLOYEE)

- GET /api/v1/payments/qr/{invoiceId}
  - Description: Generate QR code image for invoice payment (RF-23)
  - Request: Path variable `invoiceId` (Integer) — Invoice ID to generate QR for
  - Response: 200, `image/png` (binary QR code image)
  - Auth: Bearer token (ADMIN, EMPLOYEE)
 
