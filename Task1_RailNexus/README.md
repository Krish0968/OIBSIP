# RailNexus: Smart Railway Reservation System

An elegant, secure, and modern Java desktop application built using Swing, SQLite, and FlatLaf, designed for the **Oasis Infobyte Java Development Internship** (Task 1 — Online Reservation System).

---

## Project Overview

### Problem Statement
Traditional railway booking demonstration systems often rely on command-line prompts or basic UI layouts with hardcoded credentials and database connections. This approach lacks the security of modern password hashing, fails to validate inputs properly (such as preventing booking past dates), and suffers from session leaks or cross-user ticket manipulation.

### Objectives
- Build a polished, original desktop booking dashboard with a modern dark theme interface.
- Implement robust security practices, including salted BCrypt password hashing and Parameterized PreparedStatements to block SQL Injection.
- Enforce business validations: prevent past date bookings, block identical source/destination routes, and secure ownership boundaries (preventing User A from cancelling User B's tickets).
- Create a portable, zero-configuration system utilizing SQLite that launches instantly after compilation.

---

## Key Features

1. **Branded Secure Login Form**
   - Clean sign-in page with active show/hide password toggle, clear form capability, and Enter-key submission.
   - Credentials validated against salted BCrypt hashes.
   - Pre-seeded test accounts highlighted on the interface for quick evaluation.

2. **Dashboard Overview**
   - Welcomes the authenticated user by name.
   - Dynamic metrics cards: Total, Confirmed, and Cancelled bookings.
   - Live JTable rendering the user's 5 most recent bookings.
   - Quick action navigation shortcuts.

3. **Train Ticket Booking**
   - Choice of realistic fictional Indian trains loaded dynamically from SQLite.
   - Selectable train numbers automatically populating route details (Train Name, Stations, Departure, and Arrival timings) as read-only.
   - Validation checks for empty fields and past travel dates.
   - Double-check review modal detailing itinerary before committing transaction.

4. **Unique PNR Generation**
   - Format: `RNX-YYYYMMDD-XXXXXX` (Prefix-Date-SecureRandom Suffix).
   - Collision retry loops checking database existence to ensure absolute uniqueness.

5. **My Bookings Manager**
   - Lists only the active user's tickets in a non-editable, sorted JTable.
   - Live text search filtering by PNR or Passenger Name.
   - Status filters (All, Confirmed, Cancelled).
   - Double-click to inspect departure times, routes, and booked timestamps.

6. **Ticket Cancellation Panel**
   - Look up reservation details by PNR.
   - Authorization bounds check: blocks attempts to fetch or cancel other users' tickets.
   - Soft cancellation updates ticket status to `'CANCELLED'` and timestamps the record without wiping historical audit logs.

---

## Tech Stack & Architecture

- **Language**: Java 21 (LTS)
- **GUI Framework**: Swing with **FlatLaf Dark Theme** (v3.4.1)
- **Database**: SQLite (via `sqlite-jdbc` v3.45.1.0)
- **Security**: BCrypt hashing (via `jbcrypt` v0.4)
- **Testing**: JUnit 5 for unit validation
- **Build System**: Maven

### Software Layering
```
Presentation (UI)  <--->  Service (Business Rules)  <--->  DAO (JDBC Queries)  <--->  SQLite
```

---

## Folder Structure

```
OnlineReservationSystem/
├── pom.xml
├── README.md
├── LICENSE
├── .gitignore
├── database/
│   ├── schema.sql
│   └── seed.sql
├── docs/
│   ├── DEMO_SCRIPT.md
│   ├── TEST_CASES.md
│   └── PROJECT_EXPLANATION.md
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── krish/
│   │   │           └── oibsip/
│   │   │               └── reservation/
│   │   │                   ├── Main.java
│   │   │                   ├── config/
│   │   │                   │   ├── DatabaseConnection.java
│   │   │                   │   └── DatabaseInitializer.java
│   │   │                   ├── model/
│   │   │                   │   ├── User.java
│   │   │                   │   ├── Train.java
│   │   │                   │   └── Reservation.java
│   │   │                   ├── dao/
│   │   │                   │   ├── UserDAO.java
│   │   │                   │   ├── TrainDAO.java
│   │   │                   │   └── ReservationDAO.java
│   │   │                   ├── service/
│   │   │                   │   ├── AuthService.java
│   │   │                   │   └── ReservationService.java
│   │   │                   ├── ui/
│   │   │                   │   ├── LoginFrame.java
│   │   │                   │   ├── MainFrame.java
│   │   │                   │   ├── DashboardPanel.java
│   │   │                   │   ├── ReservationPanel.java
│   │   │                   │   ├── MyBookingsPanel.java
│   │   │                   │   ├── CancellationPanel.java
│   │   │                   │   └── components/
│   │   │                   └── util/
│   │   │                       ├── PNRGenerator.java
│   │   │                       ├── PasswordUtil.java
│   │   │                       ├── ValidationUtil.java
│   │   │                       └── DateUtil.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/
│               └── krish/
│                   └── oibsip/
│                       └── reservation/
│                           ├── PNRGeneratorTest.java
│                           ├── ValidationUtilTest.java
│                           ├── PasswordUtilTest.java
│                           └── ServiceLayerTest.java
```

---

## Database Design

### Schema & Index Layout
1. **users**: Stores accounts. Password field holds only BCrypt hashes.
2. **trains**: Holds train schedules. Has unique index on `train_number`.
3. **reservations**: Stores bookings. Holds foreign keys referencing `users(id)` and `trains(id)`. Has index on `pnr` for fast cancellation searches, and `user_id` for list lookups.

---

## Installation & How to Run

### Prerequisites
- **JDK 21** or higher.
- **Apache Maven 3.x** installed.

### Step 1: Clone the Repository

Clone the OIBSIP repository and navigate to the Task 1 project directory:

```bash
git clone https://github.com/Krish0968/OIBSIP.git
cd OIBSIP/Task1_RailNexus
```

### Step 2: Clean & Compile Unit Tests
Run the Maven test suite to check validations, password hashing, and service limits:
```bash
mvn clean test
```

### Step 3: Package the Project
Package the application into an executable fat JAR containing all libraries (SQLite, FlatLaf, BCrypt):
```bash
mvn clean package
```

### Step 4: Run the Application
Start the compiled fat JAR:
```bash
java -jar target/railnexus-1.0.0-jar-with-dependencies.jar
```
*(Note: On first startup, the application creates the `database` folder and seeds `railnexus.db` automatically. You do not need to execute SQL scripts manually.)*

---

## Evaluation Credentials

Use these seeded credentials to log in:
- **User 1**: `krish` / password: `demo123`
- **User 2**: `passenger1` / password: `pass123`

---

## Testing & Quality Assurance
Run unit tests locally with `mvn clean test`. For manual verification:
1. Try invalid login combinations.
2. Book a ticket for tomorrow on train `1001`, confirm the generated PNR appears in "My Bookings".
3. Verify that logging in as `passenger1` throws access errors when trying to view or cancel the ticket created by `krish`.

---

## Security Considerations & Limitations
- **Local SQLite File**: The database file is stored locally under `database/railnexus.db`. This is ideal for lightweight offline portfolios but must be migrated to a standalone database (like PostgreSQL) for multiuser network production.
- **In-Memory Sessions**: Authentication states are stored in memory (`AuthService`). Closing the window terminates the session.
- **Seat Allotment**: The prototype handles itinerary ticketing but does not calculate physical seat maps or coach allocation.

---

## Internship Requirement Mapping

| Internship Requirement | Implementation Method | Where to Verify |
|---|---|---|
| **Login Form** | `LoginFrame.java` | Credentials checked against DB hashes; errors clear password inputs. |
| **BCrypt Password Hashing** | `PasswordUtil.java` | Standard BCrypt salts used; plain passwords never saved. |
| **Auto-population of Train details** | `ReservationPanel.java` | Selecting train numbers queries details and populates read-only fields. |
| **Unique PNR Generation** | `PNRGenerator.java` | Formats `RNX-YYYYMMDD-XXXXXX` and checks DB collision loops. |
| **Soft Cancellations** | `ReservationDAO.java` | Updates status to `CANCELLED` and logs `cancelled_at` instead of deleting. |
| **Security Bounds Check** | `ReservationService.java` | Enforces owner checks (`userId` matches session) before fetching/cancellation. |
| **Zero Configuration Setup** | `DatabaseInitializer.java` | Automatic folder generation and schema parsing on execution. |

---
## Author

**Krish**

Computer Science & Engineering Student at VIT  
Java Development Intern — Oasis Infobyte

- GitHub: Krish0968
