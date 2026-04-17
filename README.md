# Smart Campus Sensor & Room Management API

A RESTful API built with JAX-RS (Jersey) for managing campus Rooms and Sensors as part of the University of Westminster's Smart Campus initiative.

---

## Student Information

| Field | Details |
|-------|---------|
| **Name** | Abinash Ananth |
| **IIT ID** | 20231770 |
| **UOW ID** | w2120654 |
| **Module** | 5COSC022W — Client-Server Architectures |
| **University** | University of Westminster |

---

## API Overview

This API manages three core resources:
- **Rooms** — Physical spaces on campus (labs, halls, libraries)
- **Sensors** — Hardware devices deployed in rooms (temperature, CO2, occupancy)
- **SensorReadings** — Historical measurement logs for each sensor

Base URL: `http://localhost:8080/SmartCampusAPI/api/v1`

---

## Project Structure

```
SmartCampusAPI/
├── src/main/java/com/smartcampus/
│   ├── model/
│   │   ├── Room.java
│   │   ├── Sensor.java
│   │   ├── SensorReading.java
│   │   └── ErrorMessage.java
│   ├── dao/
│   │   └── DataStore.java
│   ├── resource/
│   │   ├── DiscoveryResource.java
│   │   ├── RoomResource.java
│   │   ├── SensorResource.java
│   │   └── SensorReadingResource.java
│   ├── exception/
│   │   ├── DataNotFoundException.java
│   │   ├── DataNotFoundExceptionMapper.java
│   │   ├── RoomNotEmptyException.java
│   │   ├── RoomNotEmptyExceptionMapper.java
│   │   ├── LinkedResourceNotFoundException.java
│   │   ├── LinkedResourceNotFoundExceptionMapper.java
│   │   ├── SensorUnavailableException.java
│   │   ├── SensorUnavailableExceptionMapper.java
│   │   └── GlobalExceptionMapper.java
│   └── filter/
│       └── LoggingFilter.java
├── src/main/webapp/WEB-INF/
│   └── web.xml
└── pom.xml
```

---

## How to Build and Run

### Prerequisites
- Java JDK 8 or higher
- Apache Maven 3.6+
- Apache Tomcat 9.x

### Steps

1. **Clone the repository**
```bash
git clone https://github.com/Ananth-Abi/SmartCampusAPI_CSA.git
cd SmartCampusAPI_CSA
```

2. **Build the project**
```bash
mvn clean install
```

3. **Deploy to Tomcat**
   - Copy `target/SmartCampusAPI-1.0-SNAPSHOT.war` to Tomcat's `webapps/` folder
   - Or open in NetBeans and click Run

4. **Access the API**
```
http://localhost:8080/SmartCampusAPI/api/v1
```

---

## API Endpoints

### Discovery
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1` | API metadata and resource links |

### Rooms
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/rooms` | Get all rooms |
| GET | `/api/v1/rooms/{roomId}` | Get room by ID |
| POST | `/api/v1/rooms` | Create a new room |
| DELETE | `/api/v1/rooms/{roomId}` | Delete a room (blocked if sensors exist) |

### Sensors
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/sensors` | Get all sensors |
| GET | `/api/v1/sensors?type=CO2` | Filter sensors by type |
| GET | `/api/v1/sensors/{sensorId}` | Get sensor by ID |
| POST | `/api/v1/sensors` | Register a new sensor |
| DELETE | `/api/v1/sensors/{sensorId}` | Delete a sensor |

### Sensor Readings
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/sensors/{sensorId}/readings` | Get all readings for a sensor |
| POST | `/api/v1/sensors/{sensorId}/readings` | Add a new reading |

---

## Sample curl Commands

### 1. Get API Discovery Info
```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1 -H "Accept: application/json"
```

### 2. Get All Rooms
```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/rooms -H "Accept: application/json"
```

### 3. Create a New Room
```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/rooms -H "Content-Type: application/json" -d "{\"id\":\"ROOM-NEW\",\"name\":\"New Lecture Hall\",\"capacity\":100}"
```

### 4. Register a New Sensor
```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors -H "Content-Type: application/json" -d "{\"id\":\"TEMP-999\",\"type\":\"Temperature\",\"status\":\"ACTIVE\",\"currentValue\":20.0,\"roomId\":\"LIB-301\"}"
```

### 5. Get Filtered Sensors by Type
```bash
curl -X GET "http://localhost:8080/SmartCampusAPI/api/v1/sensors?type=CO2" -H "Accept: application/json"
```

### 6. Add a Sensor Reading
```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors/TEMP-001/readings -H "Content-Type: application/json" -d "{\"value\":24.5}"
```

### 7. Delete a Room with Sensors (expect 409 error)
```bash
curl -X DELETE http://localhost:8080/SmartCampusAPI/api/v1/rooms/LIB-301 -H "Accept: application/json"
```

---

## Error Handling

| HTTP Status | Exception | Scenario |
|-------------|-----------|----------|
| 404 Not Found | `DataNotFoundException` | Room or Sensor ID does not exist |
| 409 Conflict | `RoomNotEmptyException` | Deleting a room that still has sensors |
| 422 Unprocessable Entity | `LinkedResourceNotFoundException` | Sensor references a non-existent room |
| 403 Forbidden | `SensorUnavailableException` | Posting reading to a MAINTENANCE sensor |
| 500 Internal Server Error | `GlobalExceptionMapper` | Any unexpected runtime error |

---

## Report: Answers to Coursework Questions

### Part 1.1 — JAX-RS Resource Class Lifecycle (Request-Scoped vs Singleton)

JAX-RS uses a **request-scoped lifecycle** by default — a brand new instance of each resource class such as `RoomResource` and `SensorResource` is created per HTTP request and discarded after the response is sent. This is the opposite of a **singleton lifecycle** where one shared instance handles all requests.

Because instances are not shared, instance variables inside resource classes cannot hold data across requests. To address this, a static `DataStore` class was implemented to store all rooms, sensors and readings in `HashMap` collections that persist for the full application lifetime and are shared across all request-scoped instances.

However this introduces a **concurrency risk** — two simultaneous requests modifying the same `HashMap` could cause data corruption. In production, `HashMap` would be replaced with `ConcurrentHashMap` for thread-safe operations, or `synchronized` blocks would protect critical write sections to prevent race conditions and data loss.

---

### Part 1.2 — HATEOAS and Self-Documenting APIs

HATEOAS (Hypermedia as the Engine of Application State) makes APIs **self-navigating** by embedding navigation links directly inside responses rather than relying on static external documentation. Clients discover what actions are available next by following links in the response itself.

The `GET /api/v1` Discovery endpoint in this project demonstrates this by returning a resource map pointing to `/api/v1/rooms` and `/api/v1/sensors`, guiding clients to available collections without prior knowledge of the URL structure.

This approach provides three key benefits over static documentation: it **reduces client-server coupling** — clients following embedded links adapt automatically if URLs change; it **accelerates development** — developers explore the API without reading lengthy docs; and it **future-proofs** the API, allowing the server to evolve without breaking existing consumers.

---

### Part 2.1 — Returning IDs vs Full Objects in List Responses

Returning only **IDs** produces a lightweight response but introduces the **N+1 problem** — a client needing full details for every room must fire one additional HTTP request per ID, dramatically increasing latency and server load.

Returning **full objects** eliminates extra round trips at the cost of a larger initial payload. For the Smart Campus system where facility managers need complete room details immediately, returning full objects is the more practical choice — the bandwidth overhead is justified by the reduction in total HTTP calls.

For very large datasets, combining full objects with **pagination** (`?page=1&size=20`) or **sparse fieldsets** (`?fields=id,name`) would provide the best balance between bandwidth efficiency and usability.

---

### Part 2.2 — Is DELETE Idempotent?

Yes, DELETE is **idempotent** in this implementation — repeating the same request produces the same server state as calling it once.

Tracing exact server state across multiple calls: the first `DELETE /api/v1/rooms/LIB-301` on a room with no sensors removes it from the `DataStore` and returns `204 No Content`. Any subsequent identical request finds nothing to delete, throws a `DataNotFoundException` and returns `404 Not Found`. Critically, **the server state does not change** after the first call — the room remains absent regardless of how many times the request is repeated.

This satisfies idempotency even though status codes differ (204 vs 404), because idempotency refers to **server state consistency**, not response code uniformity — which is consistent with the HTTP specification.

---

### Part 3.1 — Technical Consequences of @Consumes Mismatch

`@Consumes(MediaType.APPLICATION_JSON)` is a **content negotiation directive** that instructs JAX-RS to only accept requests where the `Content-Type` header is `application/json`. This check happens at the framework level before the resource method is ever invoked.

If a client sends `text/plain` or `application/xml`, Jersey automatically returns **HTTP 415 Unsupported Media Type** and the method is never called — no partial processing occurs and no invalid data enters the application. This prevents Jackson from receiving unparseable data and removes the need for manual content type validation inside every resource method, keeping the API boundary clean and enforced automatically.

---

### Part 3.2 — @QueryParam vs Path Parameter for Filtering

`@QueryParam` filtering (`GET /api/v1/sensors?type=CO2`) is architecturally superior to path-based filtering (`GET /api/v1/sensors/type/CO2`) for three reasons.

First, query parameters are **optional** — `GET /api/v1/sensors` returns all sensors while `?type=CO2` narrows the result, meaning one endpoint serves both cases without separate route definitions. Second, path parameters are semantically intended to **identify a specific resource**, not filter a collection — embedding a filter in the path is semantically incorrect. Third, query parameters are **composable** — multiple filters combine naturally such as `?type=CO2&status=ACTIVE` without additional routing. HTTP caching also correctly treats the base path as the cacheable resource with query parameters as optional modifiers.

---

### Part 4.1 — Architectural Benefits of the Sub-Resource Locator Pattern

The Sub-Resource Locator pattern allows a resource method to return an instance of another class rather than handling the request directly. In this API, `SensorResource` contains a locator method annotated with `@Path("/{sensorId}/readings")` that returns a new `SensorReadingResource` instance, passing the `sensorId` as constructor context.

This achieves **separation of concerns** — `SensorResource` handles only sensor operations while `SensorReadingResource` handles only reading operations, following the **Single Responsibility Principle**. A monolithic controller handling every nested path would grow unmanageable as the API scales and any change risks breaking unrelated functionality.

The pattern also enables **context injection** — the parent passes `sensorId` at construction time so `SensorReadingResource` always operates in the correct sensor context without performing its own lookups, producing a codebase that is cleaner, easier to test and straightforward to extend.

---

### Part 5.2 — Why HTTP 422 is More Semantically Accurate than 404

**HTTP 404 Not Found** signals that the target URL does not exist on the server. **HTTP 422 Unprocessable Entity** signals that the URL is valid and the JSON is well-formed, but the server cannot process the request due to a **semantic error inside the payload**.

When a client posts a new sensor with a `roomId` referencing a room that does not exist in the `DataStore`, the endpoint `POST /api/v1/sensors` is fully operational — the problem lies entirely within the request body where `roomId` creates a broken reference. Returning 404 would mislead the client into thinking the endpoint is missing. Returning 422 precisely communicates that the endpoint is valid but the payload contains a logical error, helping developers identify and fix the actual problem — the invalid `roomId` — immediately.

---

### Part 5.4 — Cybersecurity Risks of Exposing Stack Traces

Exposing Java stack traces to external consumers is a serious **information disclosure vulnerability** for four reasons.

**Architecture exposure** — reveals internal package names and class hierarchy, helping attackers map the codebase structure. **Library fingerprinting** — exposes framework and library version numbers, allowing attackers to find known CVEs for those exact versions. **Error location precision** — line numbers pinpoint exactly where errors occur, helping craft targeted injection attacks. **Infrastructure leakage** — may expose database queries, file paths or server hostnames revealing underlying infrastructure.

The `GlobalExceptionMapper<Throwable>` in this project resolves this by intercepting all unhandled exceptions including `NullPointerException` and `IndexOutOfBoundsException`, returning only a generic 500 message to the client while writing full stack trace details exclusively to the server-side Tomcat log.

---

### Part 5.5 — Why JAX-RS Filters Are Superior to Manual Logging

Inserting `Logger.info()` manually into every resource method violates the **DRY (Don't Repeat Yourself)** principle and creates a maintenance burden that grows with every new endpoint added to the API.

The `LoggingFilter` in this project implements both `ContainerRequestFilter` and `ContainerResponseFilter`, registering once with `@Provider` so the Jersey runtime automatically applies it to every HTTP interaction — logging the method, URI and response status code universally without any resource method needing to participate.

Three key advantages: **consistency** — no risk of missing a newly added endpoint; **centralised maintenance** — one class to update if log format changes; **framework-level coverage** — filters capture events even when methods are never reached, such as when a 415 rejection occurs before the method is invoked, which manual in-method logging would completely miss. This approach mirrors **Aspect-Oriented Programming**, keeping cross-cutting concerns entirely separate from core business logic.
