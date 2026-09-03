# Hotel Availability Search Service

REST API for creating hotel availability searches and counting equivalent searches.

Built with Java 21, Spring Boot, Kafka and PostgreSQL.

## Requirements

The application is fully dockerized. The only requirements are:

- Docker
- Docker Compose

## Running the application

From the project root, run:

```bash
docker compose up --build -d
```

This starts the application, Kafka and PostgreSQL.

Check the running containers:

```bash
docker compose ps
```

To stop the application:

```bash
docker compose down
```

To stop the application and remove persisted Docker volumes:

```bash
docker compose down -v
```

## Swagger

Once the application is running, Swagger UI is available at:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI specification:

```text
http://localhost:8080/v3/api-docs
```

## API Endpoints

### POST /search

Creates a new hotel availability search.

Example request:

```bash
curl -X POST http://localhost:8080/search \
  -H "Content-Type: application/json" \
  -d '{
    "hotelId": "1234aBc",
    "checkIn": "29/12/2026",
    "checkOut": "31/12/2026",
    "ages": [30, 29, 1, 3]
  }'
```

Example response:

```json
{
  "searchId": "550e8400-e29b-41d4-a716-446655440000"
}
```

The endpoint returns `202 Accepted`. The search is published to the Kafka topic `hotel_availability_searches` and persisted asynchronously in PostgreSQL.

### GET /count

Returns the number of equivalent searches for a previously generated `searchId`.

```bash
curl "http://localhost:8080/count?searchId=550e8400-e29b-41d4-a716-446655440000"
```

Example response:

```json
{
  "searchId": "550e8400-e29b-41d4-a716-446655440000",
  "search": {
    "hotelId": "1234aBc",
    "checkIn": "29/12/2026",
    "checkOut": "31/12/2026",
    "ages": [30, 29, 1, 3]
  },
  "count": 2
}
```

The order of `ages` is significant when comparing searches.

For example:

```text
[30, 29, 1, 3] != [3, 29, 30, 1]
```

> Since persistence is asynchronous through Kafka, a `/count` request executed immediately after `/search` may not find the search until the Kafka message has been consumed and persisted.

## Tests and coverage

Run the complete test suite with:

```bash
./mvnw clean verify
```

The project uses JUnit 5, Mockito and Testcontainers for testing.

JaCoCo is configured with a minimum coverage threshold of 80%.

The coverage report is generated at:

```text
target/site/jacoco/index.html
```

## Technical notes

- Java 21 and Spring Boot 4.
- Hexagonal Architecture separates application/domain logic from REST, Kafka and persistence infrastructure.
- Search identifiers are generated using UUIDs without database access.
- Kafka producer and consumer responsibilities are separated.
- PostgreSQL `INTEGER[]` is used to preserve the order of guest ages.
- Kafka consumer processing uses Java Virtual Threads for the blocking database persistence path.
- Request models are immutable and validated before processing.