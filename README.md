# 📦 Oldtown Pharma API

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.3-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![H2](https://img.shields.io/badge/Database-H2-blue?style=for-the-badge)
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=Gradle&logoColor=white)

A REST API for practicing backend development through pharmacy management features. The project uses a layered architecture organized by domain, with controllers, services, repositories, DTOs, mappers and JPA entities.

The application is under development. It currently uses an in-memory H2 database; no external database installation is required.

## ⚙️ Getting Started

### Requirements

- JDK 17, matching the Java toolchain configured in the build.
- Internet access on the first build to download Gradle and dependencies.

The repository includes the Gradle Wrapper, so a separate Gradle installation is not required. PostgreSQL is not configured and its JDBC driver is not included.

### 1. Clone the repository

```bash
git clone https://github.com/lazhcsilva/oldtown-pharma-api.git
cd oldtown-pharma-api
```

### 2. Run the application

On Linux or macOS:

```bash
./gradlew bootRun
```

On Windows:

```powershell
.\gradlew.bat bootRun
```

### 3. Explore the API

- Base URL: `http://localhost:8080`
- Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- OpenAPI JSON: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

### Local database lifecycle

The default configuration in [`application.properties`](src/main/resources/application.properties) uses H2 at `jdbc:h2:mem:old-town-pharma`, with `spring.jpa.hibernate.ddl-auto=create-drop`.

Data is not retained between application runs. Hibernate creates the schema on startup, and [`data.sql`](src/main/resources/data.sql) initializes development data after schema creation. Changes made through the API are lost when the application stops.

The H2 console is enabled at [http://localhost:8080/h2-console](http://localhost:8080/h2-console). Use the JDBC URL above and the local connection settings from `application.properties`.

## 🚀 Technologies

| Component | Declared version or implementation |
| --- | --- |
| Java toolchain | 17 |
| Spring Boot | 4.0.3 |
| Build | Gradle Wrapper 9.3.1, Kotlin DSL |
| Persistence | Spring Data JPA and H2 |
| Validation | Jakarta Bean Validation |
| Mapping | MapStruct 1.6.3 |
| API documentation | Springdoc OpenAPI 3.0.3 |
| Password hashing | BCrypt |
| Tests | JUnit Jupiter, Mockito and AssertJ |

See [`build.gradle.kts`](build.gradle.kts) for dependencies and explicitly pinned versions.

## 📐 Architecture

The application follows a layered architecture organized by domain:

```text
HTTP request → Controller → Service → Repository → Database
                  DTOs         Entities (JPA)
```

- **Controllers** bind HTTP input, trigger request validation and build HTTP responses.
- **Services** coordinate business rules and repository operations.
- **Repositories** provide persistence access through Spring Data JPA.
- **Entities** represent the persistence model.
- **DTOs** define request and response data independently of entities in the category, product and user APIs.
- **Mappers** convert between DTOs and entities using MapStruct.
- **Specifications** compose optional product search predicates passed to the repository; they are not a mandatory layer for every operation.

### Package structure

```text
br.com.oldtown.pharma
├── category
├── config
├── infrastructure
│   └── parameter
├── order
├── prescription   # in progress
├── product
├── shared
├── stock          # in progress
└── user
```

The order API uses request/response DTOs and persists orders transactionally. Stock, payment and prescription workflows remain separate milestones.

## 🧠 Implementation Status

| Domain | Current implementation |
| --- | --- |
| User | Creation, paginated listing, lookup by ID or email, update, soft deletion and a dedicated password-change endpoint |
| Category | Creation, paginated listing, lookup by ID or name, update and deletion; association with products |
| Product | Common products and medicines, category association, basic updates, price updates, promotion operations, current-price calculation, soft deletion and paginated search |
| Order | Transactional creation, paginated listing, lookup, item updates in CREATED status and logical cancellation; server-calculated prices and totals |
| Prescription / Stock | Initial persistence models; business workflows are not complete |

The order workflow records a purchase request and its prices. It does not yet process payments, check prescriptions or reserve/decrement stock.

## 🔎 Product Search

`GET /api/v1/products/search` accepts the following optional query parameters:

| Parameter | Meaning |
| --- | --- |
| `name` | Case-insensitive partial product name |
| `type` | Product type: `COMMON` or `MEDICINE` |
| `therapeuticClass` | A value from the `TherapeuticClass` enum |
| `active` | Active or inactive products |
| `categoryId` | Category ID |
| `minPrice` | Minimum current price, inclusive |
| `maxPrice` | Maximum current price, inclusive |
| `page` | Zero-based page index; defaults to `0` |
| `size` | Page size; defaults to `10` |
| `sort` | Persistent property and direction, such as `name,asc` |

Specifications combine the supplied business filters. Query performance depends on the generated SQL, relationships, database and indexes; using Specifications alone does not guarantee efficiency.

Example combining product filters and pagination:

```bash
curl --get 'http://localhost:8080/api/v1/products/search' \
  --data-urlencode 'name=para' \
  --data-urlencode 'type=MEDICINE' \
  --data-urlencode 'page=0' \
  --data-urlencode 'size=10' \
  --data-urlencode 'sort=name,asc'
```

## 📬 Request and Response Examples

The examples below describe the controller and DTO contracts. They are illustrative, not captured results from an automated API test. Generated IDs and existing data depend on the current application run.

### Create a category

```bash
curl -i -X POST 'http://localhost:8080/api/v1/categories' \
  -H 'Content-Type: application/json' \
  -d '{"name":"Portfolio category","description":"Category created through the API"}'
```

On successful creation, the controller returns `201 Created` with a body such as:

```json
{
  "id": 42,
  "name": "Portfolio category",
  "description": "Category created through the API"
}
```

### Find a category

Replace `42` with the ID returned by the creation request:

```bash
curl -i 'http://localhost:8080/api/v1/categories/42'
```

On success, the response is `200 OK` with the category response structure shown above. GET requests do not require a JSON body.

To search by exact category name:

```bash
curl --get 'http://localhost:8080/api/v1/categories/search' \
  --data-urlencode 'name=Portfolio category'
```

### Create a user

```bash
curl -i -X POST 'http://localhost:8080/api/v1/users' \
  -H 'Content-Type: application/json' \
  -d '{"firstName":"Example","lastName":"User","email":"portfolio.user@example.com","password":"Example123!"}'
```

On successful creation, the controller returns `201 Created` with a body such as:

```json
{
  "id": 42,
  "firstName": "Example",
  "lastName": "User",
  "email": "portfolio.user@example.com",
  "role": "CUSTOMER"
}
```

The password and its hash are not included in `UserResponse`. Repeating creation with an existing email produces a conflict.

## 🛒 Orders

Use `/api/v1/orders`. The existing `/orders` path is retained as an alias. Requests now identify the customer and products by ID; responses are DTOs and no longer return persistence entities.

Create an order with `POST /api/v1/orders` (`201 Created`):

```json
{
  "customerId": 1,
  "items": [
    { "productId": 31, "quantity": 2 }
  ]
}
```

Use existing IDs from the current application run. Prices, totals and initial status are calculated by the server; sending price/total fields does not override them.

- `GET /api/v1/orders?page=0&size=10`: paginated DTO response, including an empty page when there are no matches. Sortable fields: `id`, `createdAt`, `status`, `totalAmount`.
- `GET /api/v1/orders/{id}`: order DTO with customer ID, status, total, creation time and item DTOs.
- `PUT /api/v1/orders/{id}`: replace the items of a `CREATED` order with `{"items":[{"productId":31,"quantity":3}]}`. Products remaining in the order retain their recorded unit prices; new products use their current prices. The customer is not changed.
- `DELETE /api/v1/orders/{id}`: change a `CREATED` order to `CANCELED` and return `204`. Repeated cancellation is idempotent. History and recorded prices are preserved. `PAID` and `FINISHED` orders reject edits/cancellation through these operations.

Orders require an active customer, active products, distinct product IDs and positive quantities up to 1,000,000. They accept at most 100 items and a total up to 99,999,999.99, matching database precision. Unknown resources return `404`; conflicting states return `409`.

## 🔐 Security and Current Limitations

- Passwords are hashed with BCrypt.
- The dedicated `PATCH /api/v1/users/{id}/password` operation validates the current password, confirmation and a minimum new-password length of eight characters.
- `PUT /api/v1/users/{id}` updates only `firstName`, `lastName` and `email`, preserving the password hash. Password changes use the dedicated endpoint above. Password hashes are also excluded from entity serialization.
- Authentication and endpoint authorization are not implemented. Password hashing does not restrict access to API operations.

Product and promotion behavior:

- Products and users start active. Product creation initializes timestamps through JPA lifecycle callbacks.
- Price filtering uses a database expression for the current price and executes a single repository search.
- Promotion status is calculated from price and dates: `NONE`, `SCHEDULED`, `ACTIVE` or `EXPIRED`. Start and end instants are inclusive.
- Sorting by `status` uses a read-only Hibernate SQL formula; no separate status column is stored.
- Creating a product with a promotion uses the same service validation as the dedicated promotion operation: price above cost, required dates, end after start and a promotion that has not already ended.
- Dedicated promotion requests use strict `dd/MM/yyyy HH:mm:ss` parsing. Product creation uses ISO local date-time strings, such as `2030-01-01T10:00:00`.
- Nested medicine details are validated. Short product/category names and dosages are supported by SKU generation.
- Invalid sort properties, malformed parameters and invalid price ranges return `400`. Price bounds must be non-negative and `minPrice` must not exceed `maxPrice`.
- Product and order updates use optimistic locking; concurrent-update conflicts return `409`.

Current limits: authentication/authorization, stock movements, payment processing and prescription checks are not implemented. The database remains local H2 with `create-drop`. Formula-based sorting uses the database clock, while Java calculations use the application clock; keep both on the same time zone and validate behavior when introducing another database.

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
  "status": 400,
  "error": "Bad Request",
  "message": "Validation error.",
  "path": "/api/v1/users",
  "details": [
    "email: Invalid email"
  ]
}
```

Validation uses annotations including `@NotBlank`, `@NotNull`, `@Email`, `@Positive`, `@Size` and `@AssertTrue`. Their use does not imply that every invalid-input scenario is covered. The generic handler returns a fixed message rather than the internal exception message; unexpected errors are recorded through the application logger. Integrity and optimistic-locking conflicts return `409` without exposing database details.

## 🧪 Tests

Run the existing test suite:

```bash
./gradlew test
```

Run only the category service unit tests:

```bash
./gradlew test --tests 'br.com.oldtown.pharma.category.service.impl.CategoryServiceImplTest'
```

On Windows, replace `./gradlew` with `.\gradlew.bat`.

The suite includes category/product/user service tests, product promotion boundary tests, MockMvc controller tests, JPA Specification tests and complete product/order HTTP integration tests using H2. Integration scenarios cover nested validation, strict calendar dates, creation defaults, price filtering, order persistence, historical prices, cancellation and credential protection.

After test execution, the HTML report is generated at `build/reports/tests/test/index.html`. Tests using H2 validate the current local configuration; PostgreSQL compatibility and concurrency under real load require separate validation.

## 🚧 Roadmap

### Correctness and verification

- [x] Correct product price filters, promotion lifecycle and product activation defaults
- [x] Separate profile updates from password changes and improve input validation
- [x] Remove duplicate product search execution
- [x] Initial unit tests for CategoryService
- [x] Unit tests for ProductService and UserService
- [x] Controller and repository integration tests
- [ ] Automated build and tests in CI

### Persistence and business workflows

- [ ] PostgreSQL profile, JDBC driver and versioned database migrations
- [x] Basic order creation, item updates and cancellation
- [ ] Integrate orders with stock, payment and prescription workflows
- [ ] Stock movements and concurrency handling
- [ ] Prescription workflows

### Security and operation

- [ ] Authentication and role-based authorization
- [ ] JWT authentication and refresh tokens
- [ ] Token revocation strategy
- [ ] Granular permissions control
- [ ] Password reset and email confirmation
- [ ] MFA/2FA
- [ ] Rate limiting
- [ ] Structured logging and audit logging
- [ ] Docker support

## 📈 Project Goal

This personal project supports professional development through practice with Java, layered architecture organized by domain, REST contracts, validation, JPA persistence and automated testing.

The next milestones focus on consistent business rules, complete sales workflows and evidence from tests.

## 👨‍💻 Author

**Lázaro Henrique Silva** — Backend Developer (Java)

## 📝 License Status

The project is intended for educational and portfolio purposes. No license file is currently included in the repository, so this README does not declare an open-source license.

Swagger does not advertise a license until a license is selected and added to the repository.
