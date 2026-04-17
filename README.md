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

### Part 1.1 — JAX-RS Resource Lifecycle
By default, JAX-RS creates a **new instance of a resource class for every incoming HTTP request** (per-request lifecycle). This means each request gets its own fresh object, which avoids concurrency issues at the instance level. However, since we are using **static HashMaps** in our `DataStore` class as our in-memory data store, these maps are shared across all requests. This means concurrent requests could cause race conditions (e.g., two requests modifying the sensors map at the same time). In a production system, we would use `ConcurrentHashMap` or `synchronized` blocks to prevent data corruption. For this coursework, since it is a single-user demonstration, static HashMaps are sufficient.

### Part 1.2 — HATEOAS
HATEOAS (Hypermedia as the Engine of Application State) means that API responses include **links** to related resources, guiding the client on what actions are available next — similar to how a web browser follows links on a page. For example, a response returning a room could also include links like `"sensors": "/api/v1/rooms/LIB-301/sensors"`. This benefits client developers because they do not need to hardcode URLs or read extensive documentation — the API itself tells them what they can do next. It makes APIs self-discoverable, reduces coupling between client and server, and allows the server to change URLs without breaking clients.

### Part 2.1 — IDs vs Full Objects in List Responses
Returning only **IDs** in a list response reduces bandwidth significantly, especially when there are thousands of rooms. The client then makes individual requests for the details it needs. However, this causes the "N+1 problem" — if a client needs details on all rooms, it must make one request per room. Returning **full objects** is more convenient for the client and reduces round trips, but increases payload size and server load. The best practice for large APIs is to return full objects by default but support query parameters like `?fields=id,name` to allow clients to request only the fields they need.

### Part 2.2 — Is DELETE Idempotent?
Yes, DELETE is **idempotent** in our implementation. Idempotent means that making the same request multiple times produces the same result as making it once. In our API, the first DELETE on a room removes it from the map. Any subsequent DELETE for the same room ID will throw a `DataNotFoundException` and return a `404 Not Found`. The **server state remains the same** — the room is still gone — so the operation is idempotent. The response code may differ (204 vs 404), but the resource state does not change after the first deletion.

### Part 3.1 — @Consumes and Content-Type Mismatch
The `@Consumes(MediaType.APPLICATION_JSON)` annotation tells JAX-RS that the endpoint only accepts JSON-formatted request bodies. If a client sends data as `text/plain` or `application/xml`, JAX-RS will automatically return a **415 Unsupported Media Type** error without even invoking the resource method. This protects the API from malformed input and ensures that Jackson can safely deserialize the incoming data into the correct Java object. The developer does not need to write any manual content-type checking code.

### Part 3.2 — @QueryParam vs Path-based Filtering
Using `@QueryParam` for filtering (e.g., `/sensors?type=CO2`) is considered superior because query parameters are semantically designed for **optional, non-hierarchical filtering**. The path `/sensors` still represents the full collection, and the query parameter narrows it down. In contrast, using `/sensors/type/CO2` implies that `type` and `CO2` are part of the resource hierarchy, which is misleading. Query parameters are also easier to combine (e.g., `?type=CO2&status=ACTIVE`), easier to make optional, and are correctly ignored by caching systems that cache the base resource.

### Part 4.1 — Sub-Resource Locator Pattern
The Sub-Resource Locator pattern allows a resource method to **delegate** to a separate class instead of handling all nested paths in one file. In our API, `SensorResource` delegates `/sensors/{sensorId}/readings` to a dedicated `SensorReadingResource` class. This improves **separation of concerns** — each class has one responsibility. It also improves readability and maintainability; in large APIs with many nested paths, putting everything in one class creates a massive, unmanageable controller. Sub-resources also allow for **context injection** — the parent can pass the `sensorId` to the sub-resource so it always operates in the correct context.

### Part 5.2 — Why 422 is More Accurate than 404
A `404 Not Found` means **the requested URL/resource does not exist**. However, when a client POSTs a valid sensor with a `roomId` that does not exist, the URL `/api/v1/sensors` is perfectly valid — the problem is inside the **request body**. HTTP `422 Unprocessable Entity` means the server understood the request format but could not process the **semantic content** of it. It tells the client clearly that the issue is a broken reference inside their JSON payload, not a missing URL. This is more precise and helps client developers debug faster.

### Part 5.4 — Security Risks of Exposing Stack Traces
Exposing Java stack traces to external API consumers is a serious security risk. Stack traces reveal: the **internal package and class structure** of the application (helping attackers map the codebase), the **framework and library versions** in use (helping attackers find known CVEs), the **exact line numbers** of errors (helping pinpoint where to inject attacks), and sometimes **database query details or file paths** (directly exposing infrastructure). A professional API should always return a generic `500 Internal Server Error` message with no internal details, logging the full trace only to a secure server-side log.

### Part 5.5 — Why Use Filters Instead of Manual Logging
JAX-RS filters implement **cross-cutting concerns** — functionality needed by every endpoint — in one centralized place. If we manually inserted `Logger.info()` into every resource method, we would violate the **DRY (Don't Repeat Yourself)** principle. Adding or changing logging behaviour would require modifying every resource class. Filters are automatically applied to **every request and response** without touching resource code. They also run at the framework level, capturing data even when exceptions are thrown, which manual in-method logging might miss. This makes the codebase cleaner, more maintainable, and easier to extend.
