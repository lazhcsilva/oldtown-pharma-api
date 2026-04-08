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
