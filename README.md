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
├── order          # in progress
├── prescription   # in progress
├── product
├── shared
├── stock          # in progress
└── user
```

The order API is still being developed and currently exposes entities directly.

## 🧠 Implementation Status

| Domain | Current implementation |
| --- | --- |
| User | Creation, paginated listing, lookup by ID or email, update, soft deletion and a dedicated password-change endpoint |
| Category | Creation, paginated listing, lookup by ID or name, update and deletion; association with products |
| Product | Common products and medicines, category association, basic updates, price updates, promotion operations, current-price calculation, soft deletion and paginated search |
| Order | Initial entities, repository, controller and service; insertion does not persist an order, and update/delete service methods are empty |
| Prescription / Stock | Initial persistence models; business workflows are not complete |

Implemented operations still have known limitations listed below. Order endpoints should not be interpreted as completed sales workflows, even when they return a successful HTTP status.

## 🔎 Product Search

`GET /api/v1/products/search` accepts the following optional query parameters:

| Parameter | Meaning |
| --- | --- |
| `name` | Case-insensitive partial product name |
| `type` | Product type: `COMMON` or `MEDICINE` |
| `therapeuticClass` | A value from the `TherapeuticClass` enum |
| `active` | Active or inactive products |
| `categoryId` | Category ID |
| `minPrice` | Minimum current price; implementation requires correction |
| `maxPrice` | Maximum current price; implementation requires correction |
| `page` | Zero-based page index; defaults to `0` |
| `size` | Page size; defaults to `10` |
| `sort` | Persistent property and direction, such as `name,asc` |

Specifications combine the supplied business filters. Query performance depends on the generated SQL, relationships, database and indexes; using Specifications alone does not guarantee efficiency.

Example without the price filters that currently require correction:

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

## 🔐 Security and Current Limitations

- Passwords are hashed with BCrypt.
- The dedicated `PATCH /api/v1/users/{id}/password` operation validates the current password, confirmation and a minimum new-password length of eight characters.
- The general user update operation also accepts a password, but does not check the current password and uses a minimum length of six characters. These paths still need a consistent policy.
- Authentication and endpoint authorization are not implemented. Password hashing does not restrict access to API operations.

Other known limitations identified through source inspection:

- Product price filters reference `currentPrice`, which is a transient JPA attribute. The unused helper expression also references date property names that differ from the entity.
- Promotion status is stored separately from the date-based current-price calculation and does not automatically follow time transitions in the inspected flows.
- Common-product creation does not explicitly set `active` to `true`.
- Product search executes the repository query twice.
- Product validation needs additional handling for null prices, nested medicine details and promotion dates.

These observations have not been reproduced through runtime tests as part of this documentation update.

Instead of exposing internal server stack traces, the API consistently returns a standardized, user-friendly JSON response:
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

Validation uses annotations including `@NotBlank`, `@NotNull`, `@Email`, `@Positive`, `@Size` and `@AssertTrue`. Their use does not imply that every invalid-input scenario is covered. The generic handler returns a fixed message rather than the internal exception message; handlers currently print stack traces to the application console.

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

The suite currently contains category service unit tests using Mockito and a Spring Boot context-loading test. Category tests include successful operations, missing resources, duplicate names and empty pagination. They do not exercise real database queries or HTTP contracts.

After test execution, the HTML report is generated at `build/reports/tests/test/index.html`. Product/user service tests and controller/database integration coverage remain planned. Test commands are documented here; no passing result is claimed by this README.

## 🚧 Roadmap

### Correctness and verification

- [ ] Correct product price filters, promotion lifecycle and product activation defaults
- [ ] Unify password-change rules and improve input validation
- [ ] Remove duplicate product search execution
- [x] Initial unit tests for CategoryService
- [ ] Unit tests for ProductService and UserService
- [ ] Controller and repository integration tests
- [ ] Automated build and tests in CI

### Persistence and business workflows

- [ ] PostgreSQL profile, JDBC driver and versioned database migrations
- [ ] Complete order creation, updates and cancellation
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

The current Swagger configuration advertises Apache 2.0, but this metadata is not accompanied by a repository license file. A license decision and alignment of that metadata remain pending.
