# UAE Travel Card Management System

## Project Overview
The UAE Travel Card Management System is a Spring Boot-based backend service designed to handle the core operations of a public transit smart card (similar to a Nol card). It provides a RESTful API for frontend applications or physical turnstiles to register cards, add funds, check balances, and process journey check-ins and check-outs, calculating the appropriate fare based on predefined business rules.

## Core Features
*   **Card Management:** Register new single cards or perform batch registrations.
*   **Balance Management:** Recharge cards and retrieve current balances.
*   **Journey Processing (Swipe):** A unified endpoint to handle both check-ins (starting a journey) and check-outs (ending a journey).
*   **Dynamic Fare Calculation:** A robust rules engine that calculates fares based on the transport type (Bus/Train) and the zones travelled between.
*   **Data Persistence:** Utilizes an H2 in-memory database with Spring Data JPA for storing card details and station configurations.
*   **Containerization:** Fully containerized with a multi-stage Dockerfile for easy and consistent deployment.
*   **Cross-Origin Resource Sharing (CORS):** Fully configured to accept requests from frontend applications hosted on different domains.

## Technologies Used
*   **Java 8**
*   **Spring Boot (2.6.2):** Core framework for dependency injection, embedded server (Tomcat), and auto-configuration.
*   **Spring Web:** For building RESTful APIs.
*   **Spring Data JPA:** For database interactions and ORM.
*   **H2 Database:** In-memory relational database for data persistence.
*   **Docker:** For containerization and deployment.
*   **Lombok:** To reduce boilerplate code (Getters, Setters, Constructors).
*   **Maven:** Project build and dependency management.

---

## API Endpoints

The API is accessible at the base URL: `http://localhost:8080/api`

### 1. Health Check
*   **GET** `/card/ping`
*   **Description:** Verifies the service is running.
*   **Response:** `Service is UP and Running` (String)

### 2. Card Management
*   **Register Single Card**
    *   **POST** `/card/register`
    *   **Body:** `{"cardNumber": "CARD123", "balance": 50.0}`
    *   **Response:** `TravelCardResponse` object.
*   **Register Multiple Cards (Batch)**
    *   **POST** `/card/register/batch`
    *   **Body:** `[{"cardNumber": "CARD1", "balance": 20.0}, {"cardNumber": "CARD2", "balance": 10.0}]`
    *   **Response:** List of `TravelCardResponse` objects.
*   **Get All Cards**
    *   **GET** `/card`
    *   **Response:** List of card numbers (Strings).
*   **Get Card Details**
    *   **GET** `/card/{cardNumber}`
    *   **Response:** `TravelCardResponse` object containing balance and transit status.
*   **Recharge Card**
    *   **POST** `/card/recharge/{rechargeAmount}`
    *   **Headers:** `Content-Type: text/plain`
    *   **Body:** Raw string of the `cardNumber` (e.g., `CARD123`)
    *   **Response:** Updated `TravelCardResponse` object.

### 3. Journey Processing
*   **Swipe Card (Check-In / Check-Out)**
    *   **POST** `/card/swipe`
    *   **Body:** `{"cardNumber": "CARD123", "stationName": "Union", "transportType": "TRAIN"}`
    *   **Description:** If the card is not in transit, this deducts the maximum fare and marks the card as in transit (Check-In). If the card is already in transit, this calculates the actual fare, refunds the difference from the max fare, and ends the journey (Check-Out).
    *   **Response:** Updated `TravelCardResponse` object.

### 4. System Data
*   **Get Stations & Zones**
    *   **GET** `/stations-zones`
    *   **Response:** List of stations and the zones they belong to.

---

## Application Architecture & Class Specifications

### Entities (`com.demo.travelcardsystem.entity`)
*   **`TravelCard`**: A JPA `@Entity` representing a physical travel card. Stores the `cardNumber` (Primary Key), `balance`, `inTransit` boolean flag, and transient details of the current journey (`startStation`, `transportType`).
*   **`Station`**: A JPA `@Entity` representing a transit station. Stores the station `name` (Primary Key) and a collection of `Zone` enums it belongs to.
*   **`Journey`**: A POJO used by the service layer to encapsulate details of a completed or ongoing trip (start/end stations, transport type) for fare calculation.
*   **`ZonePair`**: A POJO used within business rules to define a valid start and end zone combination for a specific fare.

### Repositories (`com.demo.travelcardsystem.repository`)
*   **`TravelCardRepository`**: Spring Data `JpaRepository` interface for CRUD operations on `TravelCard` entities.
*   **`StationRepository`**: Spring Data `JpaRepository` interface for CRUD operations on `Station` entities.

### Services (`com.demo.travelcardsystem.service`)
*   **`TravellerService`**: The core service layer handling all business logic. It validates requests, interacts with the repositories, orchestrates the check-in/check-out flow, and invokes the `FareCalculator`.
*   **`FareCalculator`**: A utility component that takes a `Journey` object, evaluates it against the loaded `RuleCollection`, and determines the lowest applicable fare.
*   **`TravelCardConverter`**: Contains mapping functions (using `java.util.function.Function`) to transform `TravelCard` entities into `TravelCardResponse` DTOs to avoid exposing database models to the client.

### Controllers (`com.demo.travelcardsystem.controller`)
*   **`TravellerController`**: Exposes the REST API endpoints and routes HTTP requests to the `TravellerService`.

### Business Rules Engine (`com.demo.travelcardsystem.businessrule`)
*   **`Rule`**: Represents a specific pricing condition. Contains the applicable `TransportType`, a specific `chargeableFare`, and a set of valid `ZonePair`s.
*   **`RuleCollection`**: A Singleton bean holding all active `Rule` objects and defining the system's `maxFare`.
*   **`TravelStrategy`**: Contains Java Functional interfaces (`Consumer`, `BiConsumer`) acting as strategy patterns to define how rules are created (e.g., `anyWhereInZoneOneStrategy`, `anyJourneyByBus`). It populates the `RuleCollection` on startup.

---

## Database Schema (H2)

The application utilizes Spring Data JPA with `ddl-auto=update` to automatically generate the following schema:

*   **`travel_card` Table:**
    *   `card_number` (VARCHAR, Primary Key)
    *   `balance` (DOUBLE)
    *   `in_transit` (BOOLEAN)
    *   `start_station_id` (VARCHAR, Foreign Key to `station.name`)
    *   `transport_type` (VARCHAR)
*   **`station` Table:**
    *   `name` (VARCHAR, Primary Key)
*   **`station_zones` Table (Element Collection):**
    *   `station_name` (VARCHAR, Foreign Key to `station.name`)
    *   `zone` (VARCHAR)

---

## Business Logic: Fare Calculation

Fares are calculated dynamically based on predefined strategies loaded at application startup (`TravelcardsystemApplication.java`).

**The Swipe Mechanism:**
1.  **Check-In:** When a user swipes an inactive card, the system immediately deducts the `maxFare` (e.g., 3.20 AED) to ensure they have enough funds to complete any journey. The card is marked as `inTransit`.
2.  **Check-Out:** When the user swipes an active card at their destination, the system creates a `Journey` object.
3.  **Calculation:** The `FareCalculator` filters all rules in the `RuleCollection` to find those where the journey matches the rule's `ZonePair` or `TransportType`.
4.  **Resolution:** If multiple rules apply, the system selects the one with the lowest fare (benefiting the customer).
5.  **Settlement:** The system refunds the initial `maxFare` deduction and then debits the actual calculated fare.

**Current Pre-configured Fares:**
*   Any journey by Bus: 1.80 AED
*   Anywhere in Zone 1: 2.50 AED
*   Any single zone outside Zone 1: 2.00 AED
*   Any two zones excluding Zone 1: 2.25 AED
*   Any two zones including Zone 1: 3.00 AED
*   Any three zones (Max Fare): 3.20 AED

---

## Configuration

The application is configured via `src/main/resources/application.properties`.

Notable configurations:
*   `spring.datasource.url=jdbc:h2:mem:travelcarddb;DATABASE_TO_UPPER=false`: Configures the in-memory database and ensures H2 does not force uppercase table names, preventing conflicts with Hibernate's naming strategies.
*   `spring.h2.console.enabled=true`: Enables the H2 web console (accessible at `/h2-console`).

---

## How to Run

### Locally with Maven
1.  Ensure you have Java 8 and Maven installed.
2.  Navigate to the root directory of the project (`UAE-Travel-Card-Management-System_Group9/card-system-service`).
3.  Execute the following Maven command:
    ```bash
    ./mvnw spring-boot:run
    ```
4.  The application will start on port 8080.

### Using Docker
1.  Ensure you have Docker installed and running.
2.  Navigate to the root directory of the project (`UAE-Travel-Card-Management-System_Group9/card-system-service`).
3.  **Build the Docker image:**
    ```bash
    docker build -t travel-card-system .
    ```
4.  **Run the Docker container:**
    ```bash
    docker run -p 8080:8080 travel-card-system
    ```
5.  The application will be accessible at `http://localhost:8080`.

Initial data (Stations and two test cards: `A101` and `B201`) is automatically loaded on startup in both run modes.