# NexusLibrary Pro - Digital Library Management System

NexusLibrary Pro is a complete, enterprise-grade Digital Library Management System designed for the Oasis Infobyte Java Development Internship. Built on a modern tech stack utilizing Java 21, Spring Boot 3.3.x, SQLite, Spring Data JPA, Spring Security, Bootstrap 5.3, and Thymeleaf, this system is styled as a premium dark-themed administrative dashboard utilizing glassmorphic components and micro-animations.

This application is designed to be portfolio-ready, fully compiling, and equipped with a realistic database seeder (containing 50+ books, 3 members, and 1 admin), report export systems (CSV/PDF), dynamic fine calculations (₹10/day, fully configurable), and automated reservation expirations.

---

## 🚀 Tech Stack & Core Libraries

- **Core Runtime**: Java 21 (LTS)
- **Framework**: Spring Boot 3.3.1 (Spring Web, JPA, Security, Validation, Thymeleaf)
- **Database**: SQLite (Transactional file-backed DB with foreign key constraints enabled)
- **JPA Provider**: Hibernate 6 (utilizing Community SQLite Dialect)
- **UI/UX**: HTML5, Thymeleaf, Bootstrap 5.3 (Dark Theme custom stylesheet, glassmorphism, responsive navigation)
- **Graphics/Charts**: Chart.js (CDN) for animated analytical line graphs
- **Reporting Utilities**: OpenPDF (for stylized PDF report exports), OpenCSV-compatible streams
- **Testing**: JUnit 5, Mockito, Spring Security Test

---

## 🌟 Key Features

### 👤 Role-Based Portals

#### 1. Administrator Portal (`ROLE_ADMIN`)
- **Interactive Dashboard**: Real-time counters showing Total Books, Members, Books Issued, Overdue checkouts, Active Reservations, and Fines collected. Features an animated Chart.js line graph mapping monthly borrows and returns.
- **Jobs Trigger**: Manual button to synchronize overdue checks (calculate late days and fines) and cancel expired reservations.
- **Book Management**: Full CRUD operations to search, filter, catalog new books, edit shelf numbers, cover mock URLs, and adjust stock quantities.
- **Author & Category Management**: Dynamic dashboards to add, modify, or delete authors and shelf categories with structural constraint checks (e.g. preventing category deletion if books exist).
- **Member Directory**: Grid listing registered members, contact information, addresses, and button controls to suspend or activate accounts.
- **Request Approvals**: Inbox to review and approve/reject member borrow requests, return requests, and finalize transactions.
- **Reservations Fulfiller**: Inbox to review book reservations, approve holds (which decrements stock), and fulfill pick-up transactions.
- **Exports & Audits**: Dedicated reports section allowing administrators to download database dumps (Books Catalog CSV, Active Checkouts CSV) or a stylized system summary PDF.

#### 2. Member Portal (`ROLE_MEMBER`)
- **Reader Dashboard**: Overview of currently borrowed books, active reservations, accumulated fines, and a chronological history of their recent library interactions.
- **Searchable Catalog**: A 3-column responsive card catalog with instant keyword searches and genre filter sidebar widgets.
- **Book Details Sheet**: Dedicated pages showing descriptions, cover illustrations, publishers, shelf codes, availability counts, and contextual buttons that check reader relations.
- **Self-Checkout Requests**: One-click button to request a borrow (if book is in stock) or request a reservation hold (if book is out of stock).
- **Self-Return Requests**: Request return option from history log when checking out books.
- **Profile Configuration**: Form to edit phone numbers, addresses, and reset login passwords (authenticating current password first).

### 🛠️ Core Library Business Logic
- **Configurable Fine Rate**: Calculated dynamically during returns or batch updates at ₹10.00/day (configurable via `library.fine.rate-per-day` in `application.properties`).
- **Reservation Expirations**: Approved book holds are kept for a maximum of 3 days. If not picked up, the hold is automatically released, restoring book stock (duration is configurable via `library.reservation.expiry-days`).
- **Maximum Borrow Limit**: Enforces a strict limit of 5 active checkouts per member.
- **Suspension Lock**: Members with a status of `SUSPENDED` are blocked from placing new checkout requests or book holds.

---

## 📁 Project Directory Tree

```
DigitalLibraryManagementSystem/
├── database/                    # SQLite database directory
│   └── nexuslibrary.db          # Database file auto-generated on startup
├── docs/                        # Dedicated project documentation
│   ├── DEMO_SCRIPT.md           # Script for evaluator walkthrough
│   ├── PROJECT_EXPLANATION.md   # Architectural details & database schema
│   └── TEST_CASES.md            # Test cases and execution guide
├── screenshots/                 # App UI screenshots
│   ├── login.png
│   ├── dashboard.png
│   ├── books.png
│   ├── members.png
│   ├── issue.png
│   ├── return.png
│   └── reports.png
├── src/
│   ├── main/
│   │   ├── java/com/oasis/nexuslibrary/
│   │   │   ├── config/          # Security & SQL Dialect configs
│   │   │   ├── controller/      # Auth, Admin, Member, and Export Controllers
│   │   │   ├── dto/             # Data Transfer Objects with validation annotations
│   │   │   ├── entity/          # JPA Hibernate entities & relationship mappings
│   │   │   ├── exception/       # Custom exceptions & global HTTP error advice
│   │   │   ├── mapper/          # Lightweight Entity-DTO data mappers
│   │   │   ├── repository/      # Spring Data JPA Repository interfaces
│   │   │   ├── security/        # Custom UserDetailsService implementation
│   │   │   ├── service/         # Transactional service interfaces
│   │   │   │   └── impl/        # Core business logic implementations
│   │   │   └── util/            # DataSeeder, PDF/CSV generators
│   │   └── resources/
│   │       ├── static/css/      # Custom glassmorphism stylesheet (main.css)
│   │       ├── templates/       # Thymeleaf HTML views
│   │       │   ├── admin/       # Dashboard, books, members, approvals, reports
│   │       │   ├── error/       # Custom 403, 404, 500 error pages
│   │       │   ├── fragments/   # Navigation sidebar, styles, scripts, toast components
│   │       │   ├── member/      # Catalog, book details, history, profile views
│   │       │   └── login.html / register.html
│   │       └── application.properties # Server, database, security, and fine settings
│   └── test/                    # JUnit Mockito unit tests suite
├── pom.xml                      # Maven project descriptor
├── LICENSE                      # MIT License
└── README.md                    # Main readme documentation
```

---

## 🔑 Demo Accounts & Logins

The system database is seeded automatically on initial application boot.

| Role | Username (Email) | Password | Account Name |
| :--- | :--- | :--- | :--- |
| **Administrator** | `admin` | `admin123` | System Administrator |
| **Library Member** | `krish@mail.com` | `password123` | Krish Sharma |
| **Library Member** | `aarav@mail.com` | `password123` | Aarav Mehta |
| **Library Member** | `rohan@mail.com` | `password123` | Rohan Verma |

---

## ⚙️ Installation & Build Guide

### Prerequisites
- **Java JDK 21** or higher.
- **Maven** (bundled or installed).

### Steps to Run
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/yourusername/DigitalLibraryManagementSystem.git
   cd DigitalLibraryManagementSystem
   ```
2. **Build the Application**:
   Compile the classes and package them into an executable JAR:
   ```bash
   mvn clean package
   ```
3. **Execute the JAR**:
   Run the packaged application:
   ```bash
   java -jar target/nexuslibrary-1.0.0-SNAPSHOT.jar
   ```
   *Alternatively, run in developer mode:*
   ```bash
   mvn spring-boot:run
   ```
4. **Access the Portal**:
   Open a web browser and navigate to `http://localhost:8080`.

---

## 🔮 Future Improvements
- **Automatic Email Notifications**: Send automated reminders to members when their books are 3 days away from due dates or when fines accumulate.
- **RFID & Barcode Scan Integration**: Support barcode inputs on the admin books form to instantly log ISBNs and checkouts.
- **Payment Gateway Integration**: Integrate Razorpay or Stripe to allow readers to settle overdue fines directly inside their member profiles.

---

## 📄 License
This project is licensed under the **MIT License** - see the [LICENSE](file:///C:/Users/Asus/.gemini/antigravity/scratch/DigitalLibraryManagementSystem/LICENSE) file for details.

## ✍️ Author
**Krish Mathur**  
*Java Development Intern | Oasis Infobyte*
