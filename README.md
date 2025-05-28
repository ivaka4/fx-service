# FX Exchange Service

A Java Spring Boot microservice for currency conversion, leveraging:

* **OpenAPI** (via OpenAPI Generator) for DTOs, controllers, and Swagger UI
* **MySQL** (via Spring Data JPA) for persistence
* **Redis** for in-memory rate caching
* **MapStruct** for entity/DTO mapping
* **Flyway** for database migrations
* **Docker Compose** for local development

---

## Prerequisites

* Java 17
* Maven 3.8+
* Docker & Docker Compose

---

## Quickstart

1. **Clone the repository**

   ```bash
   git clone <repo-url>
   cd fx-exchange
   ```

2. **Configure secrets**

   * Provide your [CurrencyLayer.com](https://currencylayer.com/) API key via environment variable

   ```yaml
     fx:
       provider:
         url: https://api.currencylayer.com/live
         api-key: #secret
         base-currency: USD
    ```
4. **Build the project**

    ```bash
    mvn clean package
    mvn clean install
    ```

3. **Run with Docker Compose**

   ```bash
   docker compose up --build
   ```

   * MySQL will be available on `jdbc:mysql://mysql:3306/fxdb`
   * Redis on `redis:6379`
   * The app on `http://localhost:8080`

4. **Access Swagger UI**
   Open [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui.html)

---

## Database Migrations

Flyway scans `src/main/resources/db/migration` for scripts named `V1__*.sql`, etc. On startup, it will automatically apply any pending migrations.

---

## API Reference

### GET `/rate?from={from}&to={to}`

Retrieve the current FX rate.

**Parameters**

* `from` (string, ISO currency code)
* `to` (string, ISO currency code)

**Response** 200 OK

```json
{
  "from": "USD",
  "to": "EUR",
  "rate": 0.91234
}
```

### POST `/convert`

Convert an amount.

**Request**

```json
{
  "from": "USD",
  "to": "EUR",
  "amount": 150.00
}
```

**Response** 200 OK

```json
{
  "transactionId": "c1a2b3d4-e5f6-7a8b-9c0d-e1f2a3b4c5d6",
  "from": "USD",
  "to": "EUR",
  "amount": 150.00,
  "convertedAmount": 136.85,
  "ratePrecision": 2.0861251081282175,
  "timestamp": "2025-05-28T16:00:00+03:00"
}
```

### GET `/history?page={page}&size={size}`

Get a page of conversion history.

**Parameters**

* `page` (integer, default 0)
* `size` (integer, default 20)

**Response** 200 OK

```json
{
  "amount": 150,
  "from": "USD",
  "to": "BGN",
  "convertedAmount": 258.627,
  "ratePrecision": 1.72418,
  "transactionId": "75d20f60-0c3d-4ac8-85a6-c63a02cf762e",
  "timestamp": "2025-05-27T22:18:34.200463Z"
    
}
```

---

## Exception Handling

* **400 Bad Request** for invalid input (e.g., negative amount, missing currency code)
* **404 Not Found** when a conversion by ID is not found
* **502 Bad Gateway** when the external FX provider fails
* **500 Internal Server Error** for unexpected server errors

The global `@ControllerAdvice` maps custom `ApiException` subclasses into structured JSON error responses:

```json
{
  "errorCode": "RATE_NOT_FOUND",
  "message": "Missing rate for USDGBP",
  "timestamp": "2025-05-28T16:05:00Z"
}
```

---

## Mapping

MapStruct is used to:

* Generate `ConversionEntity ↔ ConversionRequest/Response` mappers
* Auto-generate transaction IDs, timestamps, and mapping of rates

---

## Development Tips

* **Re-generate OpenAPI code** after editing `src/main/resources/api.yaml`:

  ```bash
  mvn clean install
  ```

* **Debugging**:
  Expose JDWP in Docker by adding `JAVA_TOOL_OPTIONS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005` to the `app` service.

---
