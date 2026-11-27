# Backend

This project uses Spring Boot version 4.1.3 and the Maven Wrapper.

## Prerequisites

Java JDK 25 is required. The Maven Wrapper downloads Maven and project dependencies on the first run.

## Development server

From the `backend` directory, run:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

The `dev` profile loads demonstration data and configures the temporary current accountant. Without `casalazi.dev.utilizator-curent-id`, startup fails intentionally until authentication is implemented.

The server starts at `http://127.0.0.1:8081` and listens only on the local loopback interface. Press `Ctrl+C` to stop it.

`GET /api/firme` returns the active companies of the current accountant’s cabinet, ordered by name. Requests to `/` still return HTTP 404. Example requests are available in `http/firme.http`.

## Project structure

Java sources are located in `src/main/java/ro/casalazi/`:

- `CasaLaZiApplication.java` starts the application.
- `firme/` contains the company entity, plus its repository, response DTO, listing service and REST controller.
- `cabinete/` contains the accounting office entity.
- `utilizatori/` contains the user entity, role enum, user repository and temporary current-user component.
- `util/` contains shared CUI and email normalization helpers.
- `registru/` contains register day and register row placeholders.
- `extragere/` contains photo extraction placeholders.

Each feature groups the placeholder classes currently defined for it in its own package.

The entities are mapped to the existing V1 schema. The company API supports listing, details, creation, editing, and archiving. The ANAF lookup can populate the form before creation.

## ANAF lookups

The lookup keeps an in-memory cache keyed by normalized CUI. Concurrent requests for the same CUI share one lookup. Entries refresh on demand after expiry. Only validated results and confirmed not-found responses are cached. Timeouts, HTML responses and other failures return HTTP 503.

| Spring property | Default |
| --- | --- |
| `casalazi.anaf.cache.ttl-gasit-secunde` | 900 seconds |
| `casalazi.anaf.cache.ttl-negasit-secunde` | 60 seconds |
| `casalazi.anaf.cache.capacitate` | 1,000 entries |

Expired entries are removed as needed. If the cache fills with fresh entries, additional lookups still return their results. The cache and rate limiter are local to each backend process. The backend waits at least one second after each ANAF call finishes before allowing another. A cache miss during that interval or during another CUI lookup returns HTTP 503 with `Retry-After: 1`. A cached response remains available during that interval.

## Building

To build the project, run:

```bash
./mvnw clean package
```

The executable JAR is stored in the `target/` directory.

## Running unit tests

From the `backend` directory, run:

```bash
./mvnw test
```

The unit tests run without Spring, Docker or external services. Current-user tests use a mocked repository to check the active accountant, role and cabinet. They cover CUI normalization and checksum validation, plus email normalization. Call `CuiUtil.normalizeaza` before `CuiUtil.esteValid`: the validator accepts only 2–10 ASCII digits with a non-zero first digit and a valid checksum. A valid checksum does not confirm that a company exists in ANAF. Email normalization does not validate the email format.

Company listing integration tests require Docker. Testcontainers starts a separate PostgreSQL 17 database and Flyway applies V1 there. The tests do not use or reset the development database. They verify cabinet isolation, archived-company exclusion, sorting, empty lists and response fields. The current accountant is mocked for these integration tests.

```bash
curl http://127.0.0.1:8081/api/firme
```

`GET /api/firme/{id}` returns an active company from the current cabinet. Missing, archived and other-cabinet companies all return the same 404 ProblemDetail. Invalid UUIDs return 400. Error details are in Romanian. Integration tests cover both endpoints and their error responses.

See `http/firme.http` for examples of all six endpoints. API exceptions are grouped by feature under `errors/`.
