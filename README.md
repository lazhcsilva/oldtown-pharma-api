# 📦 Oldtown Pharma API

REST API for pharmacy management, built with a focus on clean architecture, scalability, and best backend development practices.

---

## 🚀 Technologies

* Java 17+
* Spring Boot
* Spring Data JPA
* Bean Validation (Jakarta Validation)
* PostgreSQL
* Swagger / OpenAPI
* Gradle

---

## 📐 Architecture

The project follows a layered architecture:

```
controller → service → repository → entity
                 ↓
                dto
                 ↓
               mapper
```

### 📂 Package Structure

```
br.com.oldtown.pharma
│
├── config
├── shared
│   ├── exception
│   ├── handler
│   └── response
│
├── auth
├── user
├── product
├── category
├── order (in progress)
└── prescription
```

---

## 🧠 Features

### 👤 User

* Create user
* Get all users (paginated)
* Get user by ID
* Get user by email
* Update user
* Delete user
* Change password with validation

### 📦 Product

* Create product
* Associate with category
* Paginated listing
* Search by name

### 🗂️ Category

* Full CRUD operations
* Relationship with products

---

## 🔐 Security

* Password hashing using `PasswordEncoder`
* Old password validation before update
* DTO usage to avoid exposing entities

---

## 📑 API Documentation

Swagger UI available at:

```
http://localhost:8080/swagger-ui.html
```

---

## 📬 Endpoints Examples

### 🔑 Change Password

```
PATCH /users/{id}/change-password
```

Body:

```json
{
  "oldPassword": "123456",
  "confirmOldPassword": "123456",
  "newPassword": "newPassword123"
}
```

---

### 👤 Create User

```
POST /users
```

---

### 📦 Get Products (Paginated)

```
GET /products?page=0&size=10
```

---

## ⚠️ Error Handling

Global error handling is implemented using `GlobalExceptionHandler`.

Example response:

```json
{
<<<<<<< Updated upstream
=======
  "id": 42,
  "firstName": "Example",
  "lastName": "User",
  "email": "portfolio.user@example.com",
  "role": "CUSTOMER"
}
```

The password and its hash are not included in `UserResponse`. Repeating creation with an existing email produces a conflict.

## 🔐 Security and Current Limitations

- Passwords are hashed with BCrypt.
- The dedicated `PATCH /api/v1/users/{id}/password` operation validates the current password, confirmation and a minimum new-password length of eight characters.
- `PUT /api/v1/users/{id}` updates only `firstName`, `lastName` and `email`; it preserves the password hash. Password changes use the dedicated endpoint above.
- Authentication and endpoint authorization are not implemented. Password hashing does not restrict access to API operations.

Other known limitations identified through source inspection:

- Product price filters reference `currentPrice`, which is a transient JPA attribute. The unused helper expression also references date property names that differ from the entity.
- Promotion status is stored separately from the date-based current-price calculation and does not automatically follow time transitions in the inspected flows.
- Common-product creation does not explicitly set `active` to `true`.
- Product search executes the repository query twice.
- Product validation needs additional handling for null prices, nested medicine details and promotion dates.

These observations have not been reproduced through runtime tests as part of this documentation update.

## ⚠️ Error Handling and Validation

`GlobalExceptionHandler` uses `@RestControllerAdvice` to map application exceptions and supported validation errors to `HttpErrorResponse`.

| Status | Handled cases |
| --- | --- |
| `400 Bad Request` | Business input errors, invalid prices, request-body validation, constraint violations and unreadable request bodies |
| `404 Not Found` | Resources not found |
| `409 Conflict` | Conflicting operations, such as duplicate records detected by the service |
| `500 Internal Server Error` | Exceptions falling through to the generic handler |

Example of the response structure for an invalid email in user creation:

```json
{
  "timestamp": "2026-05-24T10:30:00",
>>>>>>> Stashed changes
  "status": 400,
  "message": "Invalid request",
  "path": "/users"
}
```

Handled status codes:

* 400 → Bad Request
* 404 → Not Found
* 409 → Conflict
* 500 → Internal Server Error (can be improved)

---

## 🧪 Validation

Using Bean Validation annotations:

* `@NotBlank`
* `@Email`
* `@Positive`
* `@Size`

---

## 📌 Best Practices Applied

* DTO separation (Request / Response)
* Mapper pattern
* Pagination with `Pageable`
* Global exception handling
* Domain-based modular structure
* Use of `ResponseEntity`

---

## 🚧 Future Improvements

* [ ] JWT authentication
* [ ] Role-based authorization
* [ ] Improved 500 error handling
* [ ] Unit and integration tests
* [ ] Docker support
* [ ] Redis caching
* [ ] Audit logging

---

## 📈 Project Goal

This project was built as part of backend development evolution, focusing on:

* Clean architecture
* Scalable design
* Real-world API patterns

---

## 👨‍💻 Author

**Lázaro Henrique Silva**

Backend Developer (Java)

---

## 📝 License

This project is intended for educational and portfolio purposes.
