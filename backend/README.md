# Backend

This project uses Spring Boot version 4.1.3 and the Maven Wrapper.

## Prerequisites

Java JDK 25 is required. The Maven Wrapper downloads Maven and project dependencies on the first run.

## Development server

From the `backend` directory, run:

```bash
./mvnw spring-boot:run
```

The server starts at `http://127.0.0.1:8081` and listens only on the local loopback interface. Press `Ctrl+C` to stop it.

No API endpoints are implemented yet. Browser requests return HTTP 404.

## Project structure

Java sources are located in `src/main/java/ro/casalazi/`:

- `CasaLaZiApplication.java` starts the application.
- `firme/` contains company placeholders (entity, DTO, controller, service, repository).
- `registru/` contains register day and register row placeholders.
- `extragere/` contains photo extraction placeholders.

Each feature groups the placeholder classes currently defined for it in its own package.

All placeholder classes are empty. Database access, authentication, AI integration and frontend integration are not implemented.

## Building

To build the project, run:

```bash
./mvnw clean package
```

The executable JAR is stored in the `target/` directory.
