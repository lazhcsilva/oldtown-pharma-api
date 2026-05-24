# 📦 Oldtown Pharma API

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=Gradle&logoColor=white)

> A REST API management system for small pharmacies to efficiently track products, categories, prescriptions, and orders, built with a focus on clean architecture, scalability, and backend development best practices.

---

## ⚙️ Getting Started (How to Run)

To run this project locally, you will need Java 17+ and PostgreSQL installed on your machine.

**1. Clone the repository**

```bash
git clone [https://github.com/lazhcsilva/oldtown-pharma-api.git](https://github.com/lazhcsilva/oldtown-pharma-api.git)
cd oldtown-pharma-api
```

**2. Configure the Database**
Create a PostgreSQL database named oldtown_pharma and update the src/main/resources/application.properties file with your database credentials:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/oldtown_pharma
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```
**2. Configure the Database**
```bash
./gradlew bootRun
```
The API will start at http://localhost:8080/swagger-ui/index.html

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
controller → service → specification → repository → entity
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
* Update product
* Soft delete
* Associate with category
* Paginated listing
  * Specifications: Handle dynamic query building and complex database filtering.   
  * Search by name, type, therapeutic class, actives, price and category

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

To provide a highly flexible and scalable API, this project uses **Spring Data JPA Specifications** for complex database querying. 

Instead of hardcoding multiple static methods in repositories to handle various filter combinations (e.g., filtering by name, category, or both), `Specifications` allow the API to dynamically construct safe and efficient SQL queries at runtime based solely on the parameters provided by the client. This keeps the data access layer clean, modular, and easy to maintain.

---

## 🛡️ Robust Error Handling

Dynamic filtering and strict business rules require robust validation. To ensure a predictable and safe contract with any client consuming the API (Frontend or Mobile), the project implements a `GlobalExceptionHandler` using `@RestControllerAdvice`.

Any exceptions thrown within the application—such as a `NotFoundException` when a queried resource doesn't exist, a `ConflictException` during data modification, or `MethodArgumentNotValidException` from invalid payload inputs—are intercepted globally. 

Instead of exposing internal server stack traces, the API consistently returns a standardized, user-friendly JSON response:

```json
{
  "timestamp": "2026-05-24T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation error.",
  "path": "/users",
  "details": [
    "email: must be a well-formed email address"
  ]
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

* [ ] Order domain
* [ ] Prescription domain
* [ ] Stock domain
* [ ] JWT authentication + RefreshToken
* [ ] Token revocation
* [ ] Token blacklist
* [ ] Role-based authorization
* [ ] Granular permissions control
* [ ] Unit and integration tests
* [ ] Docker support
* [ ] Password reset
* [ ] Audit logging
* [ ] Email confirmation
* [ ] MFA/2FA
* [ ] Rate limiting

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
