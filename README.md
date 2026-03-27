# Payment Gateway

## Architecture Overview

This project implements a Payment Gateway API built with Java 17 and Spring Boot 3.1.5, following a layered architecture with clear separation of concerns across the following packages:

- **controller** - handles incoming HTTP requests and delegates to the service layer
- **service** - orchestrates payment processing, coordinating between the bank simulator and repository
- **mapper** - responsible for mapping between request DTOs and domain objects
- **repository** - manages in-memory storage of payment records
- **model** - contains the `Payment` domain object representing a stored payment
- **dto** - contains request and response objects for the client API and bank simulator
- **enums** - contains `PaymentStatus` and `Currency` enums shared across layers
- **utils** - stateless utility classes for card masking and expiry date formatting
- **exception** - global exception handling via `GlobalExceptionHandler`
- **configuration** - Spring Bean configuration

Key design principles applied throughout:

- **Single Responsibility** - each class has one clearly defined purpose
- **Open/Closed** - classes are structured to be open for extension without modification, with interfaces and layered separation making future integrations straightforward, such as swapping the bank simulator for a real acquiring bank
- **Liskov Substitution / Composition over Inheritance** - composition is preferred over inheritance throughout, reducing coupling and avoiding the tight constraints that deep inheritance hierarchies impose
- **Interface Segregation** - mappers are defined as separate focused interfaces (`BankRequestMapper`, `PaymentMapper`) rather than a single general-purpose mapper interface, ensuring classes only depend on what they need
- **Dependency Inversion** - services and repositories depend on abstractions rather than concrete implementations, enabling easier testing and future extensibility
- **YAGNI** - implementation is scoped to the requirements without speculative complexity

---

## How to Run

### Prerequisites
- Java 17
- Docker Desktop - required to run the bank simulator

### Starting the Bank Simulator
The bank simulator runs as a Docker container and must be started before the application.

1. Ensure Docker Desktop is running
2. Navigate to the project root directory
3. Run the following command:
```bash
docker-compose up
```
The bank simulator will be available at `http://localhost:8080`

### Starting the Application
Once the bank simulator is running, start the Spring Boot application:

```bash
./gradlew bootRun
```

The application will be available at `http://localhost:8090`

### Swagger UI
The API documentation is available via Swagger UI once the application is running:

```
http://localhost:8090/swagger-ui/index.html
```

---

## Endpoints

### POST /payment/process - Process a Payment
Submits a payment request to the bank simulator and returns the result.

**Responses:**
- `201 Created` - payment processed, returns the payment details with an `Authorized` or `Declined` status
- `400 Bad Request` - invalid request body, returns a `Rejected` status with an error message

### GET /payment/{id} - Retrieve a Payment
Returns the details of a previously processed payment by its unique identifier.

**Responses:**
- `200 OK` - payment found, returns the payment details
- `404 Not Found` - no payment exists with the given ID

Full request and response schemas are documented in the Swagger UI at `http://localhost:8090/swagger-ui/index.html`

---

## Payment Status Mapping

Payments can have one of three statuses:

- **Authorized** - the bank simulator returned `authorized: true`
- **Declined** - the bank simulator returned `authorized: false`, or the bank simulator was unreachable or returned an error
- **Rejected** - the request failed gateway-level validation and never reached the bank simulator

### Rejected vs Declined
The distinction between `Rejected` and `Declined` is deliberate. `Rejected` is a gateway-level status meaning the request was invalid - the bank simulator was never contacted. `Declined` means the request reached the bank but was not approved.

The following outcomes are all mapped to `Declined`:
- Bank simulator returned `authorized: false`
- Bank simulator returned a 503 Service Unavailable response
- Network errors or any other unexpected exceptions thrown during the bank simulator call

This ensures that any failure occurring after the payment has been forwarded to the bank results in a `Declined` status rather than an unhandled error. In production, more granular decline reason codes would distinguish these cases, and the expected response codes for bank unavailability and network failures would be confirmed with the acquiring bank before implementation.

---

## CVV and Card Masking

### CVV Handling
The CVV is used solely to construct the request sent to the bank simulator for authorisation. It is never persisted to storage at any point. Persisting a CVV in any form is a PCI-DSS violation - this implementation discards the CVV immediately after the bank simulator request is made.

Additionally, `toString()` on the `PostPaymentRequest` and `BankRequest` objects explicitly excludes the CVV to prevent it from appearing in application logs.

### Card Number Masking
The full card number is required in the request to the bank simulator, however only the last 4 digits are stored and returned in responses. This ensures the full card number is never persisted to storage or returned to the client.

Similarly, `toString()` on `PostPaymentRequest` and `BankRequest` only includes the last 4 digits of the card number to prevent the full card number from appearing in application logs.

---

## Currency Validation

Currency is validated using a `Currency` enum containing exactly three supported values - `GBP`, `USD`, `EUR` - matching the requirements. Any value outside of these three will fail deserialisation before reaching the service layer, resulting in a `Rejected` status returned to the client.

Using an enum rather than a `String` provides two benefits - invalid currencies are rejected implicitly at deserialisation without requiring explicit validation logic, and it eliminates string literals being passed around the codebase.

---

## Amount

Amounts are represented as positive integers in minor currency units. For example £10.50 is represented as `1050`. This matches the format expected by the bank simulator and avoids floating point precision issues that would arise from using `double` or similar types.

---

## Storage

Payments are stored in-memory using a `ConcurrentHashMap` keyed by payment UUID. This is intentional for the scope of this assessment.

In a production implementation this would be replaced with a persistent store such as a relational database, with a unique index on the payment UUID. The repository layer is abstracted behind a `PaymentsRepository` interface, meaning the storage implementation can be swapped without touching the service layer.

---

## Idempotency

Idempotency is not implemented in this assessment as it was outside the stated scope.

In a production payment gateway, idempotency is a critical requirement. Merchants may retry a payment request due to network timeouts or other transient failures - without idempotency handling, this could result in duplicate charges to the cardholder.

The standard approach is a client-supplied `Idempotency-Key` header included with each payment request. The gateway stores the key alongside the payment record and on any subsequent request with the same key returns the original response rather than processing the payment again. This ensures retries are safe and duplicate charges cannot occur.

---

## Design Decisions

### Template Changes
The provided template was used as a starting point, however several changes were made based on the requirements and business logic:

- **`cardNumberLastFour` renamed to `cardNumber`** - the request from the client contains the full card number, not the last 4 digits. The last 4 digits are derived and stored after processing. Renaming reflects the actual domain meaning of the field.
- **`cardNumber` type changed from `Integer` to `String`** - card numbers are identifiers not arithmetic values. Using `Integer` risks losing leading zeros and potential overflow. `String` is the semantically correct type.
- **`payments` type changed from `HashMap` to `ConcurrentHashMap`** - this allows the repository to store data in thread safe manner, which is instrumental for a payment gateway.
- **`cvv` type changed from `Integer` to `String`** - a CVV beginning with `0` such as `099` would silently become `99` as an `Integer`, which would cause bank authorisation failures. `String` preserves leading zeros.
- **`currency` type changed from `String` to `Currency` enum** - invalid currencies are rejected at deserialisation before reaching any validation logic, and eliminates string literals throughout the codebase.
- **`getExpiryDate()` removed from `PostPaymentRequest`** - this method formatted the expiry date for the bank simulator request, which is not the responsibility of a request DTO. It was moved to `BankRequestMapperImpl` as a private concern of the bank request mapping.
- **`PostPaymentResponse`  and `GetPaymentResponse` consolidated into `Payment`** - both models had identical fields. A single `Payment` model is used for both storage and responses. If future requirements introduced additional stored fields not needed in responses, this would be revisited and split accordingly.
- **`EventProcessingException` renamed to `PaymentNotFoundException`** - naming wise it's a lot more clearer and more domain aligned so helps in following DDD.

### Assumptions
The following assumptions not already mentioned were made:

- I assumed that the Year max is 20 years from the day that the payment request is taken, in reality this would need to be clarified when taking into account what are the acceptable ranges for real world use based on the desired markets.
- The requirements state the combination of expiry month + year is in the future, but cards that expire on the current month is still valid so have to follow that business logic, this would be a point that would be clarified during the requirements stage.

### Additional Classes
The following classes were added beyond the template:

- **`BankRequestMapper` / `BankRequestMapperImpl`** - maps `PostPaymentRequest` to `BankRequest`, keeping mapping logic out of the service layer
- **`PaymentMapper` / `PaymentMapperImpl`** - maps `PostPaymentRequest` and `PaymentStatus` to a `Payment` object for storage
- **`CreateExpiryDate`** - stateless utility for formatting the expiry date string sent to the bank simulator
- **`LastFourCardDigits`** - stateless utility for masking card numbers to last 4 digits, used consistently across mapping and logging
- **`PaymentProcessingError`** - dedicated error response for payment processing specific rejections, carrying both a `PaymentStatus` and a message, separate from the generic `ErrorResponse` used for non-payment errors like 404s
- **`Currency` enum** - replaces the `String` currency field across request and response models
