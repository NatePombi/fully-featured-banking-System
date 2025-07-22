# Fully Featured Banking System

A robust and scalable console-based banking system built with Java 17. It features secure authentication, modular service design, real database persistence (MySQL), and automated CI + test coverage reporting. Designed from the ground up to follow clean code, SOLID principles, and real-world enterprise patterns.

## Overview

This project simulates a simple yet complete banking system supporting multiple users, each managing multiple bank accounts. Features include:

    Secure login & registration with password hashing
    Deposits, withdrawals, and transfers with validations
    Real-time transaction history (persisted in MySQL)
    Robust service + repository layers with test coverage
    CI-ready with code coverage (JaCoCo)

## Features

- User Authentication
  - Registration and login system
  - Password hashing with salt for security

- Account Management
  - Support for multiple accounts per user
  - Account type, ID, and balance stored in the database

- Transaction Management
  - Deposit, withdraw, and transfer funds
  - Transaction history with timestamps


- Logging
   - Uses Logback (via SLF4J) for logging warnings, errors, and system activity
   - Configured with custom logback.xml

- Connection Pooling
   - Uses HikariCP for high-performance JDBC connection pooling
   - Config loaded from external db.properties

- Manual Transactions
   - Explicit transaction commit and rollback for data consistency

- Testing
  - Unit tests for utilities, services, and logic
  - Integration tests for real DB interactions (test schema)
  - UI logic tested independently
  - CI pipeline runs on push via GitHub Actions
  - Code coverage powered by JaCoCo

- Modular Design
  - Uses interfaces and separation of concerns
  - Follows SOLID principles

- Database Integration
  - JDBC-based connection to MySQL
  - Data persistence for users, accounts, and transactions

## System Architecture

  - Clean separation between UI, service, model, and persistence
  - Interfaces & dependency injection for testability
  - Connection pooling with HikariCP
  - Manual JDBC transactions (commit/rollback) for consistency


## Technologies Used

- Java 17
- JDBC
- MySQL
- HikariCP
- Logback + SLF4J
- JUnit 5 & Mockito (testing)
- Maven (build & dependency management)
- JaCoCo (code coverage)
- GitHub Actions (CI)



----


## Setting Up MySQL

1. git clone https://github.com/NatePombi/fully-featured-banking-System.git

2. Install MySQL and create a banking database.
    Add your credentials in db.properties:
    Configure db.properties:
        db.url=jdbc:mysql://localhost:3306/banking  -> add your own configurations
        db.name=root
        db.password=yourpassword


3. Run the app — it will auto-create necessary tables.

        Build and run via Maven or your IDE.
        Launch Main class from app/ package.
        Interact through the console UI.


## Running the Application
    Compile and run the Main class from the app package.
    Follow on-screen prompts in the console UI to register, login, and manage accounts.

## Testing

    Unit & Integration Tests:
    copy this into the terminal: mvn clean verify
        Unit tests cover services, logic, and utilities
        Integration tests hit the real DB (with test tables)

      -Coverage Report:
        target/site/jacoco/index.html

## Continuous Integration
    This project includes automated CI via GitHub Actions:
        Tests run on every push
        Coverage checked with JaCoCo
        Easy integration into future pipelines (e.g., deployment)

## Future Work
    Migrate to a JavaFX or React frontend
    Add new account types (e.g., credit, joint accounts)
    Enhance security measures (e.g., multi-factor authentication).

## Author
    Nathan Pombi
    Java Developer | Passionate about Clean Code & Real-World Applications
    GitHub: https://github.com/NatePombi
    LinkedIn: https://www.linkedin.com/in/nathanpombi/




## Recruiter Notes
     Built entirely in Java 17, JDBC, MySQL, JUnit, and Mockito
     Full CI pipeline using GitHub Actions and JaCoCo
     Modular architecture with clear separation of concerns
     Real DB interactions + isolated test database for integration tests
     Next step: Migrate to a full-stack experience (JavaFX or React),
