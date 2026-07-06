# Project Explanation: RailNexus

This document provides a comprehensive technical overview of **RailNexus**, the Smart Railway Reservation System, explaining the design decisions, architecture, implementation details, and interview preparation questions.

---

## Architectural & Technology Decisions

### 1. Why Java Swing was chosen?
Java Swing is the native, mature UI library bundled within the Java Standard Edition (SE). For a desktop project targeting educational internships, Swing offers:
- **Zero Heavy Tooling**: Runs natively on any OS with a JVM without installing complex, external native runtimes (unlike JavaFX, which requires specific platform modules).
- **Lightweight & Stable**: Very low footprint, git-friendly, and has a clean, straightforward thread execution model (the Event Dispatch Thread).
- **Customizability**: With modern library additions like **FlatLaf**, we can easily modernize Swing's look and feel to look clean, responsive, and state-of-the-art.

### 2. Why SQLite was chosen?
SQLite is a serverless, zero-configuration SQL database engine.
- **Embedded Database**: The database runs in-process, storing all data in a single local file (`database/railnexus.db`).
- **No Installation Needed**: The student or evaluator does not need to install, configure, or run a database server (like MySQL or PostgreSQL). Simply running the application creates and seeds the database automatically.
- **Full SQL Support**: Supports transactions, constraints, joins, indices, and foreign keys.

---

## Layered System Architecture

The project is structured under a clean **Layered Architecture**:

```
Presentation Layer (UI)  <--->  Service Layer  <--->  DAO Layer  <--->  JDBC / SQLite
```

1. **Presentation Layer (UI)**:
   - Formed by `LoginFrame`, `MainFrame`, and components (`DashboardPanel`, `ReservationPanel`, `MyBookingsPanel`, `CancellationPanel`).
   - Purely handles rendering user controls, listening for events, and displaying feedback.
   - It **never** contains business rules or directly executes SQL. It invokes the Service Layer.

2. **Service Layer**:
   - Class files: `AuthService`, `ReservationService`.
   - Orchestrates transactions, validates inputs using `ValidationUtil`, generates PNR codes, and checks authorization rules (e.g., verifying if the logged-in user owns the ticket before cancellation).

3. **Data Access Object (DAO) Layer**:
   - Class files: `UserDAO`, `TrainDAO`, `ReservationDAO`.
   - Responsible for raw database interactions (CRUD). Utilizes `DatabaseConnection` to execute queries using PreparedStatements.

4. **JDBC / SQLite Configuration**:
   - Configures the connection, enforces foreign key checks, and reads raw scripts from `schema.sql` and `seed.sql` to initialize database structures.

---

## Component Workflows

### 1. Database Initialization
At startup, `Main` executes `DatabaseInitializer.initializeDatabase()`.
- It queries the SQLite system tables for the existence of the `users` table.
- If missing, it reads the DDL statements from `database/schema.sql` and executes them using JDBC.
- It then executes statements from `database/seed.sql` to populate 10 fictional trains and two demo users with BCrypt password hashes.

### 2. Authentication (BCrypt)
- The user inputs credentials in `LoginFrame`.
- `AuthService` queries `UserDAO` to fetch the user by username.
- If the user is found, the entered password is verified against the stored hash using `PasswordUtil` (`BCrypt.checkpw()`).
- Storing only hashes ensures that even if the database is leaked, the plain passwords cannot be recovered.

### 3. PNR Generation
- Formatted as `RNX-YYYYMMDD-XXXXXX` (Prefix, Current Date, and 6-character random alphanumeric suffix).
- Generated securely in `PNRGenerator` using `SecureRandom`.
- To prevent collisions, the service queries `ReservationDAO::isPnrExists`. If a match is found, the system retries up to 10 times to secure absolute uniqueness.

### 4. Ticket Booking
- The passenger fills the booking form. Choosing a train number dynamically auto-populates all route details in read-only text fields.
- The UI runs validations (e.g., journey date cannot be in the past).
- A Review Dialog presents booking details.
- On confirmation, `ReservationService` generates a unique PNR, creates a new `Reservation` record, and inserts it into the SQLite database.

### 5. Ticket Cancellation (Soft Cancellation)
- The passenger submits their PNR.
- The Service layer queries the reservation.
- **Authorization Check**: The system validates that the ticket exists and matches the currently logged-in user. If a mismatch is detected, a `SecurityException` is thrown, blocking unauthorized deletion.
- If validation succeeds, `ReservationDAO` performs a **soft cancellation** by updating `booking_status` to `CANCELLED` and storing the cancellation timestamp, preserving audit history.

---

## 20 Viva/Interview Questions & Answers

1. **What is SQLite and why did you choose it over MySQL?**
   SQLite is a serverless, embedded SQL database engine. It stores data in a single local file. I chose it because it requires zero configuration, meaning the evaluator can run the project instantly without installing a MySQL server, while still offering standard SQL query support.

2. **How did you secure user passwords?**
   I used BCrypt password hashing via the `jbcrypt` library. Passwords are never stored as plain text. Instead, we generate a one-way salted hash at user creation and verify it during login using `BCrypt.checkpw()`.

3. **Why did you use PreparedStatements instead of Statements?**
   PreparedStatements pre-compile SQL queries, substituting user input using placeholders (`?`). This prevents SQL injection attacks, secures database syntax parsing, and improves execution performance.

4. **What layout managers did you use in Swing?**
   I avoided null layouts and instead used `BorderLayout` for main panels, `GridBagLayout` for form alignments, `GridLayout` for structured grids (like cards), and `BoxLayout` for the sidebar list.

5. **How did you handle the screen transitions without opening duplicate windows?**
   I used a single main `JFrame` (`MainFrame`) containing a `CardLayout` container panel. Clicking sidebar navigation items switches the visible cards dynamically on the same frame.

6. **What is FlatLaf and why did you use it?**
   FlatLaf is a modern open-source Look and Feel library for Java Swing. I used it to replace the outdated Java Metal theme with a professional Dark theme that matches modern UI/UX design standards.

7. **How does your database handle data integrity?**
   It defines explicit constraints: Primary Keys, unique indexes on PNR and usernames, NOT NULL flags, and Foreign Keys on `user_id` and `train_id`. I explicitly enable foreign keys by running `PRAGMA foreign_keys = ON;` upon opening every SQLite connection.

8. **Explain the PNR generation format and how you ensure uniqueness.**
   The PNR format is `RNX-YYYYMMDD-XXXXXX`. The date segment represents the booking date, and the suffix is a 6-character random alphanumeric string. Uniqueness is guaranteed by running a database check on generation; if the PNR already exists, it retries up to 10 times.

9. **What is a "Soft Cancellation" and why is it preferred over deletion?**
   Soft cancellation updates the ticket's `booking_status` field to `'CANCELLED'` and records a timestamp instead of deleting the row. This preserves historical transactions, audit trails, and reporting accuracy.

10. **How did you prevent one user from cancelling another user's ticket?**
    In `ReservationService`, the cancellation routine checks if the reservation's `userId` matches the ID of the currently logged-in user session. If they do not match, it throws a `SecurityException`.

11. **Why do you use SwingWorker for login and booking actions?**
    Swing is single-threaded. Running heavy database queries on the Event Dispatch Thread (EDT) freezes the UI. `SwingWorker` executes database operations in a background thread and updates the UI on completion.

12. **How did you configure train auto-population?**
    The train selection JComboBox is populated with `Train` objects. Selecting a train triggers an event listener that reads the selected train's source, destination, departure, and arrival times, and populates them into read-only text fields.

13. **How does your project read configuration settings?**
    It reads settings from `application.properties` on the classpath. This lets us customize parameters like the database URL without modifying the Java source code.

14. **What unit testing framework did you use and what was tested?**
    I used JUnit 5. I wrote tests for PNR structure, validation rules (past dates, matching stations, empty names), password hashing verification, and service authorization boundaries.

15. **How do you prevent a ticket from being cancelled multiple times?**
    Before executing cancellation, the service layer checks the reservation status. If it is already `'CANCELLED'`, it throws an `IllegalArgumentException`, preventing duplicate cancellation.

16. **Why did you use java.time instead of java.util.Date for core logic?**
    The modern `java.time` API (specifically `LocalDate` and `LocalDateTime`) is immutable, thread-safe, and provides clearer methods for operations like comparing travel dates against today's date.

17. **How did you handle database exceptions without showing raw stack traces?**
    I caught `SQLException` at the UI layer, logging details to standard error, and displaying clean, user-friendly warnings in a dialog box (e.g. "Database Connection Error" instead of SQLite stack traces).

18. **What does `PRAGMA foreign_keys = ON;` do?**
    In SQLite, foreign key enforcement is turned off by default for backwards compatibility. Running this command on connection startup ensures SQLite actively rejects orphan references.

19. **How does Maven package this project?**
    Maven compiles the source, runs unit tests, and uses the `maven-assembly-plugin` to bundle the compiled bytecode and dependencies (SQLite JDBC, FlatLaf, BCrypt) into a single executable Fat JAR.

20. **If you wanted to scale this to support real-world bookings, what would you change?**
    I would migrate the database to PostgreSQL or MySQL, replace Swing with a RESTful Web Service (e.g. Spring Boot), add concurrent seats booking logic (managing race conditions with locking), and implement a real-time payment gateway integration.
