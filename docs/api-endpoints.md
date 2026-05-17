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
