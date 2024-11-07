# Mortgage Service API

## Description

The Mortgage Service API provides endpoints to fetch the current mortgage interest rates and to perform a mortgage feasibility check. The service calculates the feasibility and monthly costs of a mortgage based on specified business rules.

Key functionalities include:
* Retrieving the list of current mortgage rates.
* Checking if a mortgage is feasible based on the user's financial data.
* The application starts with predefined mortgage interest rates loaded into memory.
* An in-memory H2 database is used for persistence in tests.

## Business Rules
### Given requirements
1. A mortgage cannot exceed 4 times the applicant's income.
2. A mortgage cannot exceed the home value.

The feasibility check adheres to these rules and calculates monthly costs based on the interest rate and loan maturity period.

### Implementation decisions
I tried to stick to the requirements, based on expected functionality and expected development time. Based on that, I decided:
* System will store only one Mortgage Rate for each maturity period.
  * Maturity period is an unique field in mortgage_rates database table
* When checking a mortgage feasibility, a mortgage rate for a given maturity period must exist in the system.

## Main Features
* **Spring Boot 3**
* **Java 17**
* **JPA/Hibernate and H2**
* **Flyway** for managing database migrations
* **Hexagonal architecture**
* **Testing**
  * Unit tests
  * Integration tests using the H2 database to validate API functionality
* **OpenAPI** integration for endpoint documentation
* **In-memory initialization** of mortgage rates at startup

### Framework decision
1. H2 in-memory relational database
* Requirements expected the mortgage rates to be created in-memory on application startup.
* Mortgage rate structure is mostly fixed, not expecting a wide variety of different interest rates to manage.

2. Flyway
* Flyway is responsible of managing database versioning. Even for an MVP, database evolution should be controlled. 

## Improvements suggestions

While I tried to stick to the requirements, I wanted to define some improvements I was tempted to implement.

### Business Logic
* Add mortgage rates start and end dates
  * Handle dates collision with a priority field, being top priority the smallest one
* Allow mortgage feasibility check as a calculator, without needing a related mortgage rate in the system
  * Add interest rate param to the /mortgage-check endpoint, to allow calculation of mortgage monthly cost, even without having a related mortgage rate in the system.

### Technical improvements
* Change in-memory database for PostgreSQL as a production-ready solution
  * Use TestContainers for integration tests
## Requirements

- JDK 17
- Maven 3.8.X

## Steps to Test

### 1. Clone the Repository

Clone the repository using the following command:

```bash
git clone https://github.com/miguemiguemigue/mortgage-service.git
cd mortgage-service
```

### 2. Compile the Project

Run the following command to compile the project:

```bash
mvn clean install
```

### 3. Run Tests

Run all tests using the following command:

```bash
mvn test
```

### 4. Run the Application

To start the application, run:

```bash
mvn spring-boot:run
```

### 5. Initialize the Database

Flyway is configured to initialize the H2 database with test data on startup, if necessary.

---

