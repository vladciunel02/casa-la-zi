# Backend

This project uses Spring Boot version 3.5.16 and the Maven Wrapper.

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
- `controller/` contains controller placeholders.
- `service/` contains business logic placeholders.
- `repository/` contains data access placeholders.
- `models/` contains register day, register row and company placeholders.
- `dtos/` contains data transfer object placeholders.

All placeholder classes are empty. Database access, authentication, AI integration and frontend integration are not implemented.

## Building

To build the project, run:

```bash
./mvnw clean package
```

The executable JAR is stored in the `target/` directory.
